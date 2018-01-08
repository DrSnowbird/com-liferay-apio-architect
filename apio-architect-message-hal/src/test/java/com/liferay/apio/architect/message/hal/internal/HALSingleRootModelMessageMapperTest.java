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

package com.liferay.apio.architect.message.hal.internal;

import static com.liferay.apio.architect.message.hal.internal.HALTestUtil.isALinkTo;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonBoolean;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonInt;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonObjectWhere;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonObjectWith;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonString;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.liferay.apio.architect.test.json.Conditions;
import com.liferay.apio.architect.test.model.RootModel;
import com.liferay.apio.architect.test.writer.MockSingleModelWriter;

import javax.ws.rs.core.HttpHeaders;

import org.hamcrest.Matcher;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Hernández
 */
public class HALSingleRootModelMessageMapperTest {

	@Test
	public void testHALSingleModelMessageMapper() {
		HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);

		JsonObject jsonObject = MockSingleModelWriter.write(
			httpHeaders, _singleModelMessageMapper);

		Conditions.Builder builder = new Conditions.Builder();

		Conditions conditions = builder.where(
			"_embedded", _isAJsonObjectWithTheEmbedded
		).where(
			"_links", _isAJsonObjectWithTheLinks
		).where(
			"boolean1", is(aJsonBoolean(true))
		).where(
			"boolean2", is(aJsonBoolean(false))
		).where(
			"date1", is(aJsonString(equalTo("2016-06-15T09:00Z")))
		).where(
			"date2", is(aJsonString(equalTo("2017-04-03T18:36Z")))
		).where(
			"localizedString1", is(aJsonString(equalTo("Translated 1")))
		).where(
			"localizedString2", is(aJsonString(equalTo("Translated 2")))
		).where(
			"number1", is(aJsonInt(equalTo(2017)))
		).where(
			"number2", is(aJsonInt(equalTo(42)))
		).where(
			"string1", is(aJsonString(equalTo("Live long and prosper")))
		).where(
			"string2", is(aJsonString(equalTo("Hypermedia")))
		).build();

		assertThat(jsonObject, is(aJsonObjectWith(conditions)));
	}

	@Test
	public void testMediaTypeIsCorrect() {
		String mediaType = _singleModelMessageMapper.getMediaType();

		assertThat(mediaType, is(equalTo("application/hal+json")));
	}

	private static final Matcher<JsonElement> _isAJsonObjectWithTheEmbedded;
	private static final Matcher<JsonElement> _isAJsonObjectWithTheLinks;

	static {
		Conditions.Builder builder = new Conditions.Builder();

		Conditions linkConditions = builder.where(
			"binary1", isALinkTo("localhost/b/model/first/binary1")
		).where(
			"binary2", isALinkTo("localhost/b/model/first/binary2")
		).where(
			"embedded2", isALinkTo("localhost/p/first-inner-model/second")
		).where(
			"link1", isALinkTo("www.liferay.com")
		).where(
			"link2", isALinkTo("community.liferay.com")
		).where(
			"linked1", isALinkTo("localhost/p/first-inner-model/third")
		).where(
			"linked2", isALinkTo("localhost/p/first-inner-model/fourth")
		).where(
			"relatedCollection1", isALinkTo("localhost/p/model/first/models")
		).where(
			"relatedCollection2", isALinkTo("localhost/p/model/first/models")
		).where(
			"self", isALinkTo("localhost/p/model/first")
		).build();

		_isAJsonObjectWithTheLinks = is(aJsonObjectWith(linkConditions));

		Conditions firstEmbeddedLinkConditions = builder.where(
			"binary", isALinkTo("localhost/b/first-inner-model/first/binary")
		).where(
			"link", isALinkTo("www.liferay.com")
		).where(
			"linked", isALinkTo("localhost/p/second-inner-model/second")
		).where(
			"relatedCollection",
			isALinkTo("localhost/p/first-inner-model/first/models")
		).where(
			"self", isALinkTo("localhost/p/first-inner-model/first")
		).build();

		Conditions secondEmbeddedLinkConditions = builder.where(
			"binary", isALinkTo("localhost/b/second-inner-model/first/binary")
		).where(
			"embedded", isALinkTo("localhost/p/third-inner-model/first")
		).where(
			"link", isALinkTo("community.liferay.com")
		).where(
			"linked", isALinkTo("localhost/p/third-inner-model/second")
		).where(
			"relatedCollection",
			isALinkTo("localhost/p/second-inner-model/first/models")
		).where(
			"self", isALinkTo("localhost/p/second-inner-model/first")
		).build();

		Conditions secondEmbeddedConditions = builder.where(
			"_links", is(aJsonObjectWith(secondEmbeddedLinkConditions))
		).where(
			"boolean", is(aJsonBoolean(false))
		).where(
			"number", is(aJsonInt(equalTo(2017)))
		).where(
			"string", is(aJsonString(equalTo("A string")))
		).build();

		Matcher<JsonElement> isAJsonObjectWithTheSecondEmbedded = is(
			aJsonObjectWith(secondEmbeddedConditions));

		Conditions firstEmbeddedConditions = builder.where(
			"_embedded",
			is(aJsonObjectWhere("embedded", isAJsonObjectWithTheSecondEmbedded))
		).where(
			"_links", is(aJsonObjectWith(firstEmbeddedLinkConditions))
		).where(
			"boolean", is(aJsonBoolean(true))
		).where(
			"localizedString", is(aJsonString(equalTo("Translated")))
		).where(
			"number", is(aJsonInt(equalTo(42)))
		).where(
			"string", is(aJsonString(equalTo("A string")))
		).build();

		Conditions embeddedConditions = builder.where(
			"embedded1", is(aJsonObjectWith(firstEmbeddedConditions))
		).build();

		_isAJsonObjectWithTheEmbedded = is(aJsonObjectWith(embeddedConditions));
	}

	private final HALSingleModelMessageMapper<RootModel>
		_singleModelMessageMapper = new HALSingleModelMessageMapper<>();

}