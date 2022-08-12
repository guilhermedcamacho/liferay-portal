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

package com.liferay.portal.template.freemarker.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.Bundle;

/**
 * @author Guilherme Camacho
 */
public class FreeMarkerBundleClassloaderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test(expected = ClassNotFoundException.class)
	public void testFindClassWhenClassDoesNotExists()
		throws ClassNotFoundException {

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getSymbolicName()
		).thenReturn(
			"com.liferay.portal.template"
		);

		Mockito.when(
			bundle.loadClass(TestClass.class.getName() + "BeanInfo")
		).thenThrow(
			ClassNotFoundException.class
		);

		FreeMarkerBundleClassloader freeMarkerBundleClassloader =
			new FreeMarkerBundleClassloader(null, bundle);

		freeMarkerBundleClassloader.findClass(
			TestClass.class.getName() + "BeanInfo");
	}

	@Test
	public void testFindClassWhenThereIsBundleSymbolicNameMappers()
		throws ClassNotFoundException {

		String[] symbolicNameMappers = {
			"com.liferay.util.taglib=com.liferay.taglib",
			"com.liferay.portal.util.template=com.liferay.portal.template"
		};

		Bundle bundle = _createBundle(
			"com.liferay.portal.util.template", false);

		FreeMarkerBundleClassloader freeMarkerBundleClassloader =
			new FreeMarkerBundleClassloader(symbolicNameMappers, bundle);

		Class<?> clazz = freeMarkerBundleClassloader.findClass(
			TestClass.class.getName());

		Assert.assertEquals(TestClass.class, clazz);
	}

	@Test
	public void testFindClassWhenThereIsNoSymbolicNamePrefixOfAnotherBundle()
		throws ClassNotFoundException {

		Bundle[] bundles = {
			_createBundle("com.liferay.journal.taglib", true),
			_createBundle("com.liferay.portal.template", false)
		};

		FreeMarkerBundleClassloader freeMarkerBundleClassloader =
			new FreeMarkerBundleClassloader(null, bundles);

		Class<?> clazz = freeMarkerBundleClassloader.findClass(
			TestClass.class.getName());

		Assert.assertEquals(TestClass.class, clazz);
	}

	@Test
	public void testFindClassWhenThereIsSymbolicNamePrefixOfAnotherBundle()
		throws ClassNotFoundException {

		Bundle[] bundles = {
			_createBundle("com.liferay.portal.template", false),
			_createBundle("com.liferay.portal.template.abc", true),
			_createBundle("com.liferay.portal.template.def", true),
			_createBundle("com.liferay.portal.template.ghi", true)
		};

		FreeMarkerBundleClassloader freeMarkerBundleClassloader =
			new FreeMarkerBundleClassloader(null, bundles);

		Class<?> clazz = freeMarkerBundleClassloader.findClass(
			TestClass.class.getName());

		Assert.assertEquals(TestClass.class, clazz);
	}

	private Bundle _createBundle(
			String symbolicName, boolean throwsClassNotFoundException)
		throws ClassNotFoundException {

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getSymbolicName()
		).thenReturn(
			symbolicName
		);

		if (throwsClassNotFoundException) {
			Mockito.when(
				bundle.loadClass(TestClass.class.getName())
			).thenThrow(
				ClassNotFoundException.class
			);
		}
		else {
			Mockito.when(
				bundle.loadClass(TestClass.class.getName())
			).thenReturn(
				(Class)TestClass.class
			);
		}

		return bundle;
	}

	private class TestClass {
	}

}