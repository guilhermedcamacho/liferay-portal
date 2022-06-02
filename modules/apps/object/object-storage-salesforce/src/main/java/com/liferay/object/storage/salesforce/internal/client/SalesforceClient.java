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

package com.liferay.object.storage.salesforce.internal.client;

import com.liferay.object.storage.salesforce.internal.configuration.SalesforceConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.FileInputStream;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

import java.text.MessageFormat;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Guilherme Camacho
 */
@Component(
	configurationPid = "com.liferay.object.storage.salesforce.internal.configuration.SalesforceConfiguration",
	enabled = true, immediate = true, service = SalesforceClient.class
)
public class SalesforceClient {

	public JSONObject query(String queryString) {
		try {
			Http.Options options = new Http.Options();

			options.addHeader(
				"Authorization", "Bearer " + _getSalesforceAccessToken());

			options.setLocation(
				HttpComponentsUtil.addParameter(
					_instanceUrl + "/services/data/v54.0/query/", "q",
					queryString));

			return _jsonFactory.createJSONObject(_http.URLtoString(options));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_salesforceConfiguration = ConfigurableUtil.createConfigurable(
			SalesforceConfiguration.class, properties);
	}

	/**
	 * OAuth 2.0 Username-Password Flow
	 */
	private JSONObject _autenticate() {
		try {
			String userName = "guilherme.camacho@liferay.com";
			String password = "";
			String tokenUrl =
				"https://login.salesforce.com/services/oauth2/token";

			Http.Options options = new Http.Options();

			options.addPart(
				"client_id", _salesforceConfiguration.consumerKey());
			options.addPart(
				"client_secret", _salesforceConfiguration.consumerSecret());
			options.addPart("grant_type", "password");
			options.addPart("username", userName);
			options.addPart("password", password);

			options.setLocation(tokenUrl);
			options.setPost(true);

			return _jsonFactory.createJSONObject(_http.URLtoString(options));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	/**
	 * OAuth 2.0 JWT Bearer Flow
	 */
	private JSONObject _autenticate(String jwtBearerToken) {
		try {
			String tokenUrl =
				"https://login.salesforce.com/services/oauth2/token";

			Http.Options options = new Http.Options();

			options.addHeader(
				"Content-Type", ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED);

			options.addPart(
				"client_id", _salesforceConfiguration.consumerKey());
			options.addPart(
				"grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
			options.addPart("assertion", jwtBearerToken);

			options.setLocation(tokenUrl);
			options.setPost(true);

			return _jsonFactory.createJSONObject(_http.URLtoString(options));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	private String _getSalesforceAccessToken() {
		if (Validator.isNull(_accessToken)) {
			//			JSONObject responseJSONObject = _autenticate();

			JSONObject responseJSONObject = _autenticate(_getToken());

			_accessToken = responseJSONObject.getString("access_token");
			_instanceUrl = responseJSONObject.getString("instance_url");
		}

		return _accessToken;
	}

	// JWTExample

	private String _getToken() {
		String header = "{\"alg\":\"RS256\"}";

		// 		String claimTemplate =
		// 			"'{'\"iss\": \"{0}\", \"sub\": \"{1}\", " +

		//			"\"aud\": \"{2}\", \"exp\": \"{3}\", \"jti\": \"{4\"'}'";

		String claimTemplate =
			"'{'\"iss\": \"{0}\", \"sub\": \"{1}\", \"aud\": \"{2}\", " +
				"\"exp\": \"{3}\"'}'";

		try {
			StringBuffer token = new StringBuffer();

			// Encode the JWT Header and add it to our string to sign

			token.append(
				Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));

			// Separate with a period

			token.append(".");

			// Create the JWT Claims Object

			String[] claimArray = new String[4];

			claimArray[0] = _salesforceConfiguration.consumerKey();
			claimArray[1] = "guilherme.camacho@liferay.com";
			claimArray[2] = "https://login.salesforce.com";
			claimArray[3] = String.valueOf(
				(System.currentTimeMillis() / 1000) + 300);
			//			claimArray[4]=<JTI>

			MessageFormat claims = new MessageFormat(claimTemplate);

			String payload = claims.format(claimArray);

			// Add the encoded claims object

			token.append(
				Base64.encodeBase64URLSafeString(payload.getBytes("UTF-8")));

			// Load the private key from a keystore

			KeyStore keyStore = KeyStore.getInstance("JKS");

			keyStore.load(
				new FileInputStream(
					"/home/guilherme/projects/JWT/salesforce.jks"),
				"test@123".toCharArray());

			PrivateKey privateKey = (PrivateKey)keyStore.getKey(
				"liferay_cert", "liferaypwd".toCharArray());

			//Sign the JWT Header + "." + JWT Claims Object
			Signature signature = Signature.getInstance("SHA256withRSA");

			signature.initSign(privateKey);

			String tokenValue = token.toString();

			signature.update(tokenValue.getBytes("UTF-8"));

			String signedPayload = Base64.encodeBase64URLSafeString(
				signature.sign());

			// Separate with a period

			token.append(".");

			// Add the encoded signature

			token.append(signedPayload);

			return token.toString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SalesforceClient.class);

	private String _accessToken;

	@Reference
	private Http _http;

	private String _instanceUrl;

	@Reference
	private JSONFactory _jsonFactory;

	private volatile SalesforceConfiguration _salesforceConfiguration;

}