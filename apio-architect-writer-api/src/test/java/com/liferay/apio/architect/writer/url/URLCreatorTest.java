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

package com.liferay.apio.architect.writer.url;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageType;
import com.liferay.apio.architect.uri.Path;
import com.liferay.apio.architect.url.ServerURL;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.Collections;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class URLCreatorTest {

	@Test(expected = UnsupportedOperationException.class)
	public void testConstructorThrowsException() throws Throwable {
		Constructor<?> constructor =
			URLCreator.class.getDeclaredConstructors()[0];

		constructor.setAccessible(true);

		try {
			constructor.newInstance();
		}
		catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		}
	}

	@Test
	public void testCreateBinaryURL() {
		String binaryId = "binary";

		String url = URLCreator.createBinaryURL(_serverURL, binaryId, _path);

		assertThat(url, is(equalTo("www.liferay.com/b/name/id/binary")));
	}

	@Test
	public void testCreateCollectionPageURL() {
		Page page = new Page<>(
			String.class, Collections.emptyList(), 30, 1, 0, null);

		String firstPageURL = URLCreator.createCollectionPageURL(
			"www.liferay.com", page, PageType.FIRST);

		assertThat(
			firstPageURL, is(equalTo("www.liferay.com?page=1&per_page=30")));
	}

	@Test
	public void testCreateCollectionURL() {
		String url = URLCreator.createCollectionURL(
			_serverURL, _path, "related");

		assertThat(url, is(equalTo("www.liferay.com/p/name/id/related")));
	}

	@Test
	public void testCreateSingleURL() {
		String url = URLCreator.createSingleURL(_serverURL, _path);

		assertThat(url, is(equalTo("www.liferay.com/p/name/id")));
	}

	private final Path _path = new Path("name", "id");
	private final ServerURL _serverURL = () -> "www.liferay.com";

}