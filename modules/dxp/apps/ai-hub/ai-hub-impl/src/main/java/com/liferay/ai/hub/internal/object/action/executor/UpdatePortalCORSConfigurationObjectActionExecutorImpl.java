/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.object.action.executor;

import com.liferay.object.action.executor.BaseObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;
import java.util.List;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Leite
 */
@Component(service = ObjectActionExecutor.class)
public class UpdatePortalCORSConfigurationObjectActionExecutorImpl
	extends BaseObjectActionExecutor implements ObjectDefinitionScoped {

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return List.of("AIHubConfiguration");
	}

	@Override
	public String getKey() {
		return "update-portal-cors-configuration";
	}

	@Override
	protected void doExecute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		String environmentUrls = MapUtil.getString(
			_objectEntryLocalService.getValues(
				payloadJSONObject.getLong("classPK")),
			"environmentUrls");

		if (Validator.isNull(environmentUrls)) {
			return;
		}

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(&(service.factoryPid=", _PORTAL_CORS_CONFIGURATION_PID,
				".scoped)(companyId=", companyId, "))"));

		if (ArrayUtil.isEmpty(configurations)) {
			return;
		}

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		properties.put(
			"headers",
			TransformUtil.transform(
				GetterUtil.getStringValues(properties.get("headers")),
				header -> {
					if (!StringUtil.startsWith(
							header, "Access-Control-Allow-Origin:") ||
						StringUtil.contains(
							header, environmentUrls, StringPool.SPACE)) {

						return header;
					}

					return StringBundler.concat(
						header, StringPool.SPACE, environmentUrls);
				},
				String.class));

		configuration.update(properties);
	}

	private static final String _PORTAL_CORS_CONFIGURATION_PID =
		"com.liferay.portal.remote.cors.configuration.PortalCORSConfiguration";

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}