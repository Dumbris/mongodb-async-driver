/*
 * Copyright 2011, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */
package com.allanbank.mongodb.bson.builder.impl;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.builder.ArrayBuilder;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.allanbank.mongodb.bson.element.BinaryElement;
import com.allanbank.mongodb.bson.element.BooleanElement;
import com.allanbank.mongodb.bson.element.DoubleElement;
import com.allanbank.mongodb.bson.element.IntegerElement;
import com.allanbank.mongodb.bson.element.JavaScriptElement;
import com.allanbank.mongodb.bson.element.JavaScriptWithScopeElement;
import com.allanbank.mongodb.bson.element.LongElement;
import com.allanbank.mongodb.bson.element.MaxKeyElement;
import com.allanbank.mongodb.bson.element.MinKeyElement;
import com.allanbank.mongodb.bson.element.MongoTimestampElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.ObjectIdElement;
import com.allanbank.mongodb.bson.element.RegularExpressionElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.allanbank.mongodb.bson.element.TimestampElement;

/**
 * A builder for BSON arrays.
 * 
 * @copyright 2011, Allanbank Consulting, Inc., All Rights Reserved
 */
public class ArrayBuilderImpl extends AbstractBuilder implements ArrayBuilder {

	/**
	 * Creates a new {@link ArrayBuilderImpl}.
	 */
	public ArrayBuilderImpl() {
		this(null);
	}

	/**
	 * Creates a new {@link ArrayBuilderImpl}.
	 * 
	 * @param outerBuilder
	 *            The outer builder scope.
	 */
	public ArrayBuilderImpl(AbstractBuilder outerBuilder) {
		super(outerBuilder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addBinary(byte[] value) {
		myElements.add(new BinaryElement(nextIndex(), (byte) 0, value));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addBinary(byte subType, byte[] value) {
		myElements.add(new BinaryElement(nextIndex(), subType, value));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addBoolean(boolean value) {
		myElements.add(new BooleanElement(nextIndex(), value));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public ArrayBuilder addDBPointer(int timestamp, long machineId) {
		myElements.add(new com.allanbank.mongodb.bson.element.DBPointerElement(
				nextIndex(), timestamp, machineId));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addDouble(double value) {
		myElements.add(new DoubleElement(nextIndex(), value));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addInteger(int value) {
		myElements.add(new IntegerElement(nextIndex(), value));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addJavaScript(String code) {
		myElements.add(new JavaScriptElement(nextIndex(), code));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addJavaScript(String code, Document scope) {
		myElements
				.add(new JavaScriptWithScopeElement(nextIndex(), code, scope));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addLong(long value) {
		myElements.add(new LongElement(nextIndex(), value));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addMaxKey() {
		myElements.add(new MaxKeyElement(nextIndex()));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addMinKey() {
		myElements.add(new MinKeyElement(nextIndex()));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addMongoTimestamp(long value) {
		myElements.add(new MongoTimestampElement(nextIndex(), value));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addNull() {
		myElements.add(new NullElement(nextIndex()));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addObjectId(int timestamp, long machineId) {
		myElements.add(new ObjectIdElement(nextIndex(), timestamp, machineId));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addRegularExpression(String pattern, String options) {
		myElements.add(new RegularExpressionElement(nextIndex(), pattern,
				options));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addString(String value) {
		myElements.add(new StringElement(nextIndex(), value));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addSymbol(String symbol) {
		myElements.add(new SymbolElement(nextIndex(), symbol));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder addTimestamp(long timestamp) {
		myElements.add(new TimestampElement(nextIndex(), timestamp));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentBuilder push() {
		return doPush(nextIndex());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayBuilder pushArray() {
		return doPushArray(nextIndex());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to return an {@link ArrayElement}.
	 * </p>
	 */
	@Override
	protected Element get(String name) {
		return new ArrayElement(name, subElements());
	}

	/**
	 * Returns the next index value for an element.
	 * 
	 * @return The next index value for an element.
	 */
	private String nextIndex() {
		return String.valueOf(myElements.size());
	}
}