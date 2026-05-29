/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.ai.hub.pricing.rest.client.dto.v1_0.QuotaBlock;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectFieldValidationConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-62272"))
@RunWith(Arquillian.class)
public class QuotaBlockResourceTest extends BaseQuotaBlockResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.ai.hub.site.initializer");

		siteInitializer.initialize(TestPropsValues.getGroupId());
	}

	@AfterClass
	public static void tearDownClass() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
		ServiceContextThreadLocal.popServiceContext();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_QUOTA", TestPropsValues.getCompanyId());

		_objectEntryLocalService.addOrUpdateObjectEntry(
			"quota-" + _accountEntry.getAccountEntryId(), 0,
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			HashMapBuilder.<String, Serializable>put(
				"limit",
				RandomTestUtil.randomLong(
					ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
					ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX)
			).put(
				"r_accountToAIHubQuotas_accountEntryId",
				_accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Override
	@Test
	public void testPostAccountQuotaBlockPurchase() throws Exception {
		super.testPostAccountQuotaBlockPurchase();

		QuotaBlock postQuotaBlock =
			testPostAccountQuotaBlockPurchase_addQuotaBlock(
				new QuotaBlock() {
					{
						setSize(10000);
						setTransactionId("Test");
					}
				});

		Assert.assertEquals(
			0.6, postQuotaBlock.getAiHubQuotaConversionTableVersion(), 0.0001);
		Assert.assertEquals("Test", postQuotaBlock.getExternalReferenceCode());

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(postQuotaBlock.getPurchaseDate());

		calendar.add(Calendar.MONTH, 12);

		Assert.assertEquals(
			calendar.getTime(), postQuotaBlock.getPurchaseExpirationDate());

		Assert.assertEquals(10000, (int)postQuotaBlock.getRemainingBalance());
		Assert.assertEquals(10000, (int)postQuotaBlock.getSize());
		Assert.assertEquals("Test", postQuotaBlock.getTransactionId());
	}

	@Override
	protected QuotaBlock randomQuotaBlock() throws Exception {
		return new QuotaBlock() {
			{
				setSize(RandomTestUtil::randomInt);
				setTransactionId(RandomTestUtil::randomString);
			}
		};
	}

	@Override
	protected QuotaBlock testPostAccountQuotaBlockPurchase_addQuotaBlock(
			QuotaBlock quotaBlock)
		throws Exception {

		return quotaBlockResource.postAccountQuotaBlockPurchase(
			_accountEntry.getAccountEntryId(), quotaBlock);
	}

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}