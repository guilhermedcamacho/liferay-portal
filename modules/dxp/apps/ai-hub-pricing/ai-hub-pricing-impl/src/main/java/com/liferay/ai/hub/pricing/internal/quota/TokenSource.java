/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.pricing.internal.quota;

/**
 * @author Guilherme Camacho
 */
public enum TokenSource {

	EMBEDDING(45000L), MODEL_ARMOR(90000L), VERTEX_INPUT(2340L),
	VERTEX_OUTPUT(525L);

	public long getTokensPerLRT() {
		return _tokensPerLRT;
	}

	private TokenSource(long tokensPerLRT) {
		_tokensPerLRT = tokensPerLRT;
	}

	private final long _tokensPerLRT;

}