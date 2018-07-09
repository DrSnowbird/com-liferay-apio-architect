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

package com.liferay.apio.architect.impl.internal.writer;

import static com.liferay.apio.architect.impl.internal.url.URLCreator.createFormURL;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.impl.internal.message.json.ObjectBuilder;
import com.liferay.apio.architect.impl.internal.message.json.OperationMapper;
import com.liferay.apio.architect.impl.internal.request.RequestInfo;
import com.liferay.apio.architect.operation.Operation;

import java.util.Optional;

/**
 * Writes the operations identified by the methods of a {@link OperationMapper}.
 *
 * @author Javier Gamarra
 * @review
 */
public class OperationWriter {

	public OperationWriter(
		OperationMapper operationMapper, RequestInfo requestInfo,
		ObjectBuilder objectBuilder) {

		_operationMapper = operationMapper;
		_requestInfo = requestInfo;
		_objectBuilder = objectBuilder;
	}

	public void write(Operation operation) {
		ObjectBuilder operationObjectBuilder = new ObjectBuilder();

		Optional<Form> formOptional = operation.getFormOptional();

		formOptional.map(
			form -> createFormURL(_requestInfo.getApplicationURL(), form)
		).ifPresent(
			url -> _operationMapper.mapFormURL(operationObjectBuilder, url)
		);

		_operationMapper.mapHTTPMethod(
			operationObjectBuilder, operation.getHttpMethod());

		_operationMapper.onFinish(
			_objectBuilder, operationObjectBuilder, operation);
	}

	private final ObjectBuilder _objectBuilder;
	private final OperationMapper _operationMapper;
	private final RequestInfo _requestInfo;

}