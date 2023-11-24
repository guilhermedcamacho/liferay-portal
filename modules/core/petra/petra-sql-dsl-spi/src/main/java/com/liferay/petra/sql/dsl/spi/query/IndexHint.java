/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi.query;

import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.query.IndexHintStep;
import com.liferay.petra.sql.dsl.spi.ast.BaseASTNode;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Guilherme Camacho
 */
public class IndexHint extends BaseASTNode implements DefaultIndexHintStep {

	public IndexHint(IndexHintStep indexHintStep, String indexHint) {
		super(indexHintStep);

		_indexHint = Objects.requireNonNull(indexHint);
	}

	@Override
	protected void doToSQL(
		Consumer<String> consumer, ASTNodeListener astNodeListener) {

		consumer.accept(" ".concat(_indexHint.concat(" ")));
	}

	private final String _indexHint;

}