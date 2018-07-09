/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.apio.architect.impl.internal.message.json;

import static com.liferay.apio.architect.test.util.json.JsonMatchers.aJsonArrayThat;
import static com.liferay.apio.architect.test.util.json.JsonMatchers.aJsonBoolean;
import static com.liferay.apio.architect.test.util.json.JsonMatchers.aJsonInt;
import static com.liferay.apio.architect.test.util.json.JsonMatchers.aJsonObjectWhere;
import static com.liferay.apio.architect.test.util.json.JsonMatchers.aJsonObjectWith;
import static com.liferay.apio.architect.test.util.json.JsonMatchers.aJsonString;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.liferay.apio.architect.test.util.json.Conditions;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class JSONObjectBuilderTest {

	@Test
	public void testInvokingAddAllOnAnArrayValueCreatesAValidJsonArray() {
		ObjectBuilder.ArrayValueStep arrayValueStep =
			_objectBuilder.field(
				"array"
			).arrayValue();

		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("solution", 42);

		arrayValueStep.addAllBooleans(Arrays.asList(false, true));
		arrayValueStep.addAllJsonObjects(Arrays.asList(jsonObject, jsonObject));
		arrayValueStep.addAllNumbers(Arrays.asList(21, 42));
		arrayValueStep.addAllStrings(Arrays.asList("api", "apio"));

		List<Matcher<? super JsonElement>> matchers = Arrays.asList(
			aJsonBoolean(false), aJsonBoolean(true),
			_aJsonObjectWithTheSolution, _aJsonObjectWithTheSolution,
			aJsonInt(equalTo(21)), aJsonInt(equalTo(42)),
			aJsonString(equalTo("api")), aJsonString(equalTo("apio")));

		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(contains(matchers)));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingAddConsumerCreatesAValidJsonArray() {
		_objectBuilder.field(
			"array"
		).arrayValue(
		).add(
			jsonObjectBuilder -> jsonObjectBuilder.field(
				"solution"
			).numberValue(
				42
			)
		);

		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(contains(_aJsonObjectWithTheSolution)));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingAddJsonObjectBuilderCreatesAValidJsonArray() {
		ObjectBuilder objectBuilder = new ObjectBuilder();

		objectBuilder.field(
			"solution"
		).numberValue(
			42
		);

		_objectBuilder.field(
			"array"
		).arrayValue(
		).add(
			objectBuilder
		);

		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(contains(_aJsonObjectWithTheSolution)));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingAddOnAnArrayValueCreatesAValidJsonArray() {
		ObjectBuilder.ArrayValueStep arrayValueStep =
			_objectBuilder.field(
				"array"
			).arrayValue();

		arrayValueStep.addBoolean(true);
		arrayValueStep.addNumber(42);
		arrayValueStep.addString("apio");

		List<Matcher<? super JsonElement>> matchers = Arrays.asList(
			aJsonBoolean(true), aJsonInt(equalTo(42)),
			aJsonString(equalTo("apio")));

		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(contains(matchers)));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingAddVarargConsumersCreatesAValidJsonArray() {
		_objectBuilder.field(
			"array"
		).arrayValue(
		).add(
			jsonObjectBuilder -> jsonObjectBuilder.field(
				"solution"
			).numberValue(
				42
			),
			jsonObjectBuilder -> jsonObjectBuilder.field(
				"solution"
			).numberValue(
				42
			)
		);

		@SuppressWarnings("unchecked")
		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(
				contains(
					_aJsonObjectWithTheSolution, _aJsonObjectWithTheSolution)));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingArrayValueCreatesAJsonArray() {
		_objectBuilder.field(
			"array"
		).arrayValue();

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere(
				"array", is(aJsonArrayThat(not(contains(anything()))))));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingArrayValueWithConsumersCreatesAValidJsonArray() {
		_objectBuilder.field(
			"array"
		).arrayValue(
			arrayBuilder -> arrayBuilder.addString("first"),
			arrayBuilder -> arrayBuilder.addString("second")
		);

		@SuppressWarnings("unchecked")
		Matcher<Iterable<? extends JsonElement>> containsFirstAndSecond =
			contains(
				aJsonString(equalTo("first")), aJsonString(equalTo("second")));

		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(containsFirstAndSecond));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingBooleanValueCreatesABoolean() {
		_objectBuilder.field(
			"solution"
		).booleanValue(
			true
		);

		Matcher<JsonElement> isAJsonObjectWithTheSolution = is(
			aJsonObjectWhere("solution", is(aJsonBoolean(true))));

		assertThat(getJsonObject(), isAJsonObjectWithTheSolution);
	}

	@Test
	public void testInvokingFalseIfElseConditionCreatesACorrectField() {
		_objectBuilder.ifElseCondition(
			false, builder -> builder.field("true"),
			builder -> builder.field("solution")
		).numberValue(
			42
		);

		assertThat(getJsonObject(), is(_aJsonObjectWithTheSolution));
	}

	@Test
	public void testInvokingFieldAndFalseIfConditionCreatesACorrectField() {
		_objectBuilder.field(
			"first"
		).ifCondition(
			false, builder -> builder.field("solution")
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", is(aJsonInt(equalTo(42)))));

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst);
	}

	@Test
	public void testInvokingFieldAndFalseIfElseConditionCreatesACorrectField() {
		_objectBuilder.field(
			"first"
		).ifElseCondition(
			false, builder -> builder.field("true"),
			builder -> builder.field("solution")
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", is(is(_aJsonObjectWithTheSolution))));

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst);
	}

	@Test
	public void testInvokingFieldAndNestedPrefixedFieldCreatesACorrectField() {
		_objectBuilder.field(
			"solution"
		).nestedPrefixedField(
			"prefix", "first", "second", "third"
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheSolution = is(
			aJsonObjectWhere("solution", isAJsonObjectWithTheFirstPrefix()));

		assertThat(getJsonObject(), isAJsonObjectWithTheSolution);
	}

	@Test
	public void testInvokingFieldAndNestedSuffixedFieldCreatesACorrectField() {
		_objectBuilder.field(
			"solution"
		).nestedSuffixedField(
			"suffix", "first", "second", "third"
		).numberValue(
			42
		);

		assertThat(
			getJsonObject(),
			is(aJsonObjectWhere("solution", isAJsonObjectWithTheFirst())));
	}

	@Test
	public void testInvokingFieldAndTrueIfConditionCreatesACorrectField() {
		_objectBuilder.field(
			"first"
		).ifCondition(
			true, builder -> builder.field("solution")
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", is(is(_aJsonObjectWithTheSolution))));

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst);
	}

	@Test
	public void testInvokingFieldAndTrueIfElseConditionCreatesACorrectField() {
		_objectBuilder.field(
			"first"
		).ifElseCondition(
			true, builder -> builder.field("solution"),
			builder -> builder.field("false")
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", is(is(_aJsonObjectWithTheSolution))));

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst);
	}

	@Test
	public void testInvokingFieldCreatesACorrectField() {
		_objectBuilder.field(
			"solution"
		).numberValue(
			42
		);

		assertThat(getJsonObject(), is(_aJsonObjectWithTheSolution));
	}

	@Test
	public void testInvokingFieldsWithConsumersCreatesAValidJsonObject() {
		_objectBuilder.field(
			"object"
		).fields(
			builder -> builder.field(
				"first"
			).numberValue(
				42
			),
			builder -> builder.field(
				"second"
			).numberValue(
				2018
			)
		);

		Conditions.Builder builder = new Conditions.Builder();

		Conditions conditions = builder.where(
			"first", is(aJsonInt(equalTo(42)))
		).where(
			"second", is(aJsonInt(equalTo(2018)))
		).build();

		assertThat(
			getJsonObject(),
			is(aJsonObjectWhere("object", is(aJsonObjectWith(conditions)))));
	}

	@Test
	public void testInvokingNestedFieldCreatesACorrectNestedField() {
		_objectBuilder.nestedField(
			"the", "solution"
		).numberValue(
			42
		);

		assertThat(
			getJsonObject(),
			is(aJsonObjectWhere("the", is(_aJsonObjectWithTheSolution))));
	}

	@Test
	public void testInvokingNestedPrefixedFieldCreatesACorrectField() {
		_objectBuilder.nestedPrefixedField(
			"prefix", "first", "second", "third"
		).numberValue(
			42
		);

		assertThat(getJsonObject(), isAJsonObjectWithTheFirstPrefix());
	}

	@Test
	public void testInvokingNestedSuffixedFieldCreatesACorrectField() {
		_objectBuilder.nestedSuffixedField(
			"suffix", "first", "second", "third"
		).numberValue(
			42
		);

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst());
	}

	@Test
	public void testInvokingNumberValueCreatesANumber() {
		_objectBuilder.field(
			"solution"
		).numberValue(
			42
		);

		assertThat(getJsonObject(), is(_aJsonObjectWithTheSolution));
	}

	@Test
	public void testInvokingStringValueCreatesAString() {
		_objectBuilder.field(
			"solution"
		).stringValue(
			"forty-two"
		);

		Matcher<JsonElement> isAJsonObjectWithTheSolution = is(
			aJsonObjectWhere(
				"solution", is(aJsonString(equalTo("forty-two")))));

		assertThat(getJsonObject(), isAJsonObjectWithTheSolution);
	}

	@Test
	public void testInvokingTrueIfElseConditionCreatesACorrectField() {
		_objectBuilder.ifElseCondition(
			true, builder -> builder.field("solution"),
			builder -> builder.field("false")
		).numberValue(
			42
		);

		assertThat(getJsonObject(), is(_aJsonObjectWithTheSolution));
	}

	protected JsonObject getJsonObject() {
		return _objectBuilder.build();
	}

	protected Matcher<JsonElement> isAJsonObjectWithTheFirst() {
		Matcher<JsonElement> isAJsonObjectWithTheThirdSuffix = is(
			aJsonObjectWhere("suffix", is(aJsonInt(equalTo(42)))));

		Matcher<JsonElement> isAJsonObjectWithTheThird = is(
			aJsonObjectWhere("third", isAJsonObjectWithTheThirdSuffix));

		Matcher<JsonElement> isAJsonObjectWithTheSecondSuffix = is(
			aJsonObjectWhere("suffix", isAJsonObjectWithTheThird));

		Matcher<JsonElement> isAJsonObjectWithTheSecond = is(
			aJsonObjectWhere("second", isAJsonObjectWithTheSecondSuffix));

		Matcher<JsonElement> isAJsonObjectWithTheFirstSuffix = is(
			aJsonObjectWhere("suffix", isAJsonObjectWithTheSecond));

		return is(aJsonObjectWhere("first", isAJsonObjectWithTheFirstSuffix));
	}

	protected Matcher<JsonElement> isAJsonObjectWithTheFirstPrefix() {
		Matcher<JsonElement> isAJsonObjectWithTheThird = is(
			aJsonObjectWhere("third", is(aJsonInt(equalTo(42)))));

		Matcher<JsonElement> isAJsonObjectWithTheThirdPrefix = is(
			aJsonObjectWhere("prefix", isAJsonObjectWithTheThird));

		Matcher<JsonElement> isAJsonObjectWithTheSecond = is(
			aJsonObjectWhere("second", isAJsonObjectWithTheThirdPrefix));

		Matcher<JsonElement> isAJsonObjectWithTheSecondPrefix = is(
			aJsonObjectWhere("prefix", isAJsonObjectWithTheSecond));

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", isAJsonObjectWithTheSecondPrefix));

		return is(aJsonObjectWhere("prefix", isAJsonObjectWithTheFirst));
	}

	private final Matcher<JsonElement> _aJsonObjectWithTheSolution =
		aJsonObjectWhere("solution", is(aJsonInt(equalTo(42))));
	private final ObjectBuilder _objectBuilder =
		new ObjectBuilder();

}