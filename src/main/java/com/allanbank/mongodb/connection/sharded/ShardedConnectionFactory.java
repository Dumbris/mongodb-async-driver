/*
 * Copyright 2011-2013, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */
package com.allanbank.mongodb.connection.sharded;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoDbException;
import com.allanbank.mongodb.ReadPreference;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.connection.ClusterType;
import com.allanbank.mongodb.connection.Connection;
import com.allanbank.mongodb.connection.ConnectionFactory;
import com.allanbank.mongodb.connection.FutureCallback;
import com.allanbank.mongodb.connection.ReconnectStrategy;
import com.allanbank.mongodb.connection.message.Query;
import com.allanbank.mongodb.connection.message.Reply;
import com.allanbank.mongodb.connection.proxy.ProxiedConnectionFactory;
import com.allanbank.mongodb.connection.state.Cluster;
import com.allanbank.mongodb.connection.state.ClusterPinger;
import com.allanbank.mongodb.connection.state.LatencyServerSelector;
import com.allanbank.mongodb.connection.state.Server;
import com.allanbank.mongodb.connection.state.ServerSelector;
import com.allanbank.mongodb.util.IOUtils;

/**
 * Provides the ability to create connections to a shard configuration via
 * mongos servers.
 * 
 * @api.no This class is <b>NOT</b> part of the drivers API. This class may be
 *         mutated in incompatible ways between any two releases of the driver.
 * @copyright 2011-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class ShardedConnectionFactory implements ConnectionFactory {

    /** The logger for the {@link ShardedConnectionFactory}. */
    protected static final Logger LOG = Logger
            .getLogger(ShardedConnectionFactory.class.getCanonicalName());

    /** The factory to create proxied connections. */
    protected final ProxiedConnectionFactory myConnectionFactory;

    /** The state of the cluster. */
    private final Cluster myCluster;

    /** The MongoDB client configuration. */
    private final MongoClientConfiguration myConfig;

    /** Pings the servers in the cluster collecting latency and tags. */
    private final ClusterPinger myPinger;

    /** The slector for the mongos instance to use. */
    private final ServerSelector mySelector;

    /**
     * Creates a new {@link ShardedConnectionFactory}.
     * 
     * @param factory
     *            The factory to create proxied connections.
     * @param config
     *            The initial configuration.
     */
    public ShardedConnectionFactory(final ProxiedConnectionFactory factory,
            final MongoClientConfiguration config) {
        myConnectionFactory = factory;
        myConfig = config;
        myCluster = new Cluster(config);
        mySelector = new LatencyServerSelector(myCluster, true);
        myPinger = new ClusterPinger(myCluster, ClusterType.SHARDED, factory,
                config);

        // Add all of the servers to the cluster.
        for (final InetSocketAddress address : config.getServerAddresses()) {
            myCluster.add(address);
        }

        bootstrap();
    }

    /**
     * Finds the mongos servers.
     * <p>
     * Performs a find on the <tt>config</tt> database's <tt>mongos</tt>
     * collection to return the id for all of the mongos servers in the cluster.
     * </p>
     * <p>
     * A single mongos entry looks like: <blockquote>
     * 
     * <pre>
     * <code>
     * { 
     *     "_id" : "mongos.example.com:27017", 
     *     "ping" : ISODate("2011-12-05T23:54:03.122Z"), 
     *     "up" : 330 
     * }
     * </code>
     * </pre>
     * 
     * </blockquote>
     */
    public void bootstrap() {
        if (myConfig.isAutoDiscoverServers()) {
            // Create a query to pull all of the mongos servers out of the
            // config database.
            final Query query = new Query("config", "mongos", BuilderFactory
                    .start().build(), /* fields= */null, /* batchSize= */0,
            /* limit= */0, /* numberToSkip= */0, /* tailable= */false,
                    ReadPreference.PRIMARY, /* noCursorTimeout= */false,
                    /* awaitData= */false, /* exhaust= */false, /* partial= */
                    false);

            for (final String addr : myConfig.getServers()) {
                Connection conn = null;
                final FutureCallback<Reply> future = new FutureCallback<Reply>();
                try {
                    // Send the request...
                    conn = myConnectionFactory.connect(myCluster.add(addr),
                            myConfig);
                    conn.send(query, future);

                    // Receive the response.
                    final Reply reply = future.get();

                    // Validate and pull out the response information.
                    final List<Document> docs = reply.getResults();
                    for (final Document doc : docs) {
                        final Element idElem = doc.get("_id");
                        if (idElem instanceof StringElement) {
                            final StringElement id = (StringElement) idElem;

                            myCluster.add(id.getValue());

                            LOG.fine("Adding shard mongos: " + id.getValue());
                        }
                    }
                }
                catch (final IOException ioe) {
                    LOG.log(Level.WARNING,
                            "I/O error during sharded bootstrap to " + addr
                                    + ".", ioe);
                }
                catch (final MongoDbException me) {
                    LOG.log(Level.WARNING,
                            "MongoDB error during sharded bootstrap to " + addr
                                    + ".", me);
                }
                catch (final InterruptedException e) {
                    LOG.log(Level.WARNING,
                            "Interrupted during sharded bootstrap to " + addr
                                    + ".", e);
                }
                catch (final ExecutionException e) {
                    LOG.log(Level.WARNING, "Error during sharded bootstrap to "
                            + addr + ".", e);
                }
                finally {
                    IOUtils.close(conn, Level.WARNING,
                            "I/O error shutting down sharded bootstrap connection to "
                                    + addr + ".");
                }
            }
        }

        // Last thing is to get the status of each server we discovered.
        myPinger.initialSweep();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to close the cluster state and the
     * {@link ProxiedConnectionFactory}.
     * </p>
     */
    @Override
    public void close() {
        IOUtils.close(myPinger);
        IOUtils.close(myConnectionFactory);
    }

    /**
     * Creates a new connection to the shared mongos servers.
     * 
     * @see ConnectionFactory#connect()
     */
    @Override
    public Connection connect() throws IOException {
        IOException lastError = null;
        for (final Server primary : myCluster.getWritableServers()) {
            try {
                final Connection primaryConn = myConnectionFactory.connect(
                        primary, myConfig);

                return new ShardedConnection(primaryConn, myConfig);
            }
            catch (final IOException e) {
                lastError = e;
            }
        }

        if (lastError != null) {
            throw lastError;
        }

        throw new IOException(
                "Could not determine a shard server to connect to.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return {@link ClusterType#SHARDED} cluster type.
     * </p>
     */
    @Override
    public ClusterType getClusterType() {
        return ClusterType.SHARDED;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the delegates strategy but replace his state and
     * selector with our own.
     * </p>
     */
    @Override
    public ReconnectStrategy getReconnectStrategy() {
        final ReconnectStrategy delegates = myConnectionFactory
                .getReconnectStrategy();

        delegates.setState(myCluster);
        delegates.setSelector(mySelector);
        delegates.setConnectionFactory(myConnectionFactory);

        return delegates;
    }

    /**
     * Returns the clusterState value.
     * 
     * @return The clusterState value.
     */
    protected Cluster getCluster() {
        return myCluster;
    }
}
