/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi.query;

import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.spi.ast.BaseASTNode;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Preston Crary
 */
public class Join extends BaseASTNode implements DefaultJoinStep {

	public Join(
		JoinStep joinStep, JoinType joinType, Table<?> table,
		Predicate onPredicate) {

		this(joinStep, joinType, table, onPredicate, null);
	}

	public Join(
		JoinStep joinStep, JoinType joinType, Table<?> table,
		Predicate onPredicate, String indexHint) {

		super(joinStep);

		_joinType = Objects.requireNonNull(joinType);
		_table = Objects.requireNonNull(table);
		_onPredicate = Objects.requireNonNull(onPredicate);
		_indexHint = indexHint;
	}

	public JoinType getJoinType() {
		return _joinType;
	}

	public Predicate getOnPredicate() {
		return _onPredicate;
	}

	public Table<?> getTable() {
		return _table;
	}

	@Override
	protected void doToSQL(
		Consumer<String> consumer, ASTNodeListener astNodeListener) {

		consumer.accept(_joinType.getStringWithJoin());

		_table.toSQL(consumer, astNodeListener);

		if (_indexHint != null) {
			consumer.accept(" ".concat(_indexHint));
		}

		consumer.accept(" on ");

		_onPredicate.toSQL(consumer, astNodeListener);
	}

	private final String _indexHint;
	private final JoinType _joinType;
	private final Predicate _onPredicate;
	private final Table<?> _table;

}