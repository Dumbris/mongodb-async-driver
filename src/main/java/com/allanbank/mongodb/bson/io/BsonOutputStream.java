/*
 * #%L
 * BsonOutputStream.java - mongodb-async-driver - Allanbank Consulting, Inc.
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
package com.allanbank.mongodb.bson.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.annotation.concurrent.NotThreadSafe;

import com.allanbank.mongodb.bson.Document;

/**
 * A wrapper for an {@link OutputStream} to handle writing BSON primitives.
 *
 * @api.yes This class is part of the driver's API. Public and protected members
 *          will be deprecated for at least 1 non-bugfix release (version
 *          numbers are &lt;major&gt;.&lt;minor&gt;.&lt;bugfix&gt;) before being
 *          removed or modified.
 * @copyright 2011-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
@NotThreadSafe
public class BsonOutputStream {

    /** UTF-8 Character set for encoding strings. */
    public final static Charset UTF8 = StringDecoder.UTF8;

    /** Any thrown exceptions. */
    protected IOException myError;

    /** Output buffer for spooling the written document. */
    protected final OutputStream myOutput;

    /** The encoder for strings. */
    protected final StringEncoder myStringEncoder;

    /** The visitor for writing BSON documents. */
    protected final WriteVisitor myWriteVisitor;

    /**
     * Creates a new {@link BsonOutputStream}.
     *
     * @param output
     *            The underlying Stream to write to.
     */
    public BsonOutputStream(final OutputStream output) {
        this(output, new StringEncoderCache());
    }

    /**
     * Creates a new {@link BsonOutputStream}.
     *
     * @param output
     *            The underlying Stream to write to.
     * @param cache
     *            The cache for encoding string.
     */
    public BsonOutputStream(final OutputStream output,
            final StringEncoderCache cache) {
        myOutput = output;
        myStringEncoder = new StringEncoder(cache);
        myWriteVisitor = new WriteVisitor(this);
    }

    /**
     * Returns the I/O exception encountered by the visitor.
     *
     * @return The I/O exception encountered by the visitor.
     */
    public IOException getError() {
        return myError;
    }

    /**
     * Returns the maximum number of strings that may have their encoded form
     * cached.
     *
     * @return The maximum number of strings that may have their encoded form
     *         cached.
     * @deprecated The cache {@link StringEncoderCache} should be controlled
     *             directory. This method will be removed after the 2.1.0
     *             release.
     */
    @Deprecated
    public int getMaxCachedStringEntries() {
        return myStringEncoder.getCache().getMaxCacheEntries();
    }

    /**
     * Returns the maximum length for a string that the stream is allowed to
     * cache.
     *
     * @return The maximum length for a string that the stream is allowed to
     *         cache.
     * @deprecated The cache {@link StringDecoderCache} should be controlled
     *             directory. This method will be removed after the 2.1.0
     *             release.
     */
    @Deprecated
    public int getMaxCachedStringLength() {
        return myStringEncoder.getCache().getMaxCacheLength();
    }

    /**
     * Returns the encoder value.
     *
     * @return The encoder value.
     */
    public StringEncoder getStringEncoder() {
        return myStringEncoder;
    }

    /**
     * Returns true if the visitor had an I/O error.
     *
     * @return True if the visitor had an I/O error, false otherwise.
     */
    public boolean hasError() {
        return (myError != null);
    }

    /**
     * Clears any errors.
     */
    public void reset() {
        myError = null;
    }

    /**
     * Sets the value of maximum number of strings that may have their encoded
     * form cached.
     *
     * @param maxCacheEntries
     *            The new value for the maximum number of strings that may have
     *            their encoded form cached.
     * @deprecated The cache {@link StringEncoderCache} should be controlled
     *             directory. This method will be removed after the 2.1.0
     *             release.
     */
    @Deprecated
    public void setMaxCachedStringEntries(final int maxCacheEntries) {
        myStringEncoder.getCache().setMaxCacheEntries(maxCacheEntries);
    }

    /**
     * Sets the value of length for a string that the stream is allowed to cache
     * to the new value. This can be used to stop a single long string from
     * pushing useful values out of the cache.
     *
     * @param maxlength
     *            The new value for the length for a string that the encoder is
     *            allowed to cache.
     * @deprecated The cache {@link StringEncoderCache} should be controlled
     *             directory. This method will be removed after the 2.1.0
     *             release.
     */
    @Deprecated
    public void setMaxCachedStringLength(final int maxlength) {
        myStringEncoder.getCache().setMaxCacheLength(maxlength);

    }

    /**
     * Returns the size of the writing the {@link Document} as a BSON document.
     *
     * @param document
     *            The document to determine the size of.
     * @return The size of the writing {@link Document} as a BSON document.
     * @deprecated Replaced with {@link Document#size()}. This method will be
     *             removed after the 2.2.0 release.
     */
    @Deprecated
    public int sizeOf(final Document document) {
        return (int) document.size();
    }

    /**
     * Returns the size of the writing the <tt>strings</tt> as a c-string.
     *
     * @param strings
     *            The 'C' strings to determine the size of.
     * @return The size of the writing the <tt>strings</tt> as a c-string.
     */
    public int sizeOfCString(final String... strings) {
        int size = 0;
        for (final String string : strings) {
            size += myWriteVisitor.utf8Size(string);
        }
        return (size + 1);
    }

    /**
     * Returns the size of the writing the <tt>string</tt> as a string.
     *
     * @param string
     *            The 'UTF8' string to determine the size of.
     * @return The size of the writing the <tt>string</tt> as a string.
     */
    public int sizeOfString(final String string) {
        return 4 + myWriteVisitor.utf8Size(string) + 1;
    }

    /**
     * Writes a single byte to the stream.
     *
     * @param b
     *            The byte to write.
     */
    public void writeByte(final byte b) {
        try {
            myOutput.write(b);
        }
        catch (final IOException ioe) {
            myError = ioe;
        }
    }

    /**
     * Writes a sequence of bytes to the under lying stream.
     *
     * @param data
     *            The bytes to write.
     */
    public void writeBytes(final byte[] data) {
        try {
            myOutput.write(data);
        }
        catch (final IOException ioe) {
            myError = ioe;
        }
    }

    /**
     * Writes a "Cstring" to the stream.
     *
     * @param strings
     *            The CString to write. The strings are concatenated into a
     *            single CString value.
     */
    public void writeCString(final String... strings) {
        for (final String string : strings) {
            writeUtf8(string);
        }
        writeByte((byte) 0);
    }

    /**
     * Writes a BSON {@link Document} to the stream.
     *
     * @param document
     *            The {@link Document} to write.
     * @throws IOException
     *             On a failure writing the document.
     */
    public void writeDocument(final Document document) throws IOException {
        try {
            document.accept(myWriteVisitor);
            if (myWriteVisitor.hasError()) {
                throw myWriteVisitor.getError();
            }
        }
        finally {
            myWriteVisitor.reset();
        }
    }

    /**
     * Write the integer value in little-endian byte order.
     *
     * @param value
     *            The integer to write.
     */
    public void writeInt(final int value) {
        try {
            myOutput.write(value);
            myOutput.write(value >> 8);
            myOutput.write(value >> 16);
            myOutput.write(value >> 24);
        }
        catch (final IOException ioe) {
            myError = ioe;
        }
    }

    /**
     * Write the long value in little-endian byte order.
     *
     * @param value
     *            The long to write.
     */
    public void writeLong(final long value) {
        try {
            myOutput.write((int) value);
            myOutput.write((int) (value >> 8));
            myOutput.write((int) (value >> 16));
            myOutput.write((int) (value >> 24));
            myOutput.write((int) (value >> 32));
            myOutput.write((int) (value >> 40));
            myOutput.write((int) (value >> 48));
            myOutput.write((int) (value >> 56));
        }
        catch (final IOException ioe) {
            myError = ioe;
        }
    }

    /**
     * Writes a "string" to the stream.
     *
     * @param string
     *            The String to write.
     */
    public void writeString(final String string) {
        writeInt(myStringEncoder.encodeSize(string) + 1);
        writeUtf8(string);
        writeByte((byte) 0);
    }

    /**
     * Writes a sequence of bytes to the under lying stream.
     *
     * @param data
     *            The bytes to write.
     * @param offset
     *            The offset into the buffer to start writing data from.
     * @param length
     *            The number of bytes to write.
     */
    protected void writeBytes(final byte[] data, final int offset,
            final int length) {
        try {
            myOutput.write(data, offset, length);
        }
        catch (final IOException ioe) {
            myError = ioe;
        }
    }

    /**
     * Writes the string as a UTF-8 string. This method handles the
     * "normal/easy" cases and delegates to the full character set if things get
     * complicated.
     *
     * @param string
     *            The string to encode.
     */
    protected void writeUtf8(final String string) {
        try {
            myStringEncoder.encode(string, myOutput);
        }
        catch (final IOException ioe) {
            myError = ioe;
        }
    }

}
