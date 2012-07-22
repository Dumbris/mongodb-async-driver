/*
 * Copyright 2012, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.builder;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.ArrayBuilder;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.allanbank.mongodb.bson.element.BinaryElement;
import com.allanbank.mongodb.bson.element.BooleanElement;
import com.allanbank.mongodb.bson.element.DocumentElement;
import com.allanbank.mongodb.bson.element.DoubleElement;
import com.allanbank.mongodb.bson.element.IntegerElement;
import com.allanbank.mongodb.bson.element.JavaScriptElement;
import com.allanbank.mongodb.bson.element.JavaScriptWithScopeElement;
import com.allanbank.mongodb.bson.element.LongElement;
import com.allanbank.mongodb.bson.element.MaxKeyElement;
import com.allanbank.mongodb.bson.element.MinKeyElement;
import com.allanbank.mongodb.bson.element.MongoTimestampElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.ObjectId;
import com.allanbank.mongodb.bson.element.ObjectIdElement;
import com.allanbank.mongodb.bson.element.RegularExpressionElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.allanbank.mongodb.bson.element.TimestampElement;

/**
 * ConditionBuilder provides tracking for the condition of a single field within
 * a query.
 * <p>
 * Use the {@link QueryBuilder#where(String)} method to create a
 * {@link ConditionBuilder}.
 * </p>
 * 
 * @see QueryBuilder#whereField(String)
 * 
 * @copyright 2012, Allanbank Consulting, Inc., All Rights Reserved
 */
public class ConditionBuilder implements DocumentAssignable {

    /** The equals element. */
    private Element myEqualsComparison;

    /** The name of the field to compare. */
    private final String myFieldName;

    /** The non-equal comparisons. */
    private final Map<Operator, Element> myOtherComparisons;

    /** The parent builder for the condition. */
    private final QueryBuilder myParent;

    /**
     * Creates a new ConditionBuilder.
     * <p>
     * This constructor is protected since generally users will use the
     * {@link QueryBuilder} class to create a condition builder.
     * </p>
     * 
     * @param fieldName
     *            The name for the field to compare.
     * @param parent
     *            The parent builder for this condition.
     */
    protected ConditionBuilder(final String fieldName, final QueryBuilder parent) {
        myFieldName = fieldName;
        myParent = parent;

        myOtherComparisons = new LinkedHashMap<Operator, Element>();
    }

    /**
     * Specify the values that must <em>all</em> be in the fields array.
     * <p>
     * Only a single {@link #all(ArrayBuilder)} comparison can be used. Calling
     * multiple <tt>all(...)</tt> methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param elements
     *            A builder for the values for the comparison. Any changes to
     *            the {@link ArrayBuilder} after this method is called are not
     *            reflected in the comparison.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder all(final ArrayBuilder elements) {
        return all(elements.build());
    }

    /**
     * Specify the values that must <em>all</em> be in the fields array.
     * <p>
     * Only a single {@link #all(Element[])} comparison can be used. Calling
     * multiple <tt>all(...)</tt> methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param elements
     *            The element values for the comparison.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder all(final Element... elements) {
        myEqualsComparison = null;
        myOtherComparisons.put(MiscellaneousOperator.ALL, new ArrayElement(
                MiscellaneousOperator.ALL.getToken(), elements));

        return this;
    }

    /**
     * Starts a logical conjunction with this condition builder. If the
     * <tt>fieldName</tt> is equal to this builder's {@link #getFieldName()
     * field name} then this builder will be returned. Otherwise a different
     * builder will be returned sharing the same parent {@link QueryBuilder}.
     * 
     * @param fieldName
     *            The name of the field to create a conjunction with.
     * @return The {@link ConditionBuilder} to use to construct the conjunction.
     */
    public ConditionBuilder and(final String fieldName) {
        return myParent.whereField(fieldName);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the result of {@link #build()}.
     * </p>
     * 
     * @see #build()
     */
    @Override
    public Document asDocument() {
        return build();
    }

    /**
     * Returns the results of building the parent {@link QueryBuilder}.
     * 
     * @return The results of building the parent {@link QueryBuilder}.
     * 
     * @see QueryBuilder#build()
     */
    public Document build() {
        return myParent.build();
    }

    /**
     * Query to match a single element in the array field.
     * <p>
     * Only a single {@link #elementMatches(QueryBuilder)} comparison can be
     * used. Calling multiple <tt>elementMatches(...)</tt> methods overwrites
     * previous values. In addition any {@link #equals(boolean) equals(...)}
     * condition is removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param arrayElementQuery
     *            A builder for the query to match a sub element. Any changes to
     *            the {@link QueryBuilder} after this method is called are not
     *            reflected in the comparison.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder elementMatches(final Document arrayElementQuery) {
        myEqualsComparison = null;
        myOtherComparisons.put(
                MiscellaneousOperator.ELEMENT_MATCH,
                new DocumentElement(MiscellaneousOperator.ELEMENT_MATCH
                        .getToken(), arrayElementQuery));
        return this;
    }

    /**
     * Query to match a single element in the array field.
     * <p>
     * Only a single {@link #elementMatches(QueryBuilder)} comparison can be
     * used. Calling multiple <tt>elementMatches(...)</tt> methods overwrites
     * previous values. In addition any {@link #equals(boolean) equals(...)}
     * condition is removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param arrayElementQuery
     *            A builder for the query to match a sub element. Any changes to
     *            the {@link QueryBuilder} after this method is called are not
     *            reflected in the comparison.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder elementMatches(final QueryBuilder arrayElementQuery) {
        myEqualsComparison = null;
        myOtherComparisons.put(
                MiscellaneousOperator.ELEMENT_MATCH,
                new DocumentElement(MiscellaneousOperator.ELEMENT_MATCH
                        .getToken(), arrayElementQuery.build()));
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equals(final boolean value) {
        myOtherComparisons.clear();
        myEqualsComparison = new BooleanElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param subType
     *            The binary values subtype.
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equals(final byte subType, final byte[] value) {
        myOtherComparisons.clear();
        myEqualsComparison = new BinaryElement(getFieldName(), subType, value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equals(final byte[] value) {
        myOtherComparisons.clear();
        myEqualsComparison = new BinaryElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equals(final Document value) {
        myOtherComparisons.clear();
        myEqualsComparison = new DocumentElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equals(final double value) {
        myOtherComparisons.clear();
        myEqualsComparison = new DoubleElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equals(final int value) {
        myOtherComparisons.clear();
        myEqualsComparison = new IntegerElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equals(final long value) {
        myOtherComparisons.clear();
        myEqualsComparison = new LongElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equals(final ObjectId value) {
        myOtherComparisons.clear();
        myEqualsComparison = new ObjectIdElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * NOTE: This checks if the value is a regular expression not if it matches
     * the regular expression. See {@link #matches(Pattern)}.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     * 
     * @see #matches(Pattern)
     */
    public ConditionBuilder equals(final Pattern value) {
        myOtherComparisons.clear();
        myEqualsComparison = new RegularExpressionElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equals(final String value) {
        myOtherComparisons.clear();
        myEqualsComparison = new StringElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equalsJavaScript(final String value) {
        myOtherComparisons.clear();
        myEqualsComparison = new JavaScriptElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @param scope
     *            The stored scope value.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equalsJavaScript(final String value,
            final Document scope) {
        myOtherComparisons.clear();
        myEqualsComparison = new JavaScriptWithScopeElement(getFieldName(),
                value, scope);
        return this;
    }

    /**
     * Checks if the value is a max key element.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equalsMaxKey() {
        myOtherComparisons.clear();
        myEqualsComparison = new MaxKeyElement(getFieldName());
        return this;
    }

    /**
     * Checks if the value is a min key element.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equalsMinKey() {
        myOtherComparisons.clear();
        myEqualsComparison = new MinKeyElement(getFieldName());
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equalsMongoTimestamp(final long value) {
        myOtherComparisons.clear();
        myEqualsComparison = new MongoTimestampElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value is a null value.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equalsNull() {
        myOtherComparisons.clear();
        myEqualsComparison = new NullElement(getFieldName());
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equalsSymbol(final String value) {
        myOtherComparisons.clear();
        myEqualsComparison = new SymbolElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the value equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #equals(boolean) equals(...)} comparison can be
     * used. Calling multiple {@link #equals(byte[]) equals(...)} methods
     * overwrites previous values. In addition <tt>equals(...)</tt> removes all
     * other conditions from the builder since there is no equal operator
     * supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder equalsTimestamp(final long value) {
        myOtherComparisons.clear();
        myEqualsComparison = new TimestampElement(getFieldName(), value);
        return this;
    }

    /**
     * Checks if the field exists (or not) in the document.
     * <p>
     * Only a single {@link #exists(boolean) exists(...)} comparison can be
     * used. Calling multiple {@link #exists() exists(...)} methods overwrites
     * previous values. In addition any {@link #equals(boolean) equals(...)}
     * condition is removed since no equality operator is supported by MongoDB.
     * </p>
     * <p>
     * This method is equivalent to calling {@link #exists(boolean)
     * exists(true)}.
     * </p>
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder exists() {
        return exists(true);
    }

    /**
     * Checks if the field exists (or not) in the document.
     * <p>
     * Only a single {@link #exists(boolean) exists(...)} comparison can be
     * used. Calling multiple {@link #exists() exists(...)} methods overwrites
     * previous values. In addition any {@link #equals(boolean) equals(...)}
     * condition is removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            If true the field must exist. If false the field must not
     *            exist.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder exists(final boolean value) {
        myEqualsComparison = null;
        myOtherComparisons.put(MiscellaneousOperator.EXISTS,
                new BooleanElement(MiscellaneousOperator.EXISTS.getToken(),
                        value));
        return this;
    }

    /**
     * Returns the fieldName value.
     * 
     * @return The fieldName value.
     */
    public String getFieldName() {
        return myFieldName;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param subType
     *            The binary values subtype.
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThan(final byte subType, final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT, new BinaryElement(
                ComparisonOperator.GT.getToken(), subType, value));
        return this;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThan(final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT, new BinaryElement(
                ComparisonOperator.GT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThan(final double value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT, new DoubleElement(
                ComparisonOperator.GT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThan(final int value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT, new IntegerElement(
                ComparisonOperator.GT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThan(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT, new LongElement(
                ComparisonOperator.GT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThan(final ObjectId value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT, new ObjectIdElement(
                ComparisonOperator.GT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThan(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT, new StringElement(
                ComparisonOperator.GT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanMongoTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT,
                new MongoTimestampElement(ComparisonOperator.GT.getToken(),
                        value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param subType
     *            The binary values subtype.
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualTo(final byte subType,
            final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE, new BinaryElement(
                ComparisonOperator.GTE.getToken(), subType, value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualTo(final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE, new BinaryElement(
                ComparisonOperator.GTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualTo(final double value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE, new DoubleElement(
                ComparisonOperator.GTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualTo(final int value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE, new IntegerElement(
                ComparisonOperator.GTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualTo(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE, new LongElement(
                ComparisonOperator.GTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualTo(final ObjectId value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE, new ObjectIdElement(
                ComparisonOperator.GTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualTo(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE, new StringElement(
                ComparisonOperator.GTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualToMongoTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE,
                new MongoTimestampElement(ComparisonOperator.GTE.getToken(),
                        value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualToSymbol(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE, new SymbolElement(
                ComparisonOperator.GTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than or equals the specified
     * <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThanOrEqualTo(int)
     * greaterThanOrEqualTo(...)} comparison can be used. Calling multiple
     * {@link #greaterThanOrEqualTo(byte[]) greaterThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanOrEqualToTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GTE, new TimestampElement(
                ComparisonOperator.GTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanSymbol(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT, new SymbolElement(
                ComparisonOperator.GT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is greater than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #greaterThan(int) greaterThan(...)} comparison can
     * be used. Calling multiple {@link #greaterThan(byte[]) greaterThan(...)}
     * methods overwrites previous values. In addition any
     * {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder greaterThanTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.GT, new TimestampElement(
                ComparisonOperator.GT.getToken(), value));
        return this;
    }

    /**
     * Specify the values that one must match the fields value.
     * <p>
     * Only a single {@link #in(ArrayBuilder)} comparison can be used. Calling
     * multiple <tt>in(...)</tt> methods overwrites previous values. In addition
     * any {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param elements
     *            A builder for the values for the comparison. Any changes to
     *            the {@link ArrayBuilder} after this method is called are not
     *            reflected in the comparison.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder in(final ArrayBuilder elements) {
        return in(elements.build());
    }

    /**
     * Specify the values that one must match the fields value.
     * <p>
     * Only a single {@link #in(Element[])} comparison can be used. Calling
     * multiple <tt>in(...)</tt> methods overwrites previous values. In addition
     * any {@link #equals(boolean) equals(...)} condition is removed since no
     * equality operator is supported by MongoDB.
     * </p>
     * 
     * @param elements
     *            The element values for the comparison.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder in(final Element... elements) {
        myEqualsComparison = null;
        myOtherComparisons.put(MiscellaneousOperator.IN, new ArrayElement(
                MiscellaneousOperator.IN.getToken(), elements));

        return this;
    }

    /**
     * Checks if the value's type matches the specified <tt>type</tt>.
     * <p>
     * Only a single {@link #instanceOf(ElementType)} comparison can be used.
     * Calling multiple {@link #instanceOf(ElementType)} methods overwrites
     * previous values. In addition any {@link #equals(boolean) equals(...)}
     * condition is removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param type
     *            The expected type for the value.
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder instanceOf(final ElementType type) {
        myEqualsComparison = null;
        myOtherComparisons.put(MiscellaneousOperator.TYPE, new IntegerElement(
                MiscellaneousOperator.TYPE.getToken(), type.getToken()));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param subType
     *            The binary values subtype.
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThan(final byte subType, final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT, new BinaryElement(
                ComparisonOperator.LT.getToken(), subType, value));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThan(final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT, new BinaryElement(
                ComparisonOperator.LT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThan(final double value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT, new DoubleElement(
                ComparisonOperator.LT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThan(final int value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT, new IntegerElement(
                ComparisonOperator.LT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThan(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT, new LongElement(
                ComparisonOperator.LT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThan(final ObjectId value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT, new ObjectIdElement(
                ComparisonOperator.LT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThan(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT, new StringElement(
                ComparisonOperator.LT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanMongoTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT,
                new MongoTimestampElement(ComparisonOperator.LT.getToken(),
                        value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param subType
     *            The binary values subtype.
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualTo(final byte subType,
            final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE, new BinaryElement(
                ComparisonOperator.LTE.getToken(), subType, value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualTo(final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE, new BinaryElement(
                ComparisonOperator.LTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualTo(final double value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE, new DoubleElement(
                ComparisonOperator.LTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualTo(final int value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE, new IntegerElement(
                ComparisonOperator.LTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualTo(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE, new LongElement(
                ComparisonOperator.LTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualTo(final ObjectId value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE, new ObjectIdElement(
                ComparisonOperator.LTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualTo(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE, new StringElement(
                ComparisonOperator.LTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualToMongoTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE,
                new MongoTimestampElement(ComparisonOperator.LTE.getToken(),
                        value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualToSymbol(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE, new SymbolElement(
                ComparisonOperator.LTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than or equals the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThanOrEqualTo(int) lessThanOrEqualTo(...)}
     * comparison can be used. Calling multiple
     * {@link #lessThanOrEqualTo(byte[]) lessThanOrEqualTo(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanOrEqualToTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LTE, new TimestampElement(
                ComparisonOperator.LTE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanSymbol(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT, new SymbolElement(
                ComparisonOperator.LT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is less than the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #lessThan(int) lessThan(...)} comparison can be
     * used. Calling multiple {@link #lessThan(byte[]) lessThan(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder lessThanTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.LT, new TimestampElement(
                ComparisonOperator.LT.getToken(), value));
        return this;
    }

    /**
     * Checks if the value matches the specified <tt>pattern</tt>.
     * <p>
     * NOTE: This checks if the value matches a regular expression not if it
     * equals the regular expression. See {@link #equals(Pattern)}. </
     * <p>
     * Only a single {@link #matches(Pattern)} comparison can be used. Calling
     * multiple {@link #matches(Pattern)} methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * <p>
     * This method is equivalent to calling {@link #exists(boolean)
     * exists(true)}.
     * </p>
     * 
     * @param pattern
     *            The pattern to match the value against.
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder matches(final Pattern pattern) {
        myEqualsComparison = null;
        myOtherComparisons.put(
                MiscellaneousOperator.REG_EX,
                new RegularExpressionElement(MiscellaneousOperator.REG_EX
                        .getToken(), pattern));
        return this;
    }

    /**
     * Checks if the modulo of the documents value and <tt>divisor</tt> equals
     * the <tt>remainder</tt>.
     * <p>
     * Only a single {@link #mod(int,int)} comparison can be used. Calling
     * multiple {@link #mod(long,long)} methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param divisor
     *            The divisor for the modulo operation.
     * @param remainder
     *            The desired remainder from the modulo operation.
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder mod(final int divisor, final int remainder) {
        myEqualsComparison = null;

        final ArrayBuilder builder = BuilderFactory.startArray();
        builder.addInteger(divisor);
        builder.addInteger(remainder);

        myOtherComparisons.put(MiscellaneousOperator.MOD, new ArrayElement(
                MiscellaneousOperator.MOD.getToken(), builder.build()));
        return this;
    }

    /**
     * Checks if the modulo of the documents value and <tt>divisor</tt> equals
     * the <tt>remainder</tt>.
     * <p>
     * Only a single {@link #mod(int,int)} comparison can be used. Calling
     * multiple {@link #mod(long,long)} methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param divisor
     *            The divisor for the modulo operation.
     * @param remainder
     *            The desired remainder from the modulo operation.
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder mod(final long divisor, final long remainder) {
        myEqualsComparison = null;

        final ArrayBuilder builder = BuilderFactory.startArray();
        builder.addLong(divisor);
        builder.addLong(remainder);

        myOtherComparisons.put(MiscellaneousOperator.MOD, new ArrayElement(
                MiscellaneousOperator.MOD.getToken(), builder.build()));
        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates.
     * <p>
     * Only a single {@link #near(int, int)} comparison can be used. Calling
     * multiple <tt>near(...)</tt> methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder near(final double x, final double y) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR, new ArrayElement(
                GeospatialOperator.NEAR.getToken(), new DoubleElement("0", x),
                new DoubleElement("1", y)));
        myOtherComparisons.remove(GeospatialOperator.MAX_DISTANCE_MODIFIER);

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates.
     * <p>
     * Only a single {@link #near(int, int)} comparison can be used. Calling
     * multiple <tt>near(...)</tt> methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @param maxDistance
     *            Limits to documents returned to those within the specified
     *            maximum distance.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder near(final double x, final double y,
            final double maxDistance) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR, new ArrayElement(
                GeospatialOperator.NEAR.getToken(), new DoubleElement("0", x),
                new DoubleElement("1", y)));
        myOtherComparisons.put(
                GeospatialOperator.MAX_DISTANCE_MODIFIER,
                new DoubleElement(GeospatialOperator.MAX_DISTANCE_MODIFIER
                        .getToken(), maxDistance));

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates.
     * <p>
     * Only a single {@link #near(int, int)} comparison can be used. Calling
     * multiple <tt>near(...)</tt> methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder near(final int x, final int y) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR, new ArrayElement(
                GeospatialOperator.NEAR.getToken(), new IntegerElement("0", x),
                new IntegerElement("1", y)));
        myOtherComparisons.remove(GeospatialOperator.MAX_DISTANCE_MODIFIER);

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates.
     * <p>
     * Only a single {@link #near(int, int)} comparison can be used. Calling
     * multiple <tt>near(...)</tt> methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @param maxDistance
     *            Limits to documents returned to those within the specified
     *            maximum distance.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder near(final int x, final int y, final int maxDistance) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR, new ArrayElement(
                GeospatialOperator.NEAR.getToken(), new IntegerElement("0", x),
                new IntegerElement("1", y)));
        myOtherComparisons.put(
                GeospatialOperator.MAX_DISTANCE_MODIFIER,
                new IntegerElement(GeospatialOperator.MAX_DISTANCE_MODIFIER
                        .getToken(), maxDistance));

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates.
     * <p>
     * Only a single {@link #near(int, int)} comparison can be used. Calling
     * multiple <tt>near(...)</tt> methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder near(final long x, final long y) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR, new ArrayElement(
                GeospatialOperator.NEAR.getToken(), new LongElement("0", x),
                new LongElement("1", y)));
        myOtherComparisons.remove(GeospatialOperator.MAX_DISTANCE_MODIFIER);

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates.
     * <p>
     * Only a single {@link #near(int, int)} comparison can be used. Calling
     * multiple <tt>near(...)</tt> methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @param maxDistance
     *            Limits to documents returned to those within the specified
     *            maximum distance.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder near(final long x, final long y,
            final long maxDistance) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR, new ArrayElement(
                GeospatialOperator.NEAR.getToken(), new LongElement("0", x),
                new LongElement("1", y)));
        myOtherComparisons.put(
                GeospatialOperator.MAX_DISTANCE_MODIFIER,
                new LongElement(GeospatialOperator.MAX_DISTANCE_MODIFIER
                        .getToken(), maxDistance));

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates on a sphere.
     * <p>
     * Only a single {@link #nearSphere(int, int)} comparison can be used.
     * Calling multiple <tt>nearSphere(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder nearSphere(final double x, final double y) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR_SPHERE,
                new ArrayElement(GeospatialOperator.NEAR_SPHERE.getToken(),
                        new DoubleElement("0", x), new DoubleElement("1", y)));
        myOtherComparisons.remove(GeospatialOperator.MAX_DISTANCE_MODIFIER);

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates on a sphere.
     * <p>
     * Only a single {@link #nearSphere(int, int)} comparison can be used.
     * Calling multiple <tt>nearSphere(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @param maxDistance
     *            Limits to documents returned to those within the specified
     *            maximum distance.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder nearSphere(final double x, final double y,
            final double maxDistance) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR_SPHERE,
                new ArrayElement(GeospatialOperator.NEAR_SPHERE.getToken(),
                        new DoubleElement("0", x), new DoubleElement("1", y)));
        myOtherComparisons.put(
                GeospatialOperator.MAX_DISTANCE_MODIFIER,
                new DoubleElement(GeospatialOperator.MAX_DISTANCE_MODIFIER
                        .getToken(), maxDistance));

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates on a sphere.
     * <p>
     * Only a single {@link #nearSphere(int, int)} comparison can be used.
     * Calling multiple <tt>nearSphere(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder nearSphere(final int x, final int y) {
        myEqualsComparison = null;
        myOtherComparisons
                .put(GeospatialOperator.NEAR_SPHERE, new ArrayElement(
                        GeospatialOperator.NEAR_SPHERE.getToken(),
                        new IntegerElement("0", x), new IntegerElement("1", y)));
        myOtherComparisons.remove(GeospatialOperator.MAX_DISTANCE_MODIFIER);

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates on a sphere.
     * <p>
     * Only a single {@link #nearSphere(int, int)} comparison can be used.
     * Calling multiple <tt>nearSphere(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @param maxDistance
     *            Limits to documents returned to those within the specified
     *            maximum distance.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder nearSphere(final int x, final int y,
            final int maxDistance) {
        myEqualsComparison = null;
        myOtherComparisons
                .put(GeospatialOperator.NEAR_SPHERE, new ArrayElement(
                        GeospatialOperator.NEAR_SPHERE.getToken(),
                        new IntegerElement("0", x), new IntegerElement("1", y)));
        myOtherComparisons.put(
                GeospatialOperator.MAX_DISTANCE_MODIFIER,
                new IntegerElement(GeospatialOperator.MAX_DISTANCE_MODIFIER
                        .getToken(), maxDistance));

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates on a sphere.
     * <p>
     * Only a single {@link #nearSphere(int, int)} comparison can be used.
     * Calling multiple <tt>nearSphere(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder nearSphere(final long x, final long y) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR_SPHERE,
                new ArrayElement(GeospatialOperator.NEAR_SPHERE.getToken(),
                        new LongElement("0", x), new LongElement("1", y)));
        myOtherComparisons.remove(GeospatialOperator.MAX_DISTANCE_MODIFIER);

        return this;
    }

    /**
     * Geospatial query for documents whose field is near the specified [
     * <tt>x</tt>, <tt>y</tt>] coordinates on a sphere.
     * <p>
     * Only a single {@link #nearSphere(int, int)} comparison can be used.
     * Calling multiple <tt>nearSphere(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate to find documents near.
     * @param y
     *            The Y coordinate to find documents near.
     * @param maxDistance
     *            Limits to documents returned to those within the specified
     *            maximum distance.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder nearSphere(final long x, final long y,
            final long maxDistance) {
        myEqualsComparison = null;
        myOtherComparisons.put(GeospatialOperator.NEAR_SPHERE,
                new ArrayElement(GeospatialOperator.NEAR_SPHERE.getToken(),
                        new LongElement("0", x), new LongElement("1", y)));
        myOtherComparisons.put(
                GeospatialOperator.MAX_DISTANCE_MODIFIER,
                new LongElement(GeospatialOperator.MAX_DISTANCE_MODIFIER
                        .getToken(), maxDistance));

        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualTo(final boolean value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new BooleanElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param subType
     *            The binary values subtype.
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualTo(final byte subType, final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new BinaryElement(
                ComparisonOperator.NE.getToken(), subType, value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualTo(final byte[] value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new BinaryElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualTo(final Document value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new DocumentElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualTo(final double value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new DoubleElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualTo(final int value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new IntegerElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualTo(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new LongElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualTo(final ObjectId value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new ObjectIdElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * NOTE: This checks if the value is a regular expression not if it matches
     * the regular expression. See {@link #matches(Pattern)}.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     * 
     * @see #matches(Pattern)
     */
    public ConditionBuilder notEqualTo(final Pattern value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE,
                new RegularExpressionElement(ComparisonOperator.NE.getToken(),
                        value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualTo(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new StringElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualToJavaScript(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new JavaScriptElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @param scope
     *            The stored scope value.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualToJavaScript(final String value,
            final Document scope) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE,
                new JavaScriptWithScopeElement(
                        ComparisonOperator.NE.getToken(), value, scope));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualToMaxKey() {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new MaxKeyElement(
                ComparisonOperator.NE.getToken()));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualToMinKey() {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new MinKeyElement(
                ComparisonOperator.NE.getToken()));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualToMongoTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE,
                new MongoTimestampElement(ComparisonOperator.NE.getToken(),
                        value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualToNull() {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new NullElement(
                ComparisonOperator.NE.getToken()));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualToSymbol(final String value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new SymbolElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Checks if the value is not equal to the specified <tt>value</tt>.
     * <p>
     * Only a single {@link #notEqualTo(boolean) notEqualTo(...)} comparison can
     * be used. Calling multiple {@link #notEqualTo(byte[]) equals(...)} methods
     * overwrites previous values. In addition any {@link #equals(boolean)
     * equals(...)} condition is removed since no equality operator is supported
     * by MongoDB.
     * </p>
     * 
     * @param value
     *            The value to compare the field against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notEqualToTimestamp(final long value) {
        myEqualsComparison = null;
        myOtherComparisons.put(ComparisonOperator.NE, new TimestampElement(
                ComparisonOperator.NE.getToken(), value));
        return this;
    }

    /**
     * Specify the values that must <em>not</em> must not match the fields
     * value.
     * <p>
     * Only a single {@link #notIn(ArrayBuilder)} comparison can be used.
     * Calling multiple <tt>notIn(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param elements
     *            A builder for the values for the comparison. Any changes to
     *            the {@link ArrayBuilder} after this method is called are not
     *            reflected in the comparison.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notIn(final ArrayBuilder elements) {
        return notIn(elements.build());
    }

    /**
     * Specify the values that must <em>not</em> must not match the fields
     * value.
     * <p>
     * Only a single {@link #notIn(Element[])} comparison can be used. Calling
     * multiple <tt>notIn(...)</tt> methods overwrites previous values. In
     * addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param elements
     *            The element values for the comparison.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder notIn(final Element... elements) {
        myEqualsComparison = null;
        myOtherComparisons.put(MiscellaneousOperator.NIN, new ArrayElement(
                MiscellaneousOperator.NIN.getToken(), elements));

        return this;
    }

    /**
     * Resets the builder back to an empty, no condition, state.
     */
    public void reset() {
        myOtherComparisons.clear();
        myEqualsComparison = null;
    }

    /**
     * Checks if the value is an array of the specified <tt>length</tt>.
     * <p>
     * Only a single {@link #size(int) lessThan(...)} comparison can be used.
     * Calling multiple <tt>size(int)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param length
     *            The value to compare the field's length against.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder size(final int length) {
        myEqualsComparison = null;
        myOtherComparisons.put(MiscellaneousOperator.SIZE, new IntegerElement(
                MiscellaneousOperator.SIZE.getToken(), length));
        return this;
    }

    /**
     * Adds an ad-hoc JavaScript condition to the query.
     * <p>
     * Only a single {@link #where(String)} condition can be used. Calling
     * multiple <tt>where(...)</tt> methods overwrites previous values.
     * </p>
     * 
     * @param javaScript
     *            The javaScript condition to add.
     * @return This builder for call chaining.
     */
    public ConditionBuilder where(final String javaScript) {
        myParent.whereJavaScript(javaScript);

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding polygon.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @param p1
     *            The first point defining the bounds of the polygon.
     * @param p2
     *            The second point defining the bounds of the polygon.
     * @param p3
     *            The third point defining the bounds of the polygon.
     * @param points
     *            The remaining points in the polygon.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final boolean uniqueDocs, final Point2D p1,
            final Point2D p2, final Point2D p3, final Point2D... points) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder.pushArray(GeospatialOperator.POLYGON);

        box.pushArray().addDouble(p1.getX()).addDouble(p1.getY());
        box.pushArray().addDouble(p2.getX()).addDouble(p2.getY());
        box.pushArray().addDouble(p3.getX()).addDouble(p3.getY());
        for (final Point2D p : points) {
            box.pushArray().addDouble(p.getX()).addDouble(p.getY());
        }

        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final double x, final double y,
            final double radius) {
        return within(x, y, radius, true);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final double x, final double y,
            final double radius, final boolean uniqueDocs) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder.pushArray(GeospatialOperator.CIRCLE);
        box.pushArray().addDouble(x).addDouble(y);
        box.addDouble(radius);
        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding rectangular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x1
     *            The first X coordinate.
     * @param y1
     *            The first Y coordinate.
     * @param x2
     *            The second X coordinate. NOT THE WIDTH.
     * @param y2
     *            The second Y coordinate. NOT THE HEIGHT.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final double x1, final double y1,
            final double x2, final double y2) {
        return within(x1, y1, x2, y2, true);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding rectangular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x1
     *            The first X coordinate.
     * @param y1
     *            The first Y coordinate.
     * @param x2
     *            The second X coordinate. NOT THE WIDTH.
     * @param y2
     *            The second Y coordinate. NOT THE HEIGHT.
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final double x1, final double y1,
            final double x2, final double y2, final boolean uniqueDocs) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder.pushArray(GeospatialOperator.BOX);
        box.pushArray().addDouble(Math.min(x1, x2)).addDouble(Math.min(y1, y2));
        box.pushArray().addDouble(Math.max(x1, x2)).addDouble(Math.max(y1, y2));
        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final int x, final int y, final int radius) {
        return within(x, y, radius, true);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final int x, final int y, final int radius,
            final boolean uniqueDocs) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder.pushArray(GeospatialOperator.CIRCLE);
        box.pushArray().addInteger(x).addInteger(y);
        box.addInteger(radius);
        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding rectangular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x1
     *            The first X coordinate.
     * @param y1
     *            The first Y coordinate.
     * @param x2
     *            The second X coordinate. NOT THE WIDTH.
     * @param y2
     *            The second Y coordinate. NOT THE HEIGHT.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final int x1, final int y1, final int x2,
            final int y2) {
        return within(x1, y1, x2, y2, true);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding rectangular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x1
     *            The first X coordinate.
     * @param y1
     *            The first Y coordinate.
     * @param x2
     *            The second X coordinate. NOT THE WIDTH.
     * @param y2
     *            The second Y coordinate. NOT THE HEIGHT.
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final int x1, final int y1, final int x2,
            final int y2, final boolean uniqueDocs) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder.pushArray(GeospatialOperator.BOX);
        box.pushArray().addInteger(Math.min(x1, x2))
                .addInteger(Math.min(y1, y2));
        box.pushArray().addInteger(Math.max(x1, x2))
                .addInteger(Math.max(y1, y2));
        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final long x, final long y, final long radius) {
        return within(x, y, radius, true);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final long x, final long y,
            final long radius, final boolean uniqueDocs) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder.pushArray(GeospatialOperator.CIRCLE);
        box.pushArray().addLong(x).addLong(y);
        box.addLong(radius);
        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding rectangular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x1
     *            The first X coordinate.
     * @param y1
     *            The first Y coordinate.
     * @param x2
     *            The second X coordinate. NOT THE WIDTH.
     * @param y2
     *            The second Y coordinate. NOT THE HEIGHT.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final long x1, final long y1, final long x2,
            final long y2) {
        return within(x1, y1, x2, y2, true);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding rectangular region.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x1
     *            The first X coordinate.
     * @param y1
     *            The first Y coordinate.
     * @param x2
     *            The second X coordinate. NOT THE WIDTH.
     * @param y2
     *            The second Y coordinate. NOT THE HEIGHT.
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final long x1, final long y1, final long x2,
            final long y2, final boolean uniqueDocs) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder.pushArray(GeospatialOperator.BOX);
        box.pushArray().addLong(Math.min(x1, x2)).addLong(Math.min(y1, y2));
        box.pushArray().addLong(Math.max(x1, x2)).addLong(Math.max(y1, y2));
        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding polygon.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>within(...)</tt> methods overwrites previous values.
     * In addition any {@link #equals(boolean) equals(...)} condition is removed
     * since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param p1
     *            The first point defining the bounds of the polygon.
     * @param p2
     *            The second point defining the bounds of the polygon.
     * @param p3
     *            The third point defining the bounds of the polygon.
     * @param points
     *            The remaining points in the polygon.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder within(final Point2D p1, final Point2D p2,
            final Point2D p3, final Point2D... points) {
        return within(true, p1, p2, p3, points);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region on a sphere.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>withinXXX(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder withinOnSphere(final double x, final double y,
            final double radius) {
        return withinOnSphere(x, y, radius, true);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region on a sphere.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>withinXXX(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder withinOnSphere(final double x, final double y,
            final double radius, final boolean uniqueDocs) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder
                .pushArray(GeospatialOperator.SPHERICAL_CIRCLE);
        box.pushArray().addDouble(x).addDouble(y);
        box.addDouble(radius);
        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region on a sphere.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>withinXXX(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder withinOnSphere(final int x, final int y,
            final int radius) {
        return withinOnSphere(x, y, radius, true);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region on a sphere.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>withinXXX(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder withinOnSphere(final int x, final int y,
            final int radius, final boolean uniqueDocs) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder
                .pushArray(GeospatialOperator.SPHERICAL_CIRCLE);
        box.pushArray().addInteger(x).addInteger(y);
        box.addInteger(radius);
        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region on a sphere.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>withinXXX(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder withinOnSphere(final long x, final long y,
            final long radius) {
        return withinOnSphere(x, y, radius, true);
    }

    /**
     * Geospatial query for documents whose field is within the specified
     * bounding circular region on a sphere.
     * <p>
     * Only a single {@link #within(int, int, int, int)} comparison can be used.
     * Calling multiple <tt>withinXXX(...)</tt> methods overwrites previous
     * values. In addition any {@link #equals(boolean) equals(...)} condition is
     * removed since no equality operator is supported by MongoDB.
     * </p>
     * 
     * @param x
     *            The X coordinate for the center of the circle.
     * @param y
     *            The Y coordinate for the center of the circle.
     * @param radius
     *            The radius of the circle.
     * @param uniqueDocs
     *            Controls if documents are returned multiple times for multiple
     *            matching conditions.
     * @return The condition builder for chaining method calls.
     */
    public ConditionBuilder withinOnSphere(final long x, final long y,
            final long radius, final boolean uniqueDocs) {
        myEqualsComparison = null;

        final DocumentBuilder builder = BuilderFactory.start();
        final ArrayBuilder box = builder
                .pushArray(GeospatialOperator.SPHERICAL_CIRCLE);
        box.pushArray().addLong(x).addLong(y);
        box.addLong(radius);
        builder.addBoolean(GeospatialOperator.UNIQUE_DOCS_MODIFIER, uniqueDocs);

        myOtherComparisons.put(GeospatialOperator.WITHIN, new DocumentElement(
                GeospatialOperator.WITHIN.getToken(), builder.build()));

        return this;
    }

    /**
     * Returns the element representing the current state of this fields
     * condition.
     * 
     * @return The element for the condition which may be <code>null</code> if
     *         no condition has been set.
     */
    /* package */Element buildFieldCondition() {
        if (!myOtherComparisons.isEmpty()) {
            return new DocumentElement(myFieldName, myOtherComparisons.values());
        }

        // Note - This may be null.
        return myEqualsComparison;
    }
}