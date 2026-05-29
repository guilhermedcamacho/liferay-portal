/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.rest.internal.resource.v1_0;

import com.liferay.ai.hub.pricing.rest.resource.v1_0.QuotaBlockResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carolina Barbosa
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/quota-block.properties",
	scope = ServiceScope.PROTOTYPE, service = QuotaBlockResource.class
)
public class QuotaBlockResourceImpl extends BaseQuotaBlockResourceImpl {
}