/*
 * Copyright 2011, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */
package com.allanbank.mongodb.bson.element;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.Visitor;

/**
 * A wrapper for a BSON maximum key element.
 * 
 * @copyright 2011, Allanbank Consulting, Inc., All Rights Reserved
 */
public class MaxKeyElement extends AbstractElement {

	/** The BSON type for a binary. */
	public static final ElementType TYPE = ElementType.MAX_KEY;

	/**
	 * Constructs a new {@link MaxKeyElement}.
	 * 
	 * @param name
	 *            The name for the BSON maximum key.
	 */
	public MaxKeyElement(String name) {
		super(TYPE, name);
	}

	/**
	 * Accepts the visitor and calls the {@link Visitor#visitMaxKey} method.
	 * 
	 * @see Element#accept(Visitor)
	 */
	@Override
	public void accept(Visitor visitor) {
		visitor.visitMaxKey(getName());
	}

	/**
	 * Computes a reasonable hash code.
	 * 
	 * @return The hash code value.
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Determines if the passed object is of this same type as this object and
	 * if so that its fields are equal.
	 * 
	 * @param object
	 *            The object to compare to.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (this == object) {
			result = true;
		} else if ((object != null) && (getClass() == object.getClass())) {
			result = super.equals(object);
		}
		return result;
	}

	/**
	 * String form of the object.
	 * 
	 * @return A human readable form of the object.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append('"');
		builder.append(getName());
		builder.append("\" : /* MAX_KEY */ ");
		builder.append(Long.MAX_VALUE);

		return builder.toString();
	}
}
