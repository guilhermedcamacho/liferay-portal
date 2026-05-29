/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.internal.object.modifiable.system;

import com.liferay.object.modifiable.system.ModifiableSystemObjectDefinition;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "object.definition.name=AIHubQuotaConversionTable",
	service = ModifiableSystemObjectDefinition.class
)
public class AIHubQuotaConversionTableModifiableSystemObjectDefinition
	implements ModifiableSystemObjectDefinition {

	@Override
	public String getRESTContextPath() {
		return "/ai-hub/quota-conversion-tables";
	}

}