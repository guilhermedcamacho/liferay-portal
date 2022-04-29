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

package com.liferay.object.rest.internal.manager.v1_0;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.delivery.dto.v1_0.Creator;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.internal.dto.v1_0.converter.ObjectEntryDTOConverter;
import com.liferay.object.rest.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.object.rest.internal.odata.entity.v1_0.ObjectEntryEntityModel;
import com.liferay.object.rest.internal.resource.v1_0.ObjectEntryResourceImpl;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.util.ObjectEntryFieldValueUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.GroupUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Javier de Arcos
 */
@Component(
	immediate = true,
	property = "object.entry.manager.key=" + "Salesforce",
	service = ObjectEntryManager.class
)
public class ObjectEntryManagerSalesforceImpl implements ObjectEntryManager {

	private static final String TOKEN_URL = "https://test.salesforce.com/services/oauth2/token";

	private static JsonNode login(ObjectMapper mapper) {

		String username = "";
		String password = "";
		String consumerKey = "";
		String consumerSecret = "";

		JsonNode loginResult = null;

		try (CloseableHttpClient closeableHttpClient =  HttpClients.createDefault()) {

			List<NameValuePair> loginParams = new ArrayList<NameValuePair>();
			loginParams.add(new BasicNameValuePair("client_id", consumerKey));
			loginParams.add(new BasicNameValuePair("client_secret", consumerSecret));
			loginParams.add(new BasicNameValuePair("grant_type", "password"));
			loginParams.add(new BasicNameValuePair("username", username));
			loginParams.add(new BasicNameValuePair("password", password));

			HttpPost post = new HttpPost(TOKEN_URL);
			post.setEntity(new UrlEncodedFormEntity(loginParams));

			HttpResponse loginResponse = closeableHttpClient.execute(post);

			loginResult = mapper.readValue(loginResponse.getEntity().getContent(), JsonNode.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return loginResult;
	}

	private Page<ObjectEntry> _getObjectEntries(String accessToken, String instanceUrl, Pagination pagination) {

		try (CloseableHttpClient closeableHttpClient =  HttpClients.createDefault()) {

			URIBuilder uriBuilder = new URIBuilder(instanceUrl);
			uriBuilder.setPath("/services/data/v54.0/query/")
				.setParameter("q", "SELECT FIELDS(ALL) FROM Ticket__c LIMIT 20");

			HttpGet httpGet = new HttpGet(uriBuilder.build());
			httpGet.setHeader("Authorization", "Bearer " + accessToken);

			try (CloseableHttpResponse closeableHttpResponse =
					 closeableHttpClient.execute(httpGet)) {

				StatusLine statusLine = closeableHttpResponse.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_OK) {

					throw new PortalException(
						"Unable to get objects: " +
						EntityUtils.toString(closeableHttpResponse.getEntity()));
				}

				JSONObject responseJSONObject =
					JSONFactoryUtil.createJSONObject(
						EntityUtils.toString(
							closeableHttpResponse.getEntity(),
							Charset.defaultCharset()));

				return Page.of(
					transformToObjectEntries(responseJSONObject.getJSONArray("records")), pagination, responseJSONObject.getInt("totalSize"));
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private List<ObjectEntry> transformToObjectEntries(JSONArray jsonArray)
		throws ParseException {
		List<ObjectEntry> objectEntries = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			objectEntries.add(transformToObjectEntry(jsonObject));
		}

		return objectEntries;
	}

	private ObjectEntry transformToObjectEntry(JSONObject jsonObject)
		throws ParseException {

		ObjectEntry objectEntry = new ObjectEntry();

		objectEntry.setActions(Collections.emptyMap());

		String createdDate = jsonObject.getString("CreatedDate");
		objectEntry.setDateCreated(_dateFormat.parse(createdDate));

		String lastModifiedDate = jsonObject.getString("LastModifiedDate");
		objectEntry.setDateModified(_dateFormat.parse(lastModifiedDate));

		Creator creator = new Creator();
		creator.setAdditionalName("");
		creator.setContentType("UserAccount");
		creator.setFamilyName("Test");
		creator.setGivenName("Test");
		creator.setId(20127L);
		creator.setName("Test Test");
		objectEntry.setCreator(creator);

		//objectEntry.setId(42147L);

		Status status = new Status();
		status.setCode(0);
		status.setLabel("approved");
		status.setLabel_i18n("Approved");
		objectEntry.setStatus(status);

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			if(key.lastIndexOf("__c") != -1) {
				String customFieldName = key.substring(
					0, key.lastIndexOf("__c"));

				objectEntry.getProperties().put(customFieldName, jsonObject.get(key));
			}
			else if(key.equals("Id")) {
				objectEntry.setExternalReferenceCode(jsonObject.getString(key));
			}
		};

		return objectEntry;
	}

	private JSONObject transformToSalesforceEntry(ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		ObjectField titleObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getTitleObjectFieldId());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

//		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
//			"yyyy-MM-dd'T'HH:mm:ss'Z'");
//
//		if(objectEntry.getDateCreated() != null) {
//			jsonObject.put("CreatedDate", liferayToJSONDateFormat.format(objectEntry.getDateCreated()));
//		}
//
//		if(objectEntry.getDateModified() != null) {
//			jsonObject.put("LastModifiedDate", liferayToJSONDateFormat.format(objectEntry.getDateModified()));
//		}

		Map<String, Object> properties = objectEntry.getProperties();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {

			jsonObject.put(entry.getKey()+"__c", entry.getValue());

			if(titleObjectField != null && titleObjectField.getName().equals(entry.getKey())) {
				jsonObject.put("Name", entry.getValue());
			}
		}

		return jsonObject;
	}

	@Override
	public ObjectEntry addObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey)
		throws Exception {

		JsonNode loginResult = login(_objectMapper);

		String accessToken = loginResult.get("access_token").asText();
		String instanceUrl = loginResult.get("instance_url").asText();

		return _addObjectEntry(accessToken, instanceUrl, objectDefinition, objectEntry);

//		return _toObjectEntry(
//			dtoConverterContext, objectDefinition,
//			_objectEntryService.addObjectEntry(
//				_getGroupId(objectDefinition, scopeKey),
//				objectDefinition.getObjectDefinitionId(),
//				_toObjectValues(
//					objectDefinition.getObjectDefinitionId(),
//					objectEntry.getProperties(),
//					dtoConverterContext.getLocale()),
//				new ServiceContext()));
	}

	private ObjectEntry _addObjectEntry(String accessToken, String instanceUrl, ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		try (CloseableHttpClient closeableHttpClient =  HttpClients.createDefault()) {

			URIBuilder builder = new URIBuilder(instanceUrl);
			builder.setPath("/services/data/v54.0/sobjects/Ticket__c/");

			HttpPost httpPost = new HttpPost(builder.build());
			httpPost.setHeader("Authorization", "Bearer " + accessToken);

			JSONObject jsonObject = transformToSalesforceEntry(objectDefinition, objectEntry);

			//			httpPost.setEntity(
//				new StringEntity(body, StandardCharsets.UTF_8));

			StringEntity entity = new StringEntity(jsonObject.toString());
			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			try (CloseableHttpResponse closeableHttpResponse =
					 closeableHttpClient.execute(httpPost)) {

				StatusLine statusLine = closeableHttpResponse.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_CREATED) {

					throw new PortalException(
						"Unable to post object: " +
						EntityUtils.toString(closeableHttpResponse.getEntity()));
				}

				JSONObject responseJSONObject =
					JSONFactoryUtil.createJSONObject(
						EntityUtils.toString(
							closeableHttpResponse.getEntity(),
							Charset.defaultCharset()));

				return _getObjectEntry(accessToken, instanceUrl, responseJSONObject.getString("id"));
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public ObjectEntry addOrUpdateObjectEntry(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, String scopeKey)
		throws Exception {

		JsonNode loginResult = login(_objectMapper);

		String accessToken = loginResult.get("access_token").asText();
		String instanceUrl = loginResult.get("instance_url").asText();

		return _addOrUpdateObjectEntry(accessToken, instanceUrl, externalReferenceCode, objectDefinition, objectEntry);

//		return _toObjectEntry(
//			dtoConverterContext, objectDefinition,
//			_objectEntryService.addOrUpdateObjectEntry(
//				externalReferenceCode, _getGroupId(objectDefinition, scopeKey),
//				objectDefinition.getObjectDefinitionId(),
//				_toObjectValues(
//					objectDefinition.getObjectDefinitionId(),
//					objectEntry.getProperties(),
//					dtoConverterContext.getLocale()),
//				new ServiceContext()));
	}

	private ObjectEntry _addOrUpdateObjectEntry(String accessToken, String instanceUrl, String identifier,
												ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		try (CloseableHttpClient closeableHttpClient =  HttpClients.createDefault()) {

			URIBuilder uriBuilder = new URIBuilder(instanceUrl);
			uriBuilder.setPath("/services/data/v54.0/sobjects/Ticket__c/" + identifier);

			HttpPatch httpPatch = new HttpPatch(uriBuilder.build());
			httpPatch.setHeader("Authorization", "Bearer " + accessToken);

			JSONObject jsonObject = transformToSalesforceEntry(objectDefinition, objectEntry);

			StringEntity entity = new StringEntity(jsonObject.toString());
			httpPatch.setEntity(entity);
			httpPatch.setHeader("Accept", "application/json");
			httpPatch.setHeader("Content-type", "application/json");

			try (CloseableHttpResponse closeableHttpResponse =
					 closeableHttpClient.execute(httpPatch)) {

				StatusLine statusLine = closeableHttpResponse.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_NO_CONTENT) {

					throw new PortalException(
						"Unable to update object: " +
						EntityUtils.toString(closeableHttpResponse.getEntity()));
				}

				return _getObjectEntry(accessToken, instanceUrl, identifier);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void deleteObjectEntry(
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		_checkObjectEntryObjectDefinitionId(
			objectDefinition,
			_objectEntryService.getObjectEntry(objectEntryId));

		_objectEntryService.deleteObjectEntry(objectEntryId);
	}

	@Override
	public void deleteObjectEntry(
			String externalReferenceCode, long companyId,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {

		JsonNode loginResult = login(_objectMapper);

		String accessToken = loginResult.get("access_token").asText();
		String instanceUrl = loginResult.get("instance_url").asText();

		_deleteObjectEntry(accessToken, instanceUrl, externalReferenceCode);

//		com.liferay.object.model.ObjectEntry objectEntry =
//			_objectEntryService.getObjectEntry(
//				externalReferenceCode, companyId,
//				_getGroupId(objectDefinition, scopeKey));
//
//		_checkObjectEntryObjectDefinitionId(objectDefinition, objectEntry);
//
//		_objectEntryService.deleteObjectEntry(objectEntry.getObjectEntryId());
	}

	private void _deleteObjectEntry(String accessToken, String instanceUrl, String identifier) {

		try (CloseableHttpClient closeableHttpClient =  HttpClients.createDefault()) {

			URIBuilder uriBuilder = new URIBuilder(instanceUrl);
			uriBuilder.setPath("/services/data/v54.0/sobjects/Ticket__c/" + identifier);

			HttpDelete httpDelete = new HttpDelete(uriBuilder.build());
			httpDelete.setHeader("Authorization", "Bearer " + accessToken);

			try (CloseableHttpResponse closeableHttpResponse =
					 closeableHttpClient.execute(httpDelete)) {

				StatusLine statusLine = closeableHttpResponse.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_NO_CONTENT) {

					throw new PortalException(
						"Unable to delete object: " +
						EntityUtils.toString(closeableHttpResponse.getEntity()));
				}
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public ObjectEntry fetchObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		com.liferay.object.model.ObjectEntry objectEntry =
			_objectEntryService.fetchObjectEntry(objectEntryId);

		if (objectEntry != null) {
			return _toObjectEntry(
				dtoConverterContext, objectDefinition, objectEntry);
		}

		return null;
	}

//	private ObjectEntry _getObjectEntry(String result)
//		throws IOException {
//
//		JsonNode jsonNode = _objectMapper.readTree(result);
//
//		JsonNode nameJsonNode = jsonNode.get("name");
//		JsonNode labelJsonNode = jsonNode.get("label");
//		JsonNode amountJsonNode = jsonNode.get("amount");
//
//		return ObjectEntry.toDTO(result);
//	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
		long companyId, ObjectDefinition objectDefinition, String scopeKey,
		Aggregation aggregation, DTOConverterContext dtoConverterContext,
		Filter filter, Pagination pagination, String search, Sort[] sorts)
		throws Exception {

		//_objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		JsonNode loginResult = login(_objectMapper);

		String accessToken = loginResult.get("access_token").asText();
		String instanceUrl = loginResult.get("instance_url").asText();

		System.out.println("##### begin getTickets"+ System.lineSeparator());
		Page<ObjectEntry> page = _getObjectEntries(accessToken, instanceUrl, pagination);
		System.out.println(page);
		//System.out.println(_objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(queryResults));
		return page;
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
		long companyId, ObjectDefinition objectDefinition, String scopeKey,
		Aggregation aggregation, DTOConverterContext dtoConverterContext,
		String filterString, Pagination pagination, String search,
		Sort[] sorts)
		throws Exception {

		return getObjectEntries(
			companyId, objectDefinition, scopeKey, aggregation,
			dtoConverterContext,
			_toFilter(
				filterString, dtoConverterContext.getLocale(),
				objectDefinition.getObjectDefinitionId()),
			pagination, search, sorts);
	}

	@Override
	public ObjectEntry getObjectEntry(
		DTOConverterContext dtoConverterContext,
		ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		com.liferay.object.model.ObjectEntry objectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		_checkObjectEntryObjectDefinitionId(objectDefinition, objectEntry);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry);
	}

	@Override
	public ObjectEntry getObjectEntry(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, long companyId,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {

		JsonNode loginResult = login(_objectMapper);

		String accessToken = loginResult.get("access_token").asText();
		String instanceUrl = loginResult.get("instance_url").asText();

		return _getObjectEntry(accessToken, instanceUrl, externalReferenceCode);

//		com.liferay.object.model.ObjectEntry objectEntry =
//			_objectEntryService.getObjectEntry(
//				externalReferenceCode, companyId,
//				_getGroupId(objectDefinition, scopeKey));
//
//		_checkObjectEntryObjectDefinitionId(objectDefinition, objectEntry);

//		return _toObjectEntry(
//			dtoConverterContext, objectDefinition, objectEntry);
	}

	private ObjectEntry _getObjectEntry(String accessToken, String instanceUrl, String identifier) {

		try (CloseableHttpClient closeableHttpClient =  HttpClients.createDefault()) {

			URIBuilder uriBuilder = new URIBuilder(instanceUrl);
			uriBuilder.setPath("/services/data/v54.0/sobjects/Ticket__c/" + identifier);

			HttpGet httpGet = new HttpGet(uriBuilder.build());
			httpGet.setHeader("Authorization", "Bearer " + accessToken);

			try (CloseableHttpResponse closeableHttpResponse =
					 closeableHttpClient.execute(httpGet)) {

				StatusLine statusLine = closeableHttpResponse.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_OK) {

					throw new PortalException(
						"Unable to get object: " +
						EntityUtils.toString(closeableHttpResponse.getEntity()));
				}

				JSONObject responseJSONObject =
					JSONFactoryUtil.createJSONObject(
						EntityUtils.toString(
							closeableHttpResponse.getEntity(),
							Charset.defaultCharset()));

				return transformToObjectEntry(responseJSONObject);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public ObjectEntry updateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId,
			ObjectEntry objectEntry)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition,
			_objectEntryService.updateObjectEntry(
				objectEntryId,
				_toObjectValues(
					serviceBuilderObjectEntry.getObjectDefinitionId(),
					objectEntry.getProperties(),
					dtoConverterContext.getLocale()),
				new ServiceContext()));
	}

	private void _checkObjectEntryObjectDefinitionId(
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry objectEntry)
		throws Exception {

		if (objectDefinition.getObjectDefinitionId() !=
				objectEntry.getObjectDefinitionId()) {

			throw new NoSuchObjectEntryException();
		}
	}

	private long _getGroupId(
		ObjectDefinition objectDefinition, String scopeKey) {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			if (Objects.equals("site", objectDefinition.getScope())) {
				return GroupUtil.getGroupId(
					objectDefinition.getCompanyId(), scopeKey,
					_groupLocalService);
			}

			return GroupUtil.getDepotGroupId(
				scopeKey, objectDefinition.getCompanyId(),
				_depotEntryLocalService, _groupLocalService);
		}

		return 0;
	}

	private String _getObjectEntriesPermissionName(long objectDefinitionId) {
		return ObjectConstants.RESOURCE_NAME + "#" + objectDefinitionId;
	}

	private String _getObjectEntryPermissionName(long objectDefinitionId) {
		return ObjectDefinition.class.getName() + "#" + objectDefinitionId;
	}

	private Date _toDate(Locale locale, String valueString) {
		if (Validator.isNull(valueString)) {
			return null;
		}

		try {
			return DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm:ss'Z'", valueString, locale);
		}
		catch (ParseException parseException1) {
			try {
				return DateUtil.parseDate("yyyy-MM-dd", valueString, locale);
			}
			catch (ParseException parseException2) {
				throw new BadRequestException(
					"Unable to parse date that does not conform to ISO-8601",
					parseException2);
			}
		}
	}

	private Filter _toFilter(
		String filterString, Locale locale, Long objectDefinitionId) {

		try {
			EntityModel entityModel = new ObjectEntryEntityModel(
				_objectFieldLocalService.getObjectFields(objectDefinitionId));

			FilterParser filterParser = _filterParserProvider.provide(
				entityModel);

			com.liferay.portal.odata.filter.Filter oDataFilter =
				new com.liferay.portal.odata.filter.Filter(
					filterParser.parse(filterString));

			return _expressionConvert.convert(
				oDataFilter.getExpression(), locale, entityModel);
		}
		catch (Exception exception) {
			_log.error("Invalid filter " + filterString, exception);
		}

		return null;
	}

	private ObjectEntry _toObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry objectEntry)
		throws Exception {

		Optional<UriInfo> uriInfoOptional =
			dtoConverterContext.getUriInfoOptional();

		UriInfo uriInfo = uriInfoOptional.orElse(null);

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				dtoConverterContext.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					ActionUtil.addAction(
						ActionKeys.DELETE, ObjectEntryResourceImpl.class,
						objectEntry.getObjectEntryId(), "deleteObjectEntry",
						null, objectEntry.getUserId(),
						_getObjectEntryPermissionName(
							objectEntry.getObjectDefinitionId()),
						objectEntry.getGroupId(), uriInfo)
				).put(
					"get",
					ActionUtil.addAction(
						ActionKeys.VIEW, ObjectEntryResourceImpl.class,
						objectEntry.getObjectEntryId(), "getObjectEntry", null,
						objectEntry.getUserId(),
						_getObjectEntryPermissionName(
							objectEntry.getObjectDefinitionId()),
						objectEntry.getGroupId(), uriInfo)
				).put(
					"permissions",
					ActionUtil.addAction(
						ActionKeys.PERMISSIONS, ObjectEntryResourceImpl.class,
						objectEntry.getObjectEntryId(), "patchObjectEntry",
						null, objectEntry.getUserId(),
						_getObjectEntryPermissionName(
							objectEntry.getObjectDefinitionId()),
						objectEntry.getGroupId(), uriInfo)
				).put(
					"update",
					ActionUtil.addAction(
						ActionKeys.UPDATE, ObjectEntryResourceImpl.class,
						objectEntry.getObjectEntryId(), "putObjectEntry", null,
						objectEntry.getUserId(),
						_getObjectEntryPermissionName(
							objectEntry.getObjectDefinitionId()),
						objectEntry.getGroupId(), uriInfo)
				).build(),
				dtoConverterContext.getDTOConverterRegistry(),
				dtoConverterContext.getHttpServletRequest(),
				objectEntry.getObjectEntryId(), dtoConverterContext.getLocale(),
				uriInfo, dtoConverterContext.getUser());

		defaultDTOConverterContext.setAttribute(
			"objectDefinition", objectDefinition);

		return _objectEntryDTOConverter.toDTO(
			defaultDTOConverterContext, objectEntry);
	}

	private Map<String, Serializable> _toObjectValues(
		long objectDefinitionId, Map<String, Object> properties,
		Locale locale) {

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(objectDefinitionId);

		Map<String, Serializable> values = new HashMap<>();

		for (ObjectField objectField : objectFields) {
			String name = objectField.getName();

			Object object = properties.get(name);

			if (object == null) {
				continue;
			}

			if (Objects.equals(objectField.getDBType(), "Date")) {
				values.put(name, _toDate(locale, String.valueOf(object)));
			}

			if (objectField.getListTypeDefinitionId() != 0) {
				Map<String, String> map = (HashMap<String, String>)object;

				values.put(name, map.get("key"));
			}
			else {
				values.put(name, (Serializable)object);
			}
		}

		return values;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryManagerImpl.class);

	@Reference
	private Aggregations _aggregations;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference(
		target = "(result.class.name=com.liferay.portal.kernel.search.filter.Filter)"
	)
	private ExpressionConvert<Filter> _expressionConvert;

	@Reference
	private FilterParserProvider _filterParserProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryDTOConverter _objectEntryDTOConverter;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private Queries _queries;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private final ObjectMapper _objectMapper = new ObjectMapper();

	private static final DateFormat _dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
}