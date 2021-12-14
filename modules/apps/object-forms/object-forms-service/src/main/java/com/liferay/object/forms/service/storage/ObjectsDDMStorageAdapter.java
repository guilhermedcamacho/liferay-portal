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

package com.liferay.dynamic.data.mapping.internal.storage;

import com.liferay.dynamic.data.mapping.exception.StorageException;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapter;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterDeleteRequest;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterDeleteResponse;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterGetRequest;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterGetResponse;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterSaveRequest;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterSaveResponse;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	immediate = true, property = "ddm.storage.adapter.type=objects",
	service = DDMStorageAdapter.class
)
public class ObjectsDDMStorageAdapter implements DDMStorageAdapter {

	@Override
	public DDMStorageAdapterDeleteResponse delete(
			DDMStorageAdapterDeleteRequest ddmStorageAdapterDeleteRequest)
		throws StorageException {

		throw new UnsupportedOperationException("Not supported yet");
	}

	@Override
	public DDMStorageAdapterGetResponse get(
			DDMStorageAdapterGetRequest ddmStorageAdapterGetRequest)
		throws StorageException {

		throw new UnsupportedOperationException("Not supported yet");
	}

	@Override
	public DDMStorageAdapterSaveResponse save(
			DDMStorageAdapterSaveRequest ddmStorageAdapterSaveRequest)
		throws StorageException {

		DDMStorageAdapterSaveResponse ddmStorageAdapterSaveResponse =
			_ddmStorageAdapter.save(ddmStorageAdapterSaveRequest);

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionLocalService.getObjectDefinitions(0, 1000);

		Stream<ObjectDefinition> stream = objectDefinitions.stream();

		Optional<ObjectDefinition> objectDefinitionOptional = stream.filter(
			objectDefinition -> StringUtil.equals(
				objectDefinition.getName(),
				_getObjectName(ddmStorageAdapterSaveRequest.getStructureId()))
		).findFirst();

		DDMFormValues ddmFormValues =
			ddmStorageAdapterSaveRequest.getDDMFormValues();

		ObjectDefinition objectDefinition;

		if (objectDefinitionOptional.isPresent()) {
			objectDefinition = objectDefinitionOptional.get();
		}
		else {
			throw new StorageException("Object does not exist");
		}

		HashMap<String, Serializable> map = new HashMap<>();

		for (DDMFormFieldValue ddmFormValue :
				ddmFormValues.getDDMFormFieldValues()) {

			Value value = ddmFormValue.getValue();

			Map<Locale, String> values = value.getValues();

			map.put(
				_getObjectFieldName(ddmFormValue),
				values.get(value.getDefaultLocale()));
		}

		map.put("ddmStorageId", ddmStorageAdapterSaveResponse.getPrimaryKey());

		try {
			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				ddmStorageAdapterSaveRequest.getUserId(),
				ddmStorageAdapterSaveRequest.getScopeGroupId(),
				objectDefinition.getObjectDefinitionId(), map,
				new ServiceContext());

			return DDMStorageAdapterSaveResponse.Builder.newBuilder(
				objectEntry.getPrimaryKey()
			).build();
		}
		catch (PortalException portalException) {
			throw new StorageException(portalException);
		}
	}

	private String _getObjectFieldName(DDMFormFieldValue ddmFormValue) {
		DDMFormField ddmFormField = ddmFormValue.getDDMFormField();

		return StringUtil.toLowerCase(ddmFormField.getName());
	}

	private String _getObjectName(Long ddmStructureId) {
		return "Structure" + ddmStructureId;
	}

	@Reference(target = "(ddm.storage.adapter.type=default)")
	private DDMStorageAdapter _ddmStorageAdapter;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}