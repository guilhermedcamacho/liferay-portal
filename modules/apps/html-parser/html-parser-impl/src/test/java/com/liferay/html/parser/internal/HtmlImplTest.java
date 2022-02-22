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

package com.liferay.html.parser.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Olaf Kock
 * @author Neil Zhao Jin
 */
public class HtmlImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testExtraction() {
		Assert.assertEquals(
			"whitespace removal",
			_htmlImpl.extractText("   whitespace \n   <br/> removal   "));
		Assert.assertEquals(
			"script removal",
			_htmlImpl.extractText(
				"script <script>   test   </script> removal"));
		Assert.assertEquals(
			"HTML attribute removal",
			_htmlImpl.extractText(
				"<h1>HTML</h1> <i>attribute</i> <strong>removal</strong>"));
		Assert.assertEquals(
			"onclick removal",
			_htmlImpl.extractText(
				"<div onclick=\"honk()\">onclick removal</div>"));
	}

	private final HtmlImpl _htmlImpl = new HtmlImpl();

}