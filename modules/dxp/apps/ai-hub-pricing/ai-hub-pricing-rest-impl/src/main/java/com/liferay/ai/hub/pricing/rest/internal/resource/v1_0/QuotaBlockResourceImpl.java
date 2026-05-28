/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.rest.internal.resource.v1_0;

import com.liferay.ai.hub.pricing.rest.dto.v1_0.QuotaBlock;
import com.liferay.ai.hub.pricing.rest.resource.v1_0.QuotaBlockResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carolina Barbosa
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/quota-block.properties",
	scope = ServiceScope.PROTOTYPE, service = QuotaBlockResource.class
)
public class QuotaBlockResourceImpl extends BaseQuotaBlockResourceImpl {

	@Override
	public QuotaBlock postAccountQuotaBlockPurchase(
			Long accountId, QuotaBlock quotaBlock)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA_BLOCK", contextCompany.getCompanyId());

		Map<String, Serializable> conversionTableValues =
			_getConversionTableValues();
		Date purchaseDate = new Date();
		String transactionId = quotaBlock.getTransactionId();

		ObjectEntry objectEntry = _objectEntryService.addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", transactionId
			).put(
				"purchaseDate", purchaseDate
			).put(
				"purchaseExpirationDate",
				() -> {
					Calendar calendar = Calendar.getInstance();

					calendar.setTime(purchaseDate);

					calendar.add(Calendar.MONTH, 12);

					return calendar.getTime();
				}
			).put(
				"r_quotaCTToQuotaBlocks_l_aiHubQuotaConversionTableId",
				MapUtil.getLong(
					conversionTableValues, "l_aiHubQuotaConversionTableId")
			).put(
				"r_quotaToQuotaBlocks_l_aiHubQuotaId",
				() -> {
					ObjectDefinition quotaObjectDefinition =
						_objectDefinitionLocalService.
							getObjectDefinitionByExternalReferenceCode(
								"L_AI_HUB_QUOTA",
								contextCompany.getCompanyId());

					ObjectEntry quotaObjectEntry =
						_objectEntryService.getObjectEntry(
							"quota-" + accountId, 0,
							quotaObjectDefinition.getObjectDefinitionId());

					return quotaObjectEntry.getObjectEntryId();
				}
			).put(
				"remainingBalance", quotaBlock.getSize()
			).put(
				"size", quotaBlock.getSize()
			).put(
				"transactionId", transactionId
			).build(),
			_getServiceContext());

		Map<String, Serializable> values = objectEntry.getValues();

		return new QuotaBlock() {
			{
				setAiHubQuotaConversionTableVersion(
					() -> MapUtil.getDouble(conversionTableValues, "version"));
				setExternalReferenceCode(objectEntry::getExternalReferenceCode);
				setId(objectEntry::getObjectEntryId);
				setPurchaseDate(() -> (Date)values.get("purchaseDate"));
				setPurchaseExpirationDate(
					() -> (Date)values.get("purchaseExpirationDate"));
				setRemainingBalance(
					() -> MapUtil.getInteger(values, "remainingBalance"));
				setSize(() -> MapUtil.getInteger(values, "size"));
				setTransactionId(
					() -> MapUtil.getString(values, "transactionId"));
			}
		};
	}

	private Map<String, Serializable> _getConversionTableValues()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA_CONVERSION_TABLE",
					contextCompany.getCompanyId());

		List<Map<String, Serializable>> valuesList =
			_objectEntryLocalService.getValuesList(
				0, contextCompany.getCompanyId(), contextUser.getUserId(),
				objectDefinition.getObjectDefinitionId(), null, null, 0, 1,
				new Sort[] {new Sort("version", Sort.DOUBLE_TYPE, true)});

		return valuesList.get(0);
	}

	private ServiceContext _getServiceContext() {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setUserId(contextUser.getUserId());

		return serviceContext;
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}