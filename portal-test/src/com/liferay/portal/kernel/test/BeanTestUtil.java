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

package com.liferay.portal.kernel.test;

import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Method;

import java.util.Map;

/**
 * @author Guilherme Camacho
 */
public class BeanTestUtil {

	public static void copyProperties(Object source, Object target)
		throws Exception {

		Class<?> sourceClass = source.getClass();
		Class<?> targetClass = target.getClass();

		for (Method currentMethod : sourceClass.getMethods()) {
			String methodName = currentMethod.getName();

			if (methodName.startsWith("get")) {
				Method setMethod = targetClass.getMethod(
					methodName.replaceFirst("get", "set"),
					currentMethod.getReturnType());

				setMethod.invoke(target, currentMethod.invoke(source, null));
			}
		}
	}

	public static void setProperty(Object bean, String name, Object value)
		throws Exception {

		Class<?> clazz = bean.getClass();

		Method setMethod = clazz.getMethod(
			"set" + StringUtil.upperCaseFirstLetter(name),
			value instanceof Map ? Map.class : value.getClass());

		setMethod.invoke(bean, value);
	}

}