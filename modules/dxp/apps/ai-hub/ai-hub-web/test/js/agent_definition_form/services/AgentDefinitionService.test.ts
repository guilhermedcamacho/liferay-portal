/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	putAgentDefinition,
	putAgentDefinitionDraft,
} from '../../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/AgentDefinitionService';

const mockFetch = jest.fn();

jest.mock('frontend-js-web', () => ({
	fetch: (...args: any[]) => mockFetch(...args),
}));

const BY_ERC_URI = '/o/ai-hub/agent-definitions/by-external-reference-code/';

describe('AgentDefinitionService', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	const agentDefinition = {
		active: true,
		externalReferenceCode: 'AGENT_X',
		title_i18n: {en_US: 'My Agent'},
	} as any;

	describe('putAgentDefinition', () => {
		it('sends a PUT with the agent serialized as JSON and no status', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({externalReferenceCode: 'AGENT_X'}),
				ok: true,
			});

			await putAgentDefinition(agentDefinition);

			expect(mockFetch).toHaveBeenCalledWith(
				`${BY_ERC_URI}AGENT_X`,
				expect.objectContaining({
					body: JSON.stringify(agentDefinition),
					headers: {'Content-Type': 'application/json'},
					method: 'PUT',
				})
			);
		});

		it("throws with the server's detail when the response is not ok", async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({detail: 'Title is required'}),
				ok: false,
			});

			await expect(putAgentDefinition(agentDefinition)).rejects.toThrow(
				'Title is required'
			);
		});
	});

	describe('putAgentDefinitionDraft', () => {
		it('sends a PUT that keeps the entry a draft by carrying the draft status code', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () =>
					Promise.resolve({
						externalReferenceCode: 'AGENT_X',
						status: {label: 'draft'},
					}),
				ok: true,
			});

			await putAgentDefinitionDraft(agentDefinition);

			expect(mockFetch).toHaveBeenCalledWith(
				`${BY_ERC_URI}AGENT_X`,
				expect.objectContaining({
					body: JSON.stringify({
						...agentDefinition,
						status: {code: 2},
					}),
					headers: {'Content-Type': 'application/json'},
					method: 'PUT',
				})
			);
		});

		it("throws with the server's detail when the response is not ok", async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({detail: 'Unable to save draft'}),
				ok: false,
			});

			await expect(
				putAgentDefinitionDraft(agentDefinition)
			).rejects.toThrow('Unable to save draft');
		});
	});
});
