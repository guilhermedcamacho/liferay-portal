/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from '@liferay/object-js-components-web';
import {useFormik} from 'formik';
import {useCallback, useEffect, useState} from 'react';

import {required, validate} from '../../utils/validations';
import {
	getConfiguration,
	putConfiguration,
} from '../services/ConfigurationService';
import {Configuration} from '../types/Configuration';

interface UseConfigurationFormProps {
	externalReferenceCode: string;
}

export function useConfigurationForm({
	externalReferenceCode,
}: UseConfigurationFormProps) {
	const [loading, setLoading] = useState(Boolean(externalReferenceCode));

	const {
		errors,
		handleBlur,
		handleSubmit,
		isSubmitting,
		setFieldValue,
		setValues,
		touched,
		values,
	} = useFormik<Configuration>({
		initialValues: {
			environmentUrls: '',
			externalReferenceCode,
			recipientEmailAddress: '',
		},
		onSubmit: async (formValues) => {
			try {
				await putConfiguration(externalReferenceCode, formValues);

				openToast({
					message: Liferay.Language.get(
						'configuration-was-saved-successfully'
					),
					type: 'success',
				});
			}
			catch (error) {
				openToast({
					message:
						error instanceof Error && error.message
							? error.message
							: Liferay.Language.get(
									'failed-to-save-the-configuration'
								),
					type: 'danger',
				});
			}
		},
		validate: (formValues) =>
			validate(
				{
					environmentUrls: [required],
					recipientEmailAddress: [required],
				},
				formValues
			),
	});

	const setField = useCallback(
		<K extends keyof Configuration>(field: K, value: Configuration[K]) => {
			setFieldValue(field, value);
		},
		[setFieldValue]
	);

	useEffect(() => {
		if (!externalReferenceCode) {
			return;
		}

		getConfiguration(externalReferenceCode)
			.then((configuration) => {
				setValues({
					environmentUrls: configuration.environmentUrls || '',
					externalReferenceCode,
					recipientEmailAddress:
						configuration.recipientEmailAddress || '',
				});
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'failed-to-load-configuration'
					),
					type: 'danger',
				});
			})
			.finally(() => {
				setLoading(false);
			});
	}, [externalReferenceCode, setValues]);

	return {
		errors,
		handleBlur,
		handleSubmit,
		isSubmitting,
		loading,
		setField,
		touched,
		values,
	};
}
