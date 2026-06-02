/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayPanel from '@clayui/panel';
import {FieldBase} from 'frontend-js-components-web';
import React from 'react';

import {useConfigurationForm} from './hooks/useConfigurationForm';

const FORM_ID = 'configurationForm';

export default function ConfigurationForm({
	backURL,
	externalReferenceCode,
}: {
	backURL: string;
	externalReferenceCode: string;
}) {
	const {
		errors,
		handleBlur,
		handleSubmit,
		isSubmitting,
		loading,
		setField,
		touched,
		values,
	} = useConfigurationForm({externalReferenceCode});

	if (loading) {
		return null;
	}

	return (
		<ClayLayout.ContainerFluid className="configuration-form p-4">
			<ClayForm id={FORM_ID} onSubmit={handleSubmit}>
				<ClayPanel collapsable={false}>
					<ClayPanel.Body>
						<h2>{Liferay.Language.get('account-configuration')}</h2>

						<p className="text-secondary">
							{Liferay.Language.get(
								'settings-scoped-to-this-ai-hub-account'
							)}
						</p>

						<FieldBase
							errorMessage={
								touched.environmentUrls
									? errors.environmentUrls
									: undefined
							}
							helpMessage={Liferay.Language.get(
								'origin-of-the-dxp-site-that-embeds-ai-hub-chatbots'
							)}
							id="environmentUrls"
							label={Liferay.Language.get('environment-url')}
							required
						>
							<ClayInput
								className={
									touched.environmentUrls &&
									errors.environmentUrls
										? 'is-invalid'
										: ''
								}
								id="environmentUrls"
								name="environmentUrls"
								onBlur={handleBlur}
								onChange={(event) =>
									setField(
										'environmentUrls',
										event.target.value
									)
								}
								required
								type="text"
								value={values.environmentUrls}
							/>
						</FieldBase>

						<FieldBase
							errorMessage={
								touched.recipientEmailAddress
									? errors.recipientEmailAddress
									: undefined
							}
							helpMessage={Liferay.Language.get(
								'recipient-of-operational-emails'
							)}
							id="recipientEmailAddress"
							label={Liferay.Language.get('notification-email')}
							required
						>
							<ClayInput
								className={
									touched.recipientEmailAddress &&
									errors.recipientEmailAddress
										? 'is-invalid'
										: ''
								}
								id="recipientEmailAddress"
								name="recipientEmailAddress"
								onBlur={handleBlur}
								onChange={(event) =>
									setField(
										'recipientEmailAddress',
										event.target.value
									)
								}
								required
								type="email"
								value={values.recipientEmailAddress}
							/>
						</FieldBase>

						<div className="mt-4">
							<Button
								disabled={isSubmitting}
								displayType="primary"
								type="submit"
							>
								{Liferay.Language.get('save')}
							</Button>

							<ClayLink
								button
								className="ml-2"
								displayType="secondary"
								href={backURL}
							>
								{Liferay.Language.get('cancel')}
							</ClayLink>
						</div>
					</ClayPanel.Body>
				</ClayPanel>
			</ClayForm>
		</ClayLayout.ContainerFluid>
	);
}
