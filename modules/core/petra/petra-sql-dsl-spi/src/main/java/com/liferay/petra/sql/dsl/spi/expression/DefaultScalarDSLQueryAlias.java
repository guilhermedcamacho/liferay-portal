/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi.expression;

import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.expression.ScalarDSLQueryAlias;
import com.liferay.petra.sql.dsl.query.DSLQuery;

import java.util.function.Consumer;

/**
 * @author Marco Leo
 */
public class DefaultScalarDSLQueryAlias<T>
	extends DefaultAlias<T> implements ScalarDSLQueryAlias<T> {

	public DefaultScalarDSLQueryAlias(
		DSLQuery dslQuery, Class<T> javaType, String name, int sqlType) {

		super(null, javaType, name, sqlType);

		_dslQuery = dslQuery;
	}

	@Override
	public DSLQuery getDSLQuery() {
		return _dslQuery;
	}

	@Override
	protected void doToSQL(
		Consumer<String> consumer, ASTNodeListener astNodeListener) {

		consumer.accept("(");
		consumer.accept(_dslQuery.toSQL(astNodeListener));
		consumer.accept(") as " + getName());
	}

	private final DSLQuery _dslQuery;

}