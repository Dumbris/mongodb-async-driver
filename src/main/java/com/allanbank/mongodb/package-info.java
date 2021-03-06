/*
 * #%L
 * package-info.java - mongodb-async-driver - Allanbank Consulting, Inc.
 * %%
 * Copyright (C) 2011 - 2014 Allanbank Consulting, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/**
 * Provides a Java driver for the MongoDB document store that allows asynchronous invocation of requests.
 *
 * <h2>Package Layout / High Level Design</h2>
 * <p>
 * The <tt>com.allanbank.mongodb</tt> package contains the interfaces and basic classes that the client code
 * will use to interact with the driver.
 * </p>
 * <p>
 * The <a href="bson/package-summary.html">com.allanbank.mongodb.bson</a> package contains a strongly typed,
 * immutable implementation of the <a href="http://bsonspec.org/">BSON Specification</a>.
 * </p>
 * <p>
 * In the <a href="client/package-summary.html">com.allanbank.mongodb.client</a> package are the implementation
 * of the primary interfaces for the driver and support classes to convert
 * {@link com.allanbank.mongodb.client.message.Reply}(s) from the server into a more user friendly format.
 * </p>
 * <p>
 * To facilitate constructing queries and the more complex commands to the MongoDB servers a set
 * of support classes are provided in the <a href="builder/package-summary.html">com.allanbank.mongodb.builder</a>
 * package.
 * </p>
 * <p>
 * Exceptions (all inheriting from {@link com.allanbank.mongodb.MongoDbException} are located in the
 * <a href="error/package-summary.html">com.allanbank.mongodb.error</a> package.
 * </p>
 *
 * @copyright 2012-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
package com.allanbank.mongodb;

