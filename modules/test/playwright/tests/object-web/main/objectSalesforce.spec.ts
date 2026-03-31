/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectFields} from './utils/generateObjectFields';

const salesforceLoginURL = process.env.SALESFORCE_LOGIN_URL;
const salesforceConsumerKey = process.env.SALESFORCE_CONSUMER_KEY;
const salesforceConsumerSecret = process.env.SALESFORCE_CONSUMER_SECRET;
const salesforceUsername = process.env.SALESFORCE_USERNAME;
const salesforcePassword = process.env.SALESFORCE_PASSWORD;

const test = mergeTests(
    dataApiHelpersTest,
    featureFlagsTest({
        'LPS-135430': {enabled: true},
    }),
    instanceSettingsPagesTest,
    isolatedSiteTest,
    loginTest(),
    objectPagesTest
);

test.beforeEach(async ({instanceSettingsPage, page}) => {
    test.skip(
        !salesforceLoginURL ||
            !salesforceConsumerKey ||
            !salesforceConsumerSecret ||
            !salesforceUsername ||
            !salesforcePassword,
        'Requires Salesforce environment variables.'
    );

    page.setViewportSize({height: 1080, width: 1920});

    await test.step('Setup Salesforce Instance Settings', async () => {
        await instanceSettingsPage.goToInstanceSetting(
            'Third Party',
            'Salesforce Integration'
        );

        await page.getByLabel('Login URL').fill(salesforceLoginURL!);
        await page.getByLabel('Consumer Key').fill(salesforceConsumerKey!);
        await page.getByLabel('Consumer Secret').fill(salesforceConsumerSecret!);
        await page.getByLabel('Username').fill(salesforceUsername!);
        
        await page.locator('input[name*="password"]')
            .filter({ visible: true }) 
            .fill(salesforcePassword!);

        await instanceSettingsPage.saveAndWaitForAlert();
    });
});

async function runSalesforceCRUDTest({
    apiHelpers,
    page,
	fieldIndex = 0,
	objectConfig,
    viewObjectEntriesPage,
    isStandardObject = false
}) {
    const objectDefinitionAPIClient = await apiHelpers.buildRestClient(ObjectDefinitionAPI);

    const {body: objectDefinition} = await objectDefinitionAPIClient.postObjectDefinition(objectConfig);

    apiHelpers.data.push({
        id: objectDefinition.id,
        type: 'objectDefinition',
    });

    const fieldLabel = objectConfig.objectFields[fieldIndex].label['en_US'];
    const createValue = isStandardObject ? `Last Name ${getRandomInt()}` : getRandomString();
    const updateValue = isStandardObject ? `Last Name Updated ${getRandomInt()}` : getRandomString();

    await test.step('Create Object Entry', async () => {
        await viewObjectEntriesPage.goto(objectDefinition.className);
        await viewObjectEntriesPage.clickAddObjectEntry(objectDefinition.label['en_US']);

        await viewObjectEntriesPage.fillObjectEntry({
            objectFieldBusinessType: 'Text',
            objectFieldLabel: fieldLabel,
            objectFieldValue: createValue,
        });

        await viewObjectEntriesPage.saveObjectEntryButton.click();
        await waitForAlert(page);
        await viewObjectEntriesPage.backButton.click();
    }); 

    await test.step('Read Object Entry', async () => {
        await expect(page.getByRole('cell', { name: createValue })).toBeVisible();
    });

    await test.step('Update Object Entry', async () => {
        await page.getByRole('button', {name: 'Actions'}).last().click();
        await page.getByRole('menuitem', {name: 'View'}).click();

        await viewObjectEntriesPage.fillObjectEntry({
            objectFieldBusinessType: 'Text',
            objectFieldLabel: fieldLabel,
            objectFieldValue: updateValue,
        });

        await viewObjectEntriesPage.saveObjectEntryButton.click();
        await expect(viewObjectEntriesPage.successMessage).toBeVisible();
        await viewObjectEntriesPage.backButton.click();

        await expect(page.getByRole('cell', { name: updateValue })).toBeVisible();
    });

    await test.step('Delete Object Entry', async () => {
        await viewObjectEntriesPage.frontendDatasetActions.last().click();
        await viewObjectEntriesPage.frontendDatasetDeleteAction.click();
        await viewObjectEntriesPage.deletionConfirmationModal
            .getByRole('button', { name: 'Delete' })
            .click();

        await expect(page.getByRole('cell', { name: updateValue })).toBeAttached({attached: false});
    });
}

test(
    'LPS-162131 Assert CRUD with created custom object using Salesforce storage type',
    {tag: '@LPS-162131'},
    async ({apiHelpers, page, viewObjectEntriesPage}) => {
        const objectFields = generateObjectFields({
            objectFieldBusinessTypes: [{
                businessType: 'Text',
                externalReferenceCode: 'Title__c',
                label: { en_US: 'Title' },
                name: 'title',
            }],
        });

        await runSalesforceCRUDTest({
            apiHelpers,
            page,
            objectConfig: {
                active: true,
                externalReferenceCode: 'Playwright_Test__c',
                label: { en_US: "Playwright Test" },
                name: "PlaywrightTest",
                objectFields,
                panelCategoryKey: 'control_panel.object',
                pluralLabel: { en_US: "Playwright Tests" },
                portlet: true,
                scope: 'company',
                status: { code: 0 },
                storageType: 'salesforce',
            },
			viewObjectEntriesPage
        });
    }
);

test(
    'LPS-185429 Assert CRUD with created standard object using Salesforce storage type',
    {tag: '@LPS-185429'},
    async ({apiHelpers, page, viewObjectEntriesPage}) => {
        const objectFields = generateObjectFields({
            objectFieldBusinessTypes: [
                { businessType: 'Text', externalReferenceCode: 'Email', label: { en_US: 'Email' }, name: 'email' },
                { businessType: 'Text', externalReferenceCode: 'FirstName', label: { en_US: 'First Name' }, name: 'firstName' },
                { businessType: 'Text', externalReferenceCode: 'LastName', label: { en_US: 'Last Name' }, name: 'lastName', required: true },
                { businessType: 'Text', externalReferenceCode: 'Phone', label: { en_US: 'Phone' }, name: 'phone' },
            ],
        });

        await runSalesforceCRUDTest({
            apiHelpers,
            page,
            fieldIndex: 2,
            objectConfig: {
                active: true,
                externalReferenceCode: "Contact",
                label: { en_US: "Contact" },
                name: "Contact",
                objectFields,
                panelCategoryKey: 'control_panel.object',
                pluralLabel: { en_US: "Contacts" },
                portlet: true,
                scope: 'company',
                status: { code: 0 },
                storageType: 'salesforce',
			},
            viewObjectEntriesPage,
            isStandardObject: true
        });
    }
);