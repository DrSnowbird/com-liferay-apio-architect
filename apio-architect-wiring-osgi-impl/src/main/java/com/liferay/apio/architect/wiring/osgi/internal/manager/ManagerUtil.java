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

package com.liferay.apio.architect.wiring.osgi.internal.manager;

import com.liferay.apio.architect.error.ApioDeveloperError.MustHaveValidGenericType;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.wiring.osgi.internal.manager.service.tracker.customizer.BaseServiceTrackerCustomizer;
import com.liferay.apio.architect.wiring.osgi.util.GenericUtil;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Util class for managers.
 *
 * @author Alejandro Hernández
 * @review
 */
public class ManagerUtil {

	/**
	 * Creates a {@code ServiceTracker} that will register its managed services
	 * as the classes provided in the {@code classes} parameter.
	 *
	 * @param  bundleContext the bundle context
	 * @param  clazz the managed class
	 * @param  classes the list of classes with which the service will be
	 *         registered
	 * @return the service tracker
	 * @review
	 */
	public static <T> ServiceTracker<T, ServiceRegistration<?>>
		createServiceTracker(
			BundleContext bundleContext, Class<T> clazz, String[] classes) {

		return new ServiceTracker<>(
			bundleContext, clazz,
			(BaseServiceTrackerCustomizer<T>)serviceReference -> {
				Dictionary<String, Object> properties = getProperties(
					serviceReference);

				T t = null;

				try {
					t = bundleContext.getService(serviceReference);
				}
				catch (Exception e) {
					return null;
				}

				return bundleContext.registerService(classes, t, properties);
			});
	}

	/**
	 * Returns a dictionary containing the properties of a {@code
	 * ServiceReference}.
	 *
	 * @param  serviceReference the service reference
	 * @return a dictionary containing the properties of a {@code
	 *         ServiceReference}
	 * @review
	 */
	public static <T> Dictionary<String, Object> getProperties(
		ServiceReference<T> serviceReference) {

		Dictionary<String, Object> properties = new Hashtable<>();

		String[] propertyKeys = serviceReference.getPropertyKeys();

		for (String propertyKey : propertyKeys) {
			properties.put(
				propertyKey, serviceReference.getProperty(propertyKey));
		}

		return properties;
	}

	/**
	 * Return a type param from a generic interface of the class of an object at
	 * a certain position or fail.
	 *
	 * @param  t the object
	 * @param  interfaceClass the interface class to look for
	 * @param  position the type param position
	 * @return the type param
	 * @review
	 */
	public static <T, U> Class<U> getTypeParamOrFail(
		T t, Class<?> interfaceClass, Integer position) {

		Class<?> clazz = t.getClass();

		Try<Class<U>> classTry = GenericUtil.getGenericTypeArgumentTry(
			clazz, interfaceClass, position);

		return classTry.orElseThrow(() -> new MustHaveValidGenericType(clazz));
	}

}