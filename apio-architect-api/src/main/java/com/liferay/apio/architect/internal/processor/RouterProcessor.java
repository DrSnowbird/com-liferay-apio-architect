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

import com.liferay.apio.architect.functional.Try;

import static javax.tools.Diagnostic.Kind.ERROR;

import com.liferay.apio.architect.annotation.Router;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author Alejandro Hernández
 */
@SupportedAnnotationTypes("com.liferay.apio.architect.annotation.Router")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RouterProcessor extends AbstractProcessor {

	@Override
	public synchronized void init(ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);

		_messager = processingEnvironment.getMessager();
		_filer = processingEnvironment.getFiler();
		_elements = processingEnvironment.getElementUtils();
	}

	@Override
	public boolean process(
		Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

		Set<? extends Element> elements =
			roundEnvironment.getElementsAnnotatedWith(Router.class);

		for (Element element : elements) {
			if (element.getKind() != ElementKind.CLASS) {
				_messager.printMessage(
					ERROR, "Router must be applied to class", element);

				return true;
			}

			GenericInfo genericInfo = new GenericInfo(element);

			Routes routes = new Routes(genericInfo);

			MethodVisitor methodVisitor = new MethodVisitor(routes);

			element.getEnclosedElements().forEach(
				enclosedElement -> methodVisitor.visit(
					enclosedElement, genericInfo));

			Optional<TypeSpec> itemRouterOptional =
				routes.getItemRouterOptional();

			itemRouterOptional.ifPresent(_writeFile(element));
		}

		return true;
	}

	private String _getPackage(Element element) {
		PackageElement packageElement = _elements.getPackageOf(element);

		Name name = packageElement.getQualifiedName();

		return name.toString();
	}

	private Consumer<TypeSpec> _writeFile(Element element) {
		return typeSpec -> Try.fromFallible(
			() -> JavaFile.builder(
				_getPackage(element), typeSpec
			).indent(
				"\t"
			).build()
		).voidFold(
			__ -> _messager.printMessage(
				ERROR, "Failed to write file for element", element),
			javaFile -> javaFile.writeTo(_filer)
		);
	}

	private Elements _elements;
	private Filer _filer;

	private Messager _messager;
}