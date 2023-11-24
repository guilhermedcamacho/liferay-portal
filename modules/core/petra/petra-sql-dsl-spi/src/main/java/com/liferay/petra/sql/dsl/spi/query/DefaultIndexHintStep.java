/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi.query;

import com.liferay.petra.sql.dsl.query.IndexHintStep;
import com.liferay.petra.sql.dsl.query.JoinStep;

/**
 * @author Guilherme Camacho
 */
public interface DefaultIndexHintStep extends DefaultJoinStep, IndexHintStep {

	@Override
	public default JoinStep indexHint(String indexHint) {
		return new IndexHint(this, indexHint);
	}

}