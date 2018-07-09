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

import com.google.gson.JsonObject;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.form.FormField;
import com.liferay.apio.architect.impl.internal.message.json.FormMessageMapper;
import com.liferay.apio.architect.impl.internal.message.json.ObjectBuilder;
import com.liferay.apio.architect.impl.internal.request.RequestInfo;

import java.util.List;
import java.util.function.Function;

/**
 * Writes the form.
 *
 * @author Alejandro Hernández
 */
public class FormWriter {

	/**
	 * Creates a new {@code FormWriter} object, without creating the builder.
	 *
	 * @param  function the function that transforms a builder into the {@code
	 *         FormWriter}
	 * @return the {@code FormWriter} instance
	 */
	public static FormWriter create(Function<Builder, FormWriter> function) {
		return function.apply(new Builder());
	}

	public FormWriter(Builder builder) {
		_form = builder._form;
		_formMessageMapper = builder._formMessageMapper;
		_requestInfo = builder._requestInfo;
	}

	/**
	 * Writes the {@link Form} to a string.
	 *
	 * @return the JSON representation of the {@code Form}
	 */
	public String write() {
		ObjectBuilder objectBuilder = new ObjectBuilder();

		String url = createFormURL(_requestInfo.getApplicationURL(), _form);

		_formMessageMapper.mapFormURL(objectBuilder, url);

		String title = _form.getTitle(_requestInfo.getAcceptLanguage());

		_formMessageMapper.mapFormTitle(objectBuilder, title);

		String description = _form.getDescription(
			_requestInfo.getAcceptLanguage());

		_formMessageMapper.mapFormDescription(objectBuilder, description);

		List<FormField> formFields = _form.getFormFields();

		formFields.forEach(
			formField -> _formMessageMapper.mapFormField(
				objectBuilder, formField));

		_formMessageMapper.onFinish(objectBuilder, _form);

		JsonObject jsonObject = objectBuilder.build();

		return jsonObject.toString();
	}

	/**
	 * Creates {@code FormWriter} instances.
	 */
	public static class Builder {

		/**
		 * Add information about the form being written to the builder.
		 *
		 * @param  form the form being written
		 * @return the updated builder
		 */
		public FormMessageMapperStep form(Form form) {
			_form = form;

			return new FormMessageMapperStep();
		}

		public class BuildStep {

			/**
			 * Constructs and returns a {@code FormWriter} instance with the
			 * information provided to the builder.
			 *
			 * @return the {@code FormWriter} instance
			 */
			public FormWriter build() {
				return new FormWriter(Builder.this);
			}

		}

		public class FormMessageMapperStep {

			/**
			 * Adds information to the builder about the {@link
			 * FormMessageMapper}.
			 *
			 * @param  formMessageMapper the {@code FormMessageMapper}
			 * @return the updated builder
			 */
			public RequestInfoStep formMessageMapper(
				FormMessageMapper formMessageMapper) {

				_formMessageMapper = formMessageMapper;

				return new RequestInfoStep();
			}

		}

		public class RequestInfoStep {

			/**
			 * Adds information to the builder about the request.
			 *
			 * @param  requestInfo the information obtained from the request. It
			 *         can be created by using a {@link RequestInfo.Builder}.
			 * @return the updated builder
			 */
			public BuildStep requestInfo(RequestInfo requestInfo) {
				_requestInfo = requestInfo;

				return new BuildStep();
			}

		}

		private Form _form;
		private FormMessageMapper _formMessageMapper;
		private RequestInfo _requestInfo;

	}

	private final Form<?> _form;
	private final FormMessageMapper _formMessageMapper;
	private final RequestInfo _requestInfo;

}