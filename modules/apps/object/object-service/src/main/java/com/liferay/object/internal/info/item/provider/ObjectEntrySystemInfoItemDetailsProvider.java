/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.info.item.provider;

import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.LocaleUtil;

/**
 * @author Guilherme Camacho
 */
public class ObjectEntrySystemInfoItemDetailsProvider
	implements InfoItemDetailsProvider<BaseModel<?>> {

	public ObjectEntrySystemInfoItemDetailsProvider(
		ObjectDefinition objectDefinition) {

		_objectDefinition = objectDefinition;
	}

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(
			_objectDefinition.getClassName(),
			InfoLocalizedValue.<String>builder(
			).defaultLocale(
				LocaleUtil.fromLanguageId(
					_objectDefinition.getDefaultLanguageId())
			).values(
				_objectDefinition.getLabelMap()
			).build());
	}

	@Override
	public InfoItemDetails getInfoItemDetails(BaseModel<?> baseModel) {

		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				_objectDefinition.getClassName(),
				(long)baseModel.getPrimaryKeyObj()));
	}

	private final ObjectDefinition _objectDefinition;

}