/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.storage.samples;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.profile_2018_03_01_hybrid.Azure;
import com.microsoft.azure.management.resources.v2018_02_01.ResourceGroup;
import com.microsoft.azure.management.samples.Utils;
import com.microsoft.azure.management.storage.v2016_01_01.Encryption;
import com.microsoft.azure.management.storage.v2016_01_01.EncryptionService;
import com.microsoft.azure.management.storage.v2016_01_01.EncryptionServices;
import com.microsoft.azure.management.storage.v2016_01_01.Kind;
import com.microsoft.azure.management.storage.v2016_01_01.Sku;
import com.microsoft.azure.management.storage.v2016_01_01.SkuName;
import com.microsoft.azure.management.storage.v2016_01_01.StorageAccount;
import com.microsoft.azure.management.storage.v2016_01_01.StorageAccountKey;
import com.microsoft.azure.management.storage.v2016_01_01.StorageAccounts;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Azure Storage sample for managing storage accounts - - Create a storage
 * account - Get | regenerate storage account access keys - Create another
 * storage account - Create another storage account - List storage
 * accounts - Delete a storage account.
 */

public final class ManageStorageAccount {
    /**
     * Main function which runs the actual sample.
     * 
     * @param azure instance of the azure client
     * @return true if sample runs successfully
     */
    public static boolean runSample(Azure azure, String location) {
        final String storageAccountName = Utils.createRandomName("sa");
        final String storageAccountName2 = Utils.createRandomName("sa2");
        final String rgName = Utils.createRandomName("rgSTMS");
        try {
            // ===========================================================
            // Create a resource group
            ResourceGroup resourceGroup = azure.resourceGroups().define(rgName).withExistingSubscription()
                    .withLocation(location).create();
            System.out.println("Created a resource group with name: " + resourceGroup.name());

            // ============================================================
            // Create a storage account

            System.out.println("Creating a Storage Account");
            StorageAccount storageAccount = azure.storageAccounts().define(storageAccountName).withRegion(location)
                    .withExistingResourceGroup(rgName).withKind(Kind.STORAGE)
                    .withSku(new Sku().withName(SkuName.STANDARD_LRS)).create();

            System.out.println("Created a Storage Account:");
            Utils.print(storageAccount);

            // ============================================================
            // Get | regenerate storage account access keys

            System.out.println("Getting storage account access keys");
            List<StorageAccountKey> storageAccountKeys = azure.storageAccounts().inner()
                    .listKeys(rgName, storageAccountName).keys();

            Utils.print(storageAccountKeys);

            System.out.println("Regenerating first storage account access key");

            storageAccountKeys = azure.storageAccounts().inner()
                    .regenerateKey(rgName, storageAccountName, storageAccountKeys.get(0).keyName()).keys();

            Utils.print(storageAccountKeys);

            // ============================================================
            // Create another storage account

            System.out.println("Creating a 2nd Storage Account");

            StorageAccount storageAccount2 = azure.storageAccounts().define(storageAccountName2).withRegion(location)
                    .withExistingResourceGroup(rgName).withKind(Kind.STORAGE)
                    .withSku(new Sku().withName(SkuName.STANDARD_LRS)).create();

            System.out.println("Created a Storage Account:");
            Utils.print(storageAccount2);

            // ============================================================
            // Update storage account by enabling encryption

            System.out.println("Enabling blob encryption for the storage account: " + storageAccount2.name());
            storageAccount2.update()
                    .withEncryption(
                            new Encryption().withServices(new EncryptionServices().withBlob(new EncryptionService())))
                    .apply();

            String status = storageAccount2.encryption().services().blob().enabled() ? "Enabled" : "Not enabled";

            System.out.println("Encryption status of the service " + storageAccount2.name() + ":" + status);

            // ============================================================
            // List storage accounts

            System.out.println("Listing storage accounts");

            StorageAccounts storageAccounts = azure.storageAccounts();

            List<StorageAccount> accounts = storageAccounts.listByResourceGroup(rgName);
            StorageAccount sa;
            for (int i = 0; i < accounts.size(); i++) {
                sa = (StorageAccount) accounts.get(i);
                System.out.println("Storage Account (" + i + ") " + sa.name() + " created @ " + sa.creationTime());
            }

            // ============================================================
            // Delete a storage account

            System.out.println("Deleting a storage account - " + storageAccount.name() + " created @ "
                    + storageAccount.creationTime());

            azure.storageAccounts().deleteByIds(storageAccount.id());

            System.out.println("Deleted storage account");
            return true;
        } catch (Exception f) {
            System.out.println(f.getMessage());
            f.printStackTrace();
        } finally {
            try {
                System.out.println("Deleting Resource Group: " + rgName);
                azure.resourceGroups().deleteAsync(rgName);
                System.out.println("Deleted Resource Group: " + rgName);
            } catch (Exception e) {
                System.out.println("Did not create any resources in Azure. No clean up is necessary");
            }
        }
        return false;
    }

    public static HashMap<String, String> getActiveDirectorySettings(String armEndpoint) {
        HashMap<String, String> adSettings = new HashMap<String, String>();

        try {

            // create HTTP Client
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Create new getRequest with below mentioned URL
            HttpGet getRequest = new HttpGet(String.format("%s/metadata/endpoints?api-version=1.0", armEndpoint));

            // Add additional header to getRequest which accepts application/xml data
            getRequest.addHeader("accept", "application/xml");

            // Execute request and catch response
            HttpResponse response = httpClient.execute(getRequest);

            // Check for HTTP response code: 200 = success
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            String responseStr = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = new JSONObject(responseStr);
            adSettings.put("galleryEndpoint", responseJson.getString("galleryEndpoint"));
            JSONObject authentication = (JSONObject) responseJson.get("authentication");
            String audience = authentication.get("audiences").toString().split("\"")[1];
            adSettings.put("login_endpoint", authentication.getString("loginEndpoint"));
            adSettings.put("audience", audience);
            adSettings.put("graphEndpoint", responseJson.getString("graphEndpoint"));

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return adSettings;
    }

    /**
     * Main entry point.
     * 
     * @param args the parameters
     */
    public static void main(String[] args) {
        try {

            final String armEndpoint = System.getenv("ARM_ENDPOINT");
            final String location = System.getenv("RESOURCE_LOCATION");
            final String client = System.getenv("AZURE_CLIENT_ID");
            final String tenant = System.getenv("AZURE_TENANT_ID");
            final String key = System.getenv("AZURE_CLIENT_SECRET");
            final String subscriptionId = System.getenv("AZURE_SUBSCRIPTION_ID");

            // Get Azure Stack cloud endpoints
            final HashMap<String, String> settings = getActiveDirectorySettings(armEndpoint);

            // Register Azure Stack cloud with endpoints
            AzureEnvironment AZURE_STACK = new AzureEnvironment(new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put("managementEndpointUrl", settings.get("audience"));
                    put("resourceManagerEndpointUrl", armEndpoint);
                    put("galleryEndpointUrl", settings.get("galleryEndpoint"));
                    put("activeDirectoryEndpointUrl", settings.get("login_endpoint"));
                    put("activeDirectoryResourceId", settings.get("audience"));
                    put("activeDirectoryGraphResourceId", settings.get("graphEndpoint"));
                    put("storageEndpointSuffix", armEndpoint.substring(armEndpoint.indexOf('.')));
                    put("keyVaultDnsSuffix", ".adminvault" + armEndpoint.substring(armEndpoint.indexOf('.')));
                }
            });


            // Authenticate Service principal against Azure Stack
            AzureTokenCredentials credentials = new ApplicationTokenCredentials(client, tenant, key, AZURE_STACK)
                    .withDefaultSubscriptionId(subscriptionId);

            Azure azureStack = Azure.configure().withLogLevel(com.microsoft.rest.LogLevel.BASIC)
                    .authenticate(credentials, credentials.defaultSubscriptionId());

            // Invoke the storage management method
            runSample(azureStack, location);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private ManageStorageAccount() {

    }
}