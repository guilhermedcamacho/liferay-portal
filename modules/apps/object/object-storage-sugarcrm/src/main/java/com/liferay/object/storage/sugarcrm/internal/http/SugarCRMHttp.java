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

package com.liferay.object.storage.sugarcrm.internal.http;

import com.liferay.object.rest.manager.exception.ObjectEntryManagerHttpException;
import com.liferay.object.storage.sugarcrm.configuration.SugarCRMConfiguration;
import com.liferay.object.storage.sugarcrm.internal.web.cache.SugarCRMAccessTokenWebCacheItem;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;

import java.net.HttpURLConnection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Maurice Sepe
 */
@Component(service = SugarCRMHttp.class)
public class SugarCRMHttp {

	public JSONObject delete(long companyId, long groupId, String location) {
		try {
			return _invoke(
				companyId, groupId, location, Http.Method.DELETE, null);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	public JSONObject get(long companyId, long groupId, String location) {
		try {
			return _invoke(companyId, groupId, location, Http.Method.GET, null);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	public JSONObject patch(
		long companyId, long groupId, String location,
		JSONObject bodyJSONObject) {

		try {
			return _invoke(
				companyId, groupId, location, Http.Method.PATCH,
				bodyJSONObject);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	public JSONObject post(
		long companyId, long groupId, String location,
		JSONObject bodyJSONObject) {

		try {
			return _invoke(
				companyId, groupId, location, Http.Method.POST, bodyJSONObject);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private JSONObject _getSugarCRMAccessTokenJSONObject(
		SugarCRMConfiguration sugarcrmConfiguration) {

		JSONObject jSONObject = SugarCRMAccessTokenWebCacheItem.get(
			sugarcrmConfiguration);

		if (jSONObject == null) {
			throw new ObjectEntryManagerHttpException(
				"Unable to authenticate with SugarCRM");
		}

		return jSONObject;
	}

	private SugarCRMConfiguration _getSugarCRMConfiguration(
		long companyId, long groupId) {

		try {
			if (groupId == 0) {
				return ConfigurationProviderUtil.getCompanyConfiguration(
					SugarCRMConfiguration.class, companyId);
			}

			return ConfigurationProviderUtil.getGroupConfiguration(
				SugarCRMConfiguration.class, groupId);
		}
		catch (ConfigurationException configurationException) {
			return ReflectionUtil.throwException(configurationException);
		}
	}

	private JSONObject _invoke(
			long companyId, long groupId, String location, Http.Method method,
			JSONObject bodyJSONObject)
		throws Exception {

		byte[] bytes = _invokeAsBytes(
			companyId, groupId, location, method, bodyJSONObject);

		if (bytes == null) {
			return _jsonFactory.createJSONObject();
		}

		return _jsonFactory.createJSONObject(new String(bytes));
	}

	private byte[] _invokeAsBytes(
			long companyId, long groupId, String location, Http.Method method,
			JSONObject bodyJSONObject)
		throws Exception {

		Http.Options options = new Http.Options();

		if (bodyJSONObject != null) {
			options.addHeader(
				HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		}

		SugarCRMConfiguration sugarcrmConfiguration = _getSugarCRMConfiguration(
			companyId, groupId);

		JSONObject jsonObject = _getSugarCRMAccessTokenJSONObject(
			sugarcrmConfiguration);

		options.addHeader(
			"Authorization", "Bearer " + jsonObject.getString("access_token"));

		if (bodyJSONObject != null) {
			options.setBody(
				bodyJSONObject.toString(), ContentTypes.APPLICATION_JSON,
				StringPool.UTF8);
		}

		options.setFollowRedirects(false);
		options.setLocation(
			StringBundler.concat(sugarcrmConfiguration.baseURL(), location));
		options.setMethod(method);

		_log.debug("SugarCRM connector calling URL: " + options.getLocation());

		byte[] bytes = _http.URLtoByteArray(options);

		Http.Response response = options.getResponse();

		if ((response.getResponseCode() < HttpURLConnection.HTTP_OK) ||
			(response.getResponseCode() >=
				HttpURLConnection.HTTP_MULT_CHOICE)) {

			throw new ObjectEntryManagerHttpException(
				StringBundler.concat(
					"Unexpected response code ", response.getResponseCode(),
					" with response message: ", new String(bytes)));
		}

		return bytes;
	}

	private static final Log _log = LogFactoryUtil.getLog(SugarCRMHttp.class);

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}