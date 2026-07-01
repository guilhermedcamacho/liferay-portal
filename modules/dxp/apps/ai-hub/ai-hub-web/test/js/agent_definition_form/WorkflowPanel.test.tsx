/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import WorkflowPanel from '../../../src/main/resources/META-INF/resources/js/agent_definition_form/WorkflowPanel';

import type {AgentDefinition} from '../../../src/main/resources/META-INF/resources/js/agent_definition_form/types/AgentDefinition';

const mockGetWorkflowDefinition = jest.fn();

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/WorkflowDefinitionService',
	() => ({
		getWorkflowDefinition: (...args: any[]) =>
			mockGetWorkflowDefinition(...args),
	})
);

jest.mock('frontend-js-components-web', () => {
	const React = require('react');

	return {
		FieldBase: ({children, errorMessage, id, label, required}: any) =>
			React.createElement(
				'div',
				null,
				label &&
					React.createElement(
						'label',
						{htmlFor: id},
						label,
						required && '*'
					),
				children,
				errorMessage && React.createElement('div', null, errorMessage)
			),
	};
});

(global as any).Liferay = {
	Icons: {spritemap: 'icons.svg'},
	Language: {
		get: (key: string) => key,
	},
};

const baseValues: AgentDefinition = {
	active: false,
	description: '',
	externalReferenceCode: 'ERC-1',
	inputVariables: '',
	outputVariable: '',
	r_accountToAIHubAgentDefinitions_accountEntryERC: 'ACCOUNT',
	title_i18n: {},
	workflowDefinitionName: 'wf-1',
};

function renderPanel(
	overrides: Partial<{
		errors: any;
		readOnly: boolean;
		touched: any;
		values: AgentDefinition;
	}> = {}
) {
	render(
		<WorkflowPanel
			editAgentDefinitionURL="/agent"
			errors={overrides.errors || {}}
			kaleoDesignerNamespace="_NS_"
			readOnly={overrides.readOnly ?? false}
			touched={overrides.touched ?? {workflowDefinitionName: true}}
			values={overrides.values || baseValues}
			workflowDefinitionURL="http://localhost/workflow-url"
		/>
	);
}

describe('WorkflowPanel', () => {
	beforeEach(() => {
		mockGetWorkflowDefinition.mockReset();
		mockGetWorkflowDefinition.mockResolvedValue({});
	});

	afterEach(() => {
		cleanup();
	});

	describe('workflow association', () => {
		it('shows the edit-workflow action', () => {
			renderPanel();

			expect(
				screen.getByRole('button', {name: /edit-workflow/i})
			).toBeInTheDocument();
		});

		it('shows the view-workflow action when readOnly', () => {
			renderPanel({readOnly: true});

			expect(
				screen.getByRole('button', {name: /view-workflow/i})
			).toBeInTheDocument();
		});

		it('disables the action until a workflow is associated', () => {
			renderPanel({
				values: {...baseValues, workflowDefinitionName: ''},
			});

			expect(
				screen.getByRole('button', {name: /edit-workflow/i})
			).toBeDisabled();
		});

		it('displays the associated workflow definition title', async () => {
			mockGetWorkflowDefinition.mockResolvedValueOnce({
				title: 'My Workflow',
			});

			renderPanel();

			expect(await screen.findByText('My Workflow')).toBeInTheDocument();
		});

		it('surfaces the validation error when present', () => {
			renderPanel({
				errors: {workflowDefinitionName: 'Workflow required'},
			});

			expect(screen.getByText('Workflow required')).toBeInTheDocument();
		});
	});

	describe('workflow navigation', () => {
		let originalLocation: Location;

		beforeEach(() => {
			originalLocation = window.location;

			Object.defineProperty(window, 'location', {
				configurable: true,
				value: {href: '', origin: 'http://localhost'},
				writable: true,
			});
		});

		afterEach(() => {
			Object.defineProperty(window, 'location', {
				configurable: true,
				value: originalLocation,
				writable: true,
			});
		});

		it('navigates to the designer with the workflow name and a redirect to the agent', async () => {
			renderPanel();

			await userEvent.click(
				screen.getByRole('button', {name: /edit-workflow/i})
			);

			expect(window.location.href).toContain('_NS_name=wf-1');
			expect(window.location.href).toContain(
				'externalReferenceCode%3DERC-1'
			);
		});
	});
});
