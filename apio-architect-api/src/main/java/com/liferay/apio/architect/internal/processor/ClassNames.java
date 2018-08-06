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

package com.liferay.apio.architect.internal.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

/**
 * @author Alejandro Hernández
 */
public final class ClassNames {

	public static final ClassName BOXED_LONG = ClassName.get(
		"java.lang", "Long");

	public static final ClassName COMPONENT = ClassName.get(
		"org.osgi.service.component.annotations", "Component");

	public static final ClassName ITEM_ROUTER = ClassName.get(
		"com.liferay.apio.architect.router", "ItemRouter");

	public static final ClassName ITEM_ROUTES = ClassName.get(
		"com.liferay.apio.architect.routes", "ItemRoutes");

	public static final ClassName REFERENCE = ClassName.get(
		"org.osgi.service.component.annotations", "Reference");

	public static TypeName itemRoutesBuilderTypeName(
		TypeName... typeArguments) {

		return ParameterizedTypeName.get(
			ITEM_ROUTES.nestedClass("Builder"), typeArguments);
	}

	public static TypeName itemRoutesTypeName(TypeName... typeArguments) {
		return ParameterizedTypeName.get(ITEM_ROUTES, typeArguments);
	}

}