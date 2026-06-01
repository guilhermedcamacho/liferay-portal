/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.internal.quota;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.CountTokensResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.configuration.VertexAIConfiguration;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = QuotaManager.class)
public class AIHubPricingQuotaManagerImpl implements QuotaManager {

	@Override
	public void addQuotas(long accountEntryId, long companyId, long userId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA", companyId);

		_addQuotaObjectEntry(
			accountEntryId, companyId, "guest-quota-" + accountEntryId,
			objectDefinition, userId);
		_addQuotaObjectEntry(
			accountEntryId, companyId, "quota-" + accountEntryId,
			objectDefinition, userId);
	}

	@Override
	public long checkUsage(long companyId, String text, long userId)
		throws PortalException {

		ObjectEntry objectEntry = _fetchQuotaObjectEntry(companyId, userId);

		if (objectEntry == null) {
			return 0L;
		}

		long inputTokensCount = _getInputTokensCount(companyId, text);

		long preDebitedTokens =
			inputTokensCount + QuotaManager.MAX_OUTPUT_TOKENS_RESERVATION;

		long milliLRTCount = LiferayTokenConverter.convert(
			Map.of(
				TokenSource.VERTEX_INPUT, inputTokensCount,
				TokenSource.VERTEX_OUTPUT,
				(long)QuotaManager.MAX_OUTPUT_TOKENS_RESERVATION));

		try (Closeable closeable = _lock(objectEntry.getObjectEntryId())) {
			objectEntry = _objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

			long lrtUsage =
				MapUtil.getLong(objectEntry.getValues(), "lrtUsage") +
					milliLRTCount;
			long usage =
				MapUtil.getLong(objectEntry.getValues(), "usage") +
					preDebitedTokens;

			if ((usage > MapUtil.getLong(objectEntry.getValues(), "limit")) ||
				(lrtUsage > MapUtil.getLong(
					objectEntry.getValues(), "lrtLimit"))) {

				throw new UnsupportedOperationException(
					"You have exceeded your token quota");
			}

			_partialUpdateObjectEntry(
				companyId, lrtUsage, objectEntry, usage, userId);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return preDebitedTokens;
	}

	@Override
	public void updateUsage(
			long companyId, long inputTokensCount, long outputTokensCount,
			long preDebitedTokens, long userId)
		throws PortalException {

		ObjectEntry objectEntry = _fetchQuotaObjectEntry(companyId, userId);

		if (objectEntry == null) {
			return;
		}

		long milliLRTCount = LiferayTokenConverter.convert(
			Map.of(
				TokenSource.VERTEX_INPUT, inputTokensCount,
				TokenSource.VERTEX_OUTPUT, outputTokensCount));

		long preDebitedMilliLRTCount = 0L;

		if (preDebitedTokens > 0L) {
			preDebitedMilliLRTCount = LiferayTokenConverter.convert(
				Map.of(
					TokenSource.VERTEX_INPUT,
					preDebitedTokens -
						QuotaManager.MAX_OUTPUT_TOKENS_RESERVATION,
					TokenSource.VERTEX_OUTPUT,
					(long)QuotaManager.MAX_OUTPUT_TOKENS_RESERVATION));
		}

		long milliLRTDelta = milliLRTCount - preDebitedMilliLRTCount;
		long tokensCount =
			(inputTokensCount + outputTokensCount) - preDebitedTokens;

		try (Closeable closeable = _lock(objectEntry.getObjectEntryId())) {
			objectEntry = _objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

			_partialUpdateObjectEntry(
				companyId,
				MapUtil.getLong(objectEntry.getValues(), "lrtUsage") +
					milliLRTDelta,
				objectEntry,
				MapUtil.getLong(objectEntry.getValues(), "usage") + tokensCount,
				userId);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _addQuotaObjectEntry(
			long accountEntryId, long companyId, String externalReferenceCode,
			ObjectDefinition objectDefinition, long userId)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, 0, objectDefinition.getObjectDefinitionId());

		if (objectEntry != null) {
			return;
		}

		_objectEntryLocalService.addObjectEntry(
			0, userId, objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"limit", _QUOTA_TOKEN_LIMIT
			).put(
				"lrtLimit", _QUOTA_LRT_LIMIT_MILLI
			).put(
				"lrtUsage", 0
			).put(
				"r_accountToAIHubQuotas_accountEntryId", accountEntryId
			).put(
				"usage", 0
			).build(),
			_getServiceContext(companyId, userId));
	}

	private ObjectEntry _fetchQuotaObjectEntry(long companyId, long userId)
		throws PortalException {

		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			userId);

		if (accountEntry == null) {
			return null;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA", companyId);

		if (objectDefinition == null) {
			return null;
		}

		User user = _userLocalService.getUser(userId);

		String externalReferenceCode =
			"quota-" + accountEntry.getAccountEntryId();

		if (user.isServiceAccountUser()) {
			externalReferenceCode =
				"guest-quota-" + accountEntry.getAccountEntryId();
		}

		return _objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, 0, objectDefinition.getObjectDefinitionId());
	}

	private long _getInputTokensCount(long companyId, String text)
		throws PortalException {

		VertexAIConfiguration vertexAIConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				VertexAIConfiguration.class, companyId);

		String location = vertexAIConfiguration.location();

		if (Objects.equals(location, "global")) {
			location = _COUNT_TOKENS_FALLBACK_LOCATION;
		}

		try (VertexAI vertexAI = new VertexAI(
				vertexAIConfiguration.projectId(), location)) {

			GenerativeModel generativeModel = new GenerativeModel(
				vertexAIConfiguration.modelName(), vertexAI);

			CountTokensResponse countTokensResponse =
				generativeModel.countTokens(text);

			return countTokensResponse.getTotalTokens();
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	private ServiceContext _getServiceContext(long companyId, long userId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return serviceContext;
	}

	private Closeable _lock(long objectEntryId) throws PortalException {
		String updatedOwner = PortalUUIDUtil.generate();

		long deadline = System.currentTimeMillis() + (10 * Time.SECOND);

		while (true) {
			Lock lock = LockManagerUtil.lock(
				AIHubPricingQuotaManagerImpl.class.getName(),
				String.valueOf(objectEntryId), null, updatedOwner);

			if (Objects.equals(lock.getOwner(), updatedOwner)) {
				break;
			}

			if (System.currentTimeMillis() >= deadline) {
				throw new PortalException(new TimeoutException());
			}

			try {
				Thread.sleep(50);
			}
			catch (InterruptedException interruptedException) {
				Thread thread = Thread.currentThread();

				thread.interrupt();

				throw new PortalException(interruptedException);
			}
		}

		return () -> LockManagerUtil.unlock(
			AIHubPricingQuotaManagerImpl.class.getName(),
			String.valueOf(objectEntryId), updatedOwner);
	}

	private void _partialUpdateObjectEntry(
			long companyId, long lrtUsage, ObjectEntry objectEntry, long usage,
			long userId)
		throws PortalException {

		_objectEntryLocalService.partialUpdateObjectEntry(
			userId, objectEntry.getObjectEntryId(), 0,
			HashMapBuilder.<String, Serializable>put(
				"lrtUsage", lrtUsage
			).put(
				"usage", usage
			).build(),
			_getServiceContext(companyId, userId));
	}

	private static final String _COUNT_TOKENS_FALLBACK_LOCATION =
		"europe-central2";

	private static final long _QUOTA_LRT_LIMIT_MILLI = 36141000;

	private static final int _QUOTA_TOKEN_LIMIT = 33333333;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}