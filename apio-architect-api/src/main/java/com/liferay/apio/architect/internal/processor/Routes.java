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

import com.liferay.apio.architect.credentials.Credentials;
import static com.liferay.apio.architect.internal.processor.ClassNames.BOXED_LONG;
import static com.liferay.apio.architect.internal.processor.ClassNames.COMPONENT;
import static com.liferay.apio.architect.internal.processor.ClassNames.ITEM_ROUTER;
import static com.liferay.apio.architect.internal.processor.ClassNames.REFERENCE;
import static com.liferay.apio.architect.internal.processor.ClassNames.itemRoutesBuilderTypeName;
import static com.liferay.apio.architect.internal.processor.ClassNames.itemRoutesTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import static javax.lang.model.element.Modifier.PRIVATE;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Alejandro Hernández
 */
public class Routes {

	private GenericInfo _genericInfo;
	private Map<String, Route> itemRoutes = new HashMap<>();

	public Routes(GenericInfo genericInfo) {
		_genericInfo = genericInfo;
	}

	public Optional<TypeSpec> getItemRouterOptional() {
//		if (itemRoutes.isEmpty()) {
//			return Optional.empty();
//		}

		Element element = _genericInfo.getElement();

		TypeName routerTypeName = ClassName.get(element.asType());

		String fieldName =
			element.getSimpleName().toString().substring(0, 1).toLowerCase() +
				element.getSimpleName().toString().substring(1);

		FieldSpec routerReference = FieldSpec.builder(
			routerTypeName, fieldName, PRIVATE
		).addAnnotation(
			REFERENCE
		).build();

		TypeName credentialsTypeName = TypeName.get(Credentials.class);

		CodeBlock codeBlock = CodeBlock.builder().add(
			"return builder\n"
		).add(
			".addGetter($N::getPersonModel)\n", routerReference
		).add(
			".addRemover(\n"
		).add(
			"\t$N::deletePersonModel,\n", routerReference
		).add(
			"\t$T.class, (id, credentials) -> true)\n", credentialsTypeName
		).add(
			".build()"
		).build();

		MethodSpec intentMethod = MethodSpec.methodBuilder(
			"itemRoutes"
		).addAnnotation(
			Override.class
		).addModifiers(
			Modifier.PUBLIC
		).returns(
			itemRoutesTypeName(
				TypeName.get(_genericInfo.getModel()), BOXED_LONG)
		).addParameter(
			itemRoutesBuilderTypeName(
				TypeName.get(_genericInfo.getModel()), BOXED_LONG),
			"builder"
		).addStatement(
			codeBlock
		).build();

		TypeName typeName = ParameterizedTypeName.get(
			ITEM_ROUTER, TypeName.get(_genericInfo.getModel()), BOXED_LONG,
			TypeName.get(_genericInfo.getIdentifier()));

		TypeSpec typeSpec = TypeSpec.classBuilder(
			"PersonModelItemRouter"
		).addAnnotation(
			COMPONENT
		).addModifiers(
			Modifier.PUBLIC, Modifier.FINAL
		).addMethod(
			intentMethod
		).addSuperinterface(
			typeName
		).addField(
			routerReference
		).build();

		return Optional.of(typeSpec);
	}

	private class Route {
	}
}
