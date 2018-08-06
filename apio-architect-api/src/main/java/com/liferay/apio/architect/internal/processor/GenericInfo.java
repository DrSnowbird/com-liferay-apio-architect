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

import com.liferay.apio.architect.annotation.Router;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * @author Alejandro Hernández
 */
public class GenericInfo {

	private final Element _element;

	public GenericInfo(Element element) {
		_element = element;
		_router = element.getAnnotation(Router.class);
	}

	public TypeMirror getIdentifier() {
		try {
			_router.identifier();
		}
		catch (MirroredTypeException mte) {
			return mte.getTypeMirror();
		}

		return null;
	}

	public TypeMirror getModel() {
		try {
			_router.model();
		}
		catch (MirroredTypeException mte) {
			return mte.getTypeMirror();
		}

		return null;
	}

	private final Router _router;

	public Element getElement() {
		return _element;
	}
}