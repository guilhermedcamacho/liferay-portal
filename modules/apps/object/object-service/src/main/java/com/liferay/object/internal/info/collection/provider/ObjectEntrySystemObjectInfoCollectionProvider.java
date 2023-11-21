/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.info.collection.provider;

import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ObjectEntrySystemObjectInfoCollectionProvider
	implements SingleFormVariationInfoCollectionProvider<BaseModel<?>> {

	public ObjectEntrySystemObjectInfoCollectionProvider(
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;
	}

	@Override
	public InfoPage<BaseModel<?>> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(_objectDefinition.getName());

		Table table = systemObjectDefinitionManager.getTable();

		try {
			DSLQuery dslQuery = _getUnrelatedModelsGroupByStep(
				themeDisplay.getCompanyId(), DSLQueryFactoryUtil.select(table),
				serviceContext.getScopeGroupId(), _objectDefinition, 0, table
			).limit(
				0, 20
			);

			PersistedModelLocalService persistedModelLocalService =
				PersistedModelLocalServiceRegistryUtil.
					getPersistedModelLocalService(
						_objectDefinition.getClassName());

			List<BaseModel<?>> baseModels = persistedModelLocalService.dslQuery(dslQuery);

			InfoPage<BaseModel<?>> infoPage = InfoPage.of(baseModels, Pagination.of(20,0), 5);

			return infoPage;
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to get object entries for object definition " +
					_objectDefinition.getObjectDefinitionId(),
				exception);
		}
	}

	@Override
	public String getCollectionItemClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public String getFormVariationKey() {
		return String.valueOf(_objectDefinition.getObjectDefinitionId());
	}

	@Override
	public String getKey() {
		return StringBundler.concat(
			ObjectEntrySystemObjectInfoCollectionProvider.class.getName(),
			StringPool.UNDERLINE, _objectDefinition.getCompanyId(),
			StringPool.UNDERLINE, _objectDefinition.getName());
	}

	@Override
	public String getLabel(Locale locale) {
		return _objectDefinition.getPluralLabel(locale);
	}

	@Override
	public boolean isAvailable() {
		if (_objectDefinition.getCompanyId() !=
				CompanyThreadLocal.getCompanyId()) {

			return false;
		}

		return true;
	}

	private DynamicObjectDefinitionTable _getDynamicObjectDefinitionTable()
		throws PortalException {

		// TODO Cache this across the cluster with proper invalidation when the
		// object definition or its object fields are updated

		return new DynamicObjectDefinitionTable(
			_objectDefinition,
			_objectFieldLocalService.getObjectFields(
				_objectDefinition.getObjectDefinitionId(),
				_objectDefinition.getExtensionDBTableName()),
			_objectDefinition.getExtensionDBTableName());
	}

	private GroupByStep _getUnrelatedModelsGroupByStep(
			long companyId, FromStep fromStep, long groupId,
			ObjectDefinition objectDefinition, long objectRelationshipId,
			Table table)
		throws PortalException {

		Column<?, Long> companyIdColumn = (Column<?, Long>)table.getColumn(
			"companyId");

		// 		ObjectRelationship objectRelationship =

		//			_objectRelationshipLocalService.getObjectRelationship(
		//				objectRelationshipId);

		//

		// 		ObjectDefinition objectDefinition1 =

		//			_objectDefinitionLocalService.getObjectDefinition(
		//				objectRelationship.getObjectDefinitionId1());

		return fromStep.from(
			table
		).where(
			companyIdColumn.eq(
				companyId
			).and(
				() -> {
					Column<?, Long> groupIdColumn = table.getColumn("groupId");

					if ((groupIdColumn == null) ||
						Objects.equals(
							ObjectDefinitionConstants.SCOPE_COMPANY,
							/* objectDefinition1.getScope() */
							objectDefinition.getScope())) {

						return null;
					}

					return groupIdColumn.eq(groupId);
				}
			)/*.and(
				() -> {
					Column<?, Long> primaryKeyColumn = table.getColumn(
						objectDefinition.getPKObjectFieldDBColumnName());

					DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
						_getDynamicObjectDefinitionTable();
					ObjectField objectField =
						_objectFieldLocalService.getObjectField(
							objectRelationship.getObjectFieldId2());

					Column<DynamicObjectDefinitionTable, Long>
						foreignKeyColumn =
						(Column<DynamicObjectDefinitionTable, Long>)
							dynamicObjectDefinitionTable.getColumn(
								objectField.getDBColumnName());

					return primaryKeyColumn.notIn(
						DSLQueryFactoryUtil.select(
							dynamicObjectDefinitionTable.getPrimaryKeyColumn()
						).from(
							dynamicObjectDefinitionTable
						).where(
							foreignKeyColumn.neq(0L)
						));
				}
			)*/
		);
	}

	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}