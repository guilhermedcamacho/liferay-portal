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

package com.liferay.object.storage.sugarcrm.internal.rest.manager.v1_0;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.Link;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.dto.v1_0.util.CreatorUtil;
import com.liferay.object.rest.manager.v1_0.BaseObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.storage.sugarcrm.internal.constants.SugarCRMObjectConstants;
import com.liferay.object.storage.sugarcrm.internal.http.SugarCRMHttp;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.UnsupportedEncodingException;

import java.math.BigDecimal;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Maurice Sepe
 */
@Component(
    /* TODO: Add new entry to ObjectDefinitionConstants */
	property = "object.entry.manager.storage.type=" + "sugarcrm",
	service = ObjectEntryManager.class
)
public class SugarCRMObjectEntryManagerImpl
	extends BaseObjectEntryManager implements ObjectEntryManager {

	@Override
	public ObjectEntry addObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		_sugarcrmHttp.post(
			objectDefinition.getCompanyId(),
			getGroupId(objectDefinition, scopeKey),
			_getObjectLocation(objectDefinition),
			_toJSONObject(objectDefinition, objectEntry));

		return null;
	}

	@Override
	public void deleteObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.DELETE, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		_sugarcrmHttp.delete(
			companyId, getGroupId(objectDefinition, scopeKey),
			objectDefinition.getExternalReferenceCode() + "/" +
				externalReferenceCode);

		return;
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			String filterString, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.VIEW, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		// implement aggregation, filterString parameter

		return _getObjectEntries(
			companyId, objectDefinition, scopeKey, dtoConverterContext,
			filterString, pagination, search, sorts);
	}

	@Override
	public ObjectEntry getObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.VIEW, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		JSONObject jsonObject = _sugarcrmHttp.get(
			companyId, getGroupId(objectDefinition, scopeKey),
			StringBundler.concat(
				_getObjectLocation(objectDefinition), "/",
				externalReferenceCode));

		return _toObjectEntry(
			companyId, _getDateFormat(), dtoConverterContext, jsonObject,
			objectDefinition);
	}

	@Override
	public String getStorageLabel(Locale locale) {

		/* Add defintion "sugarcrm" to ObjectDefinitionConstants.java and lang keys */

		return language.get(locale, "SugarCRM");
	}

	@Override
	public String getStorageType() {

		/* Add defintion "sugarcrm" to ObjectDefinitionConstants.java and lang keys */

		return "SugarCRM";
	}

	@Override
	public ObjectEntry updateObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.UPDATE, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		// TODO Auto-generated method stub

		throw new UnsupportedOperationException(
			"Unimplemented method 'updateObjectEntry'");
	}

	private String _getAttachmentHref(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		ObjectField objectField) {

		StringBuilder sb = new StringBuilder();

		sb.append("/o/");
		sb.append(SugarCRMObjectConstants.SERVLET_PATH);
		sb.append(StringPool.FORWARD_SLASH);
		sb.append(objectDefinition.getExternalReferenceCode());
		sb.append(StringPool.FORWARD_SLASH);
		sb.append(objectEntry.getExternalReferenceCode());
		sb.append(StringPool.FORWARD_SLASH);
		sb.append("file");
		sb.append(StringPool.FORWARD_SLASH);
		sb.append(objectField.getExternalReferenceCode());

		return sb.toString();
	}

	private DateFormat _getDateFormat() {
		return new SimpleDateFormat(_dateFormat);
	}

	private Page<ObjectEntry> _getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			DTOConverterContext dtoConverterContext, String filterString,
			Pagination pagination, String search, Sort[] sorts)
		throws Exception {

		JSONObject responseJSONObject = _sugarcrmHttp.get(
			companyId, getGroupId(objectDefinition, scopeKey),
			_getPreparedLocation(
				objectDefinition, filterString, pagination, search, sorts));

		if ((responseJSONObject == null) ||
			(responseJSONObject.length() == 0)) {

			return Page.of(Collections.emptyList());
		}

		JSONArray jsonArray = responseJSONObject.getJSONArray("records");

		return Page.of(
			_toObjectEntries(
				companyId, dtoConverterContext, jsonArray, objectDefinition),
			pagination,
			_getTotalCount(
				companyId, objectDefinition, scopeKey, filterString, pagination,
				search, sorts));
	}

	private ObjectField _getObjectFieldByExternalReferenceCode(
		String externalReferenceCode, List<ObjectField> objectFields) {

		for (ObjectField objectField : objectFields) {
			if (Objects.equals(
					externalReferenceCode,
					objectField.getExternalReferenceCode())) {

				return objectField;
			}
		}

		return null;
	}

	private ObjectField _getObjectFieldByName(
		String name, List<ObjectField> objectFields) {

		for (ObjectField objectField : objectFields) {
			if (Objects.equals(name, objectField.getName())) {
				return objectField;
			}
		}

		return null;
	}

	private String _getObjectLocation(ObjectDefinition objectDefinition) {
		return objectDefinition.getExternalReferenceCode();
	}

	private String _getPreparedLocation(
		ObjectDefinition objectDefinition, String filterString,
		Pagination pagination, String search, Sort[] sorts) {

		String preparedLocation = StringBundler.concat(
			_getObjectLocation(objectDefinition),
			_getPreparedParameters(filterString, pagination, search, sorts));

		return preparedLocation;
	}

	private String _getPreparedParameters(
		String filterString, Pagination pagination, String search,
		Sort[] sorts) {

		try {
			StringBuilder sb = new StringBuilder("?");

			if (!filterString.trim(
				).isEmpty()) {

				sb.append("filter=");
				sb.append(
					URLEncoder.encode(
						filterString, StandardCharsets.UTF_8.toString()));
				sb.append("&&");
			}

			System.out.println(sorts);

			/* TODO: Add implementation for sort and search*/

			sb.append(_getSugarCRMPagination(pagination));

			return sb.toString();
		}
		catch (UnsupportedEncodingException ignore) {
		}

		return StringPool.BLANK;
	}

	private String _getSugarCRMPagination(Pagination pagination) {
		int offset = (pagination.getPage() - 1) * pagination.getPageSize();
		int max_num = pagination.getPageSize();

		return StringBundler.concat(
			"offset=", String.valueOf(offset), "&&", "max_num=",
			String.valueOf(max_num));
	}

	private int _getTotalCount(
		long companyId, ObjectDefinition objectDefinition, String scopeKey,
		String filterString, Pagination pagination, String search,
		Sort[] sorts) {

		JSONObject responseJSONObject = _sugarcrmHttp.get(
			companyId, getGroupId(objectDefinition, scopeKey),
			StringBundler.concat(
				_getObjectLocation(objectDefinition), "/count",
				_getPreparedParameters(
					filterString, pagination, search, sorts)));

		return responseJSONObject.getInt("record_count", 0);
	}

	private JSONObject _toJSONObject(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		Map<String, Object> map = new HashMap<>();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		Map<String, Object> properties = objectEntry.getProperties();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			ObjectField objectField = _getObjectFieldByName(
				entry.getKey(), objectFields);

			if (objectField == null) {
				continue;
			}

			Object value = entry.getValue();

			if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

				Map<String, String> valueMap = (HashMap<String, String>)value;

				ListTypeEntry listTypeEntry =
					_listTypeEntryLocalService.getListTypeEntry(
						objectField.getListTypeDefinitionId(),
						valueMap.get("key"));

				value = listTypeEntry.getExternalReferenceCode();
			}

			map.put(
				objectField.getExternalReferenceCode(),
				Objects.equals(value, StringPool.BLANK) ? null : value);

			if (Objects.equals(
					objectField.getObjectFieldId(),
					objectDefinition.getTitleObjectFieldId())) {

				map.put("Name", value);
			}
		}

		return _jsonFactory.createJSONObject(_jsonFactory.looseSerialize(map));
	}

	private List<ObjectEntry> _toObjectEntries(
			long companyId, DTOConverterContext dtoConverterContext,
			JSONArray jsonArray, ObjectDefinition objectDefinition)
		throws Exception {

		DateFormat dateFormat = _getDateFormat();

		return JSONUtil.toList(
			jsonArray,
			jsonObject -> _toObjectEntry(
				companyId, dateFormat, dtoConverterContext, jsonObject,
				objectDefinition));
	}

	private ObjectEntry _toObjectEntry(
			long companyId, DateFormat dateFormat,
			DTOConverterContext dtoConverterContext, JSONObject jsonObject,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry objectEntry = new ObjectEntry() {
			{
				actions = HashMapBuilder.put(
					"delete", Collections.<String, String>emptyMap()
				).build();
				creator = CreatorUtil.toCreator(
					_portal, null,
					_userLocalService.fetchUserByExternalReferenceCode(
						jsonObject.getString("created_by"), companyId));

				dateCreated = dateFormat.parse(
					jsonObject.getString("date_entered"));
				dateModified = dateFormat.parse(
					jsonObject.getString("date_modified"));
				externalReferenceCode = jsonObject.getString("id");
				status = new Status() {
					{
						code = 0;
						label = "approved";
						label_i18n = "Approved";
					}
				};
			}
		};

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			ObjectField objectField = _getObjectFieldByExternalReferenceCode(
				key, objectFields);

			if (objectField == null) {
				continue;
			}

			Map<String, Object> properties = objectEntry.getProperties();

			if (jsonObject.isNull(key)) {
				properties.put(objectField.getName(), null);

				continue;
			}

			Object value = jsonObject.get(key);

			if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER) ||
				Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER)) {

				if (value instanceof BigDecimal) {
					BigDecimal bigDecimalValue = (BigDecimal)value;

					value = bigDecimalValue.toBigInteger();
				}
			}
			else if (Objects.equals(
						objectField.getBusinessType(),
						ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

				ListTypeEntry listTypeEntry =
					_listTypeEntryLocalService.
						fetchListTypeEntryByExternalReferenceCode(
							(String)value, objectDefinition.getCompanyId(),
							objectField.getListTypeDefinitionId());

				if (listTypeEntry == null) {
					continue;
				}

				value = new ListEntry() {
					{
						key = listTypeEntry.getKey();
						name = listTypeEntry.getName(
							dtoConverterContext.getLocale());
						name_i18n = LocalizedMapUtil.getI18nMap(
							dtoConverterContext.isAcceptAllLanguages(),
							listTypeEntry.getNameMap());
					}
				};
			}
			else if (Objects.equals(
						objectField.getBusinessType(),
						ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				if ((value != null) && !value.equals(StringPool.BLANK)) {
					String hrefString = _getAttachmentHref(
						objectDefinition, objectEntry, objectField);

					value = new FileEntry() {
						{
							link = new Link() {
								{
									href = hrefString;
									name = jsonObject.get(
										key
									).toString();
								}
							};
						}
					};
				}
			}

			properties.put(objectField.getName(), value);
		}

		return objectEntry;
	}

	private final String _dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX";

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SugarCRMHttp _sugarcrmHttp;

	@Reference
	private UserLocalService _userLocalService;

}