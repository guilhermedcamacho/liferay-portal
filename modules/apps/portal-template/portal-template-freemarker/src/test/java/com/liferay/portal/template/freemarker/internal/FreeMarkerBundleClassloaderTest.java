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

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getSymbolicName()
		).thenReturn(
			"com.liferay.portal.util.template"
		);

		Mockito.when(
			bundle.loadClass(TestClass.class.getName())
		).thenReturn(
			(Class)TestClass.class
		);

		FreeMarkerBundleClassloader freeMarkerBundleClassloader =
			new FreeMarkerBundleClassloader(symbolicNameMappers, bundle);

		Class<?> clazz = freeMarkerBundleClassloader.findClass(
			TestClass.class.getName());

		Assert.assertEquals(TestClass.class, clazz);
	}

	@Test
	public void testFindClassWhenThereIsNoSymbolicNamePrefixOfAnotherBundle()
		throws ClassNotFoundException {

		Bundle bundle1 = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle1.getSymbolicName()
		).thenReturn(
			"com.liferay.journal.taglib"
		);

		Bundle bundle2 = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle2.getSymbolicName()
		).thenReturn(
			"com.liferay.portal.template"
		);

		Mockito.when(
			bundle2.loadClass(TestClass.class.getName())
		).thenReturn(
			(Class)TestClass.class
		);

		FreeMarkerBundleClassloader freeMarkerBundleClassloader =
			new FreeMarkerBundleClassloader(null, bundle1, bundle2);

		Class<?> clazz = freeMarkerBundleClassloader.findClass(
			TestClass.class.getName());

		Assert.assertEquals(TestClass.class, clazz);
	}

	@Test
	public void testFindClassWhenThereIsSymbolicNamePrefixOfAnotherBundle()
		throws ClassNotFoundException {

		Bundle bundle1 = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle1.getSymbolicName()
		).thenReturn(
			"com.liferay.portal.template"
		);

		Mockito.when(
			bundle1.loadClass(TestClass.class.getName())
		).thenThrow(
			ClassNotFoundException.class
		);

		Bundle bundle2 = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle2.getSymbolicName()
		).thenReturn(
			"com.liferay.portal.template.freemarker"
		);

		Mockito.when(
			bundle2.loadClass(TestClass.class.getName())
		).thenReturn(
			(Class)TestClass.class
		);

		FreeMarkerBundleClassloader freeMarkerBundleClassloader =
			new FreeMarkerBundleClassloader(null, bundle1, bundle2);

		Class<?> clazz = freeMarkerBundleClassloader.findClass(
			TestClass.class.getName());

		Assert.assertEquals(TestClass.class, clazz);
	}

	private class TestClass {
	}

}