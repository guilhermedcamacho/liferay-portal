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

package com.liferay.object.storage.sugarcrm.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Maurice Sepe
 */
@ExtendedObjectClassDefinition(
	category = "third-party", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.object.storage.sugarcrm.configuration.SugarCRMConfiguration",
	localization = "content/Language", name = "sugarcrm-configuration-name"
)
public interface SugarCRMConfiguration {

	@Meta.AD(name = "base-url", required = false)
	public String baseURL();

	@Meta.AD(name = "access-token-url", required = false)
	public String accessTokenURL();

	@Meta.AD(name = "client-id", required = false)
	public String clientId();

	@Meta.AD(name = "grant-type", required = false)
	public String grantType();

	@Meta.AD(name = "username", required = false)
	public String username();

	@Meta.AD(name = "password", required = false, type = Meta.Type.Password)
	public String password();

}