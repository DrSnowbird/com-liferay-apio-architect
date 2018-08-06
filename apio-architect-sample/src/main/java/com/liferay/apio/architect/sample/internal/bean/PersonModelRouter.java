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

package com.liferay.apio.architect.sample.internal.bean;

import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import static com.liferay.apio.architect.sample.internal.auth.PermissionChecker.hasPermission;

import com.liferay.apio.architect.annotation.Identifier;
import com.liferay.apio.architect.annotation.Router;
import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.sample.internal.identifier.PersonIdentifier;
import com.liferay.apio.architect.sample.internal.model.PersonModel;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.ForbiddenException;

import org.osgi.service.component.annotations.Component;

/**
 * Represents the values extracted from a person form.
 *
 * @author Alejandro Hernández
 */
@Component(service = PersonModelRouter.class)
@Router(identifier = PersonIdentifier.class, model = PersonModel.class)
public class PersonModelRouter {

	public PageItems<PersonModel> getPageItems(Pagination pagination) {
		List<PersonModel> personModels = PersonModel.getPage(
			pagination.getStartPosition(), pagination.getEndPosition());
		int count = PersonModel.getCount();

		return new PageItems<>(personModels, count);
	}

	public void deletePersonModel(
		@Identifier long id, Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		PersonModel.remove(id);
	}

	public PersonModel getPersonModel(@Identifier long id) {
		Optional<PersonModel> optional = PersonModel.get(id);

		return optional.get();
	}

}