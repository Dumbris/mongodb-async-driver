/*
 * Copyright 2013, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */
package com.allanbank.mongodb;

import java.beans.PropertyEditorSupport;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.beans.PropertyEditor} for the {@link Durability} class.
 * <p>
 * The string value must be one of the following tokens or parsable by
 * {@link Durability#valueOf(String)} method.
 * </p>
 * <p>
 * Valid tokens are:
 * <ul>
 * <li>NONE</li>
 * <li>ACK</li>
 * <li>FSYNC</li>
 * <li>JOURNAL</li>
 * <li>MAJORITY</li>
 * </ul>
 * </p>
 * <p>
 * {@code FSYNC}, {@code JOURNAL}, and {@code MAJORITY} all use the
 * {@link #DEFAULT_WAIT_TIME_MS} for the wait time.
 * </p>
 * 
 * @api.yes This class is part of the driver's API. Public and protected members
 *          will be deprecated for at least 1 non-bugfix release (version
 *          numbers are &lt;major&gt;.&lt;minor&gt;.&lt;bugfix&gt;) before being
 *          removed or modified.
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class DurabilityEditor extends PropertyEditorSupport {

    /** The default wait time for tokenized durabilities: {@value} ms. */
    public static final int DEFAULT_WAIT_TIME_MS = (int) TimeUnit.SECONDS
            .toMillis(30);

    /**
     * Creates a new DurabilityEditor.
     */
    public DurabilityEditor() {
        super();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to parse a string to a {@link Durability}.
     * </p>
     * 
     * @throws IllegalArgumentException
     *             If the string cannot be parsed into a {@link Durability}.
     */
    @Override
    public void setAsText(final String durabilityString)
            throws IllegalArgumentException {
        if ("NONE".equalsIgnoreCase(durabilityString)) {
            setValue(Durability.NONE);
        }
        else if ("ACK".equalsIgnoreCase(durabilityString)) {
            setValue(Durability.ACK);
        }
        else if ("FSYNC".equalsIgnoreCase(durabilityString)) {
            setValue(Durability.fsyncDurable(DEFAULT_WAIT_TIME_MS));
        }
        else if ("JOURNAL".equalsIgnoreCase(durabilityString)) {
            setValue(Durability.journalDurable(DEFAULT_WAIT_TIME_MS));
        }
        else if ("MAJORITY".equalsIgnoreCase(durabilityString)) {
            setValue(Durability.replicaDurable(Durability.MAJORITY_MODE,
                    DEFAULT_WAIT_TIME_MS));
        }
        else {
            final Durability durability = Durability.valueOf(durabilityString);
            if (durability != null) {
                setValue(durability);
            }
            else {
                throw new IllegalArgumentException(
                        "Could not determine the durability for '"
                                + durabilityString + "'.");
            }
        }
    }
}