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

package com.liferay.object.storage.salesforce.internal.rest.manager.v1_0;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.dto.v1_0.util.CreatorUtil;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.storage.salesforce.internal.client.SalesforceClient;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Guilherme Camacho
 */
@Component(
	immediate = true,
	property = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
	service = ObjectEntryManager.class
)
public class SalesforceObjectEntryManagerImpl implements ObjectEntryManager {

	@Override
	public ObjectEntry addObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey)
		throws Exception {

		return null;
	}

	@Override
	public ObjectEntry addObjectRelationshipMappingTableValues(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, String objectRelationshipName,
			long primaryKey1, long primaryKey2)
		throws Exception {

		return null;
	}

	@Override
	public ObjectEntry addOrUpdateObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, String scopeKey)
		throws Exception {

		DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		return _toObjectEntry(
			companyId, dateFormat,
			_salesforceClient.updateSObject(
				externalReferenceCode,
				_toSalesforceEntry(objectDefinition, objectEntry),
				_getSalesforceObjectName(objectDefinition.getName())));
	}

	@Override
	public void deleteObjectEntry(
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {
	}

	@Override
	public void deleteObjectEntry(
			String externalReferenceCode, long companyId,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {
	}

	@Override
	public ObjectEntry fetchObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		return null;
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			Filter filter, Pagination pagination, String search, Sort[] sorts)
		throws Exception {

		JSONObject responseJSONObject1 = _salesforceClient.query(
			"SELECT FIELDS(ALL) FROM " +
				_getSalesforceObjectName(objectDefinition.getName()) +
					_getSalesforcePagination(pagination));

		if ((responseJSONObject1 == null) ||
			(responseJSONObject1.length() == 0)) {

			return Page.of(Collections.emptyList());
		}

		JSONObject responseJSONObject2 = _salesforceClient.query(
			"SELECT COUNT(Id) FROM " +
				_getSalesforceObjectName(objectDefinition.getName()));

		JSONArray jsonArray = responseJSONObject2.getJSONArray("records");

		return Page.of(
			_toObjectEntries(
				companyId, responseJSONObject1.getJSONArray("records")),
			pagination,
			jsonArray.getJSONObject(
				0
			).getInt(
				"expr0"
			));
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			Pagination pagination, Predicate predicate, String search,
			Sort[] sorts)
		throws Exception {

		return null;
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			String filterString, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception {

		return null;
	}

	@Override
	public ObjectEntry getObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		return null;
	}

	@Override
	public ObjectEntry getObjectEntry(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, long companyId,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {

		DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		return _toObjectEntry(
			companyId, dateFormat,
			_salesforceClient.retrieveSObject(
				externalReferenceCode,
				_getSalesforceObjectName(objectDefinition.getName())));
	}

	@Override
	public Page<ObjectEntry> getObjectEntryRelatedObjectEntries(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, Long objectEntryId,
			String objectRelationshipName, Pagination pagination)
		throws Exception {

		return null;
	}

	@Override
	public ObjectEntry updateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId,
			ObjectEntry objectEntry)
		throws Exception {

		return null;
	}

	private ObjectField _getObjectField(
		String name, List<ObjectField> objectFields) {

		for (ObjectField objectField : objectFields) {
			if (Objects.equals(name, objectField.getName())) {
				return objectField;
			}
		}

		return null;
	}

	private String _getSalesforceObjectName(String objectDefinitionName) {
		return StringUtil.removeFirst(objectDefinitionName, "C_") +
			_SOBJECT_NAME_SUFFIX;
	}

	private String _getSalesforcePagination(Pagination pagination) {
		return StringBundler.concat(
			" LIMIT ", pagination.getPageSize(), " OFFSET ",
			pagination.getStartPosition());
	}

	private List<ObjectEntry> _toObjectEntries(
			long companyId, JSONArray jsonArray)
		throws Exception {

		DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		return JSONUtil.toList(
			jsonArray,
			jsonObject -> _toObjectEntry(companyId, dateFormat, jsonObject));
	}

	private ObjectEntry _toObjectEntry(
			long companyId, DateFormat dateFormat, JSONObject jsonObject)
		throws Exception {

		ObjectEntry objectEntry = new ObjectEntry() {
			{
				actions = Collections.emptyMap();
				creator = CreatorUtil.toCreator(
					_portal, Optional.empty(),
					_userLocalService.fetchUserByExternalReferenceCode(
						companyId, jsonObject.getString("OwnerId")));
				dateCreated = dateFormat.parse(
					jsonObject.getString("CreatedDate"));
				dateModified = dateFormat.parse(
					jsonObject.getString("LastModifiedDate"));
				externalReferenceCode = jsonObject.getString("Id");
				status = new Status() {
					{
						code = 0;
						label = "approved";
						label_i18n = "Approved";
					}
				};
			}
		};

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			if (StringUtil.contains(
					key, _SOBJECT_NAME_SUFFIX, StringPool.BLANK)) {

				String customFieldName = StringUtil.removeLast(
					key, _SOBJECT_NAME_SUFFIX);

				customFieldName = StringUtil.removeSubstring(
					customFieldName, "_");

				customFieldName = StringUtil.lowerCaseFirstLetter(
					customFieldName);

				Map<String, Object> properties = objectEntry.getProperties();

				properties.put(
					customFieldName,
					jsonObject.isNull(key) ? null : jsonObject.get(key));
			}
		}

		return objectEntry;
	}

	private JSONObject _toSalesforceEntry(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		ObjectField titleObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getTitleObjectFieldId());

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		Map<String, Object> properties = objectEntry.getProperties();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			ObjectField objectField = _getObjectField(
				entry.getKey(), objectFields);

			jsonObject.put(
				entry.getKey() + _SOBJECT_NAME_SUFFIX, entry.getValue());

			if ((titleObjectField != null) &&
				Objects.equals(titleObjectField.getName(), entry.getKey())) {

				jsonObject.put("Name", entry.getValue());
			}
		}

		return jsonObject;
	}

	private static final String _SOBJECT_NAME_SUFFIX = "__c";

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SalesforceClient _salesforceClient;

	@Reference
	private UserLocalService _userLocalService;

}