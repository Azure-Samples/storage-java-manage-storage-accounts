---
services: storage
platforms: java
author: selvasingh
---

# Getting Started with Azure Storage Resource Provider in Java

This sample shows how to manage your storage account using the Azure Storage Resource Provider for Java. The Storage Resource Provider is a client library for working with the storage accounts in your Azure subscription. Using the client library, you can:

- Create a storage account
- Get or regenerate storage account access keys
- Create another storage account
- List storage accounts
- Delete a storage account.

**On this page**

- Run this sample
- What is program.cs doing?
 
## Running this sample

To run this sample:

1. If you don't already have a Microsoft Azure subscription, you can register for a [free trial account](http://go.microsoft.com/fwlink/?LinkId=330212).

2. Install the [Azure Management Libraries for Java](https://github.com/Azure/azure-sdk-for-java/). These libraries contain the Azure Storage Resource Provider.

3. Set the environment variable `AZURE_AUTH_LOCATION` with the full path for an auth file. See [how to create an auth file](https://github.com/Azure/azure-sdk-for-java/blob/master/AUTH.md) for more information.

4. Clone this repository: 

    	git clone https://github.com/Azure-Samples/storage-java-manage-storage-accounts.git

5. Navigate to the source directory for the storage sample:

    	cd storage-java-manage-storage-accounts

6. Build the project with [Maven](https://maven.apache.org/download.cgi):

    	mvn clean compile exec:java

## What is ManageStorageAccount.java doing?

The sample walks you through several Storage Resource Provider operations.

### Get credentials and authenticate 

First, the sample retrieves credentials from the file specified by the `AZURE_AUTH_LOCATION` environment variable: 

    final File credFile = new File(System.getenv("AZURE_AUTH_LOCATION"));

    Azure azure = Azure
            .configure()
            .withLogLevel(HttpLoggingInterceptor.Level.BASIC)
            .authenticate(credFile)
            .withDefaultSubscription();

### Create a storage account

The sample creates a storage account with the specified name and region. The new storage account is created in a new resource group, which is created as part of the same call:

    StorageAccount storageAccount = azure.storageAccounts().define(storageAccountName)
            .withRegion(Region.US_EAST)
            .withNewResourceGroup(rgName)
            .create();

### Read and regenerate storage account keys

The sample lists storage account keys for the newly created storage account and resource group:

    List<StorageAccountKey> storageAccountKeys = storageAccount.keys();

It also regenerates the account access keys:

    storageAccountKeys = storageAccount.regenerateKey(storageAccountKeys.get(0).keyName());

### List storage accounts in the resource group

The sample lists all of the storage accounts in the resource group: 

    StorageAccounts storageAccounts = azure.storageAccounts();

    List accounts = storageAccounts.listByGroup(rgName);
    StorageAccount sa;
    for (int i = 0; i < accounts.size(); i++) {
        sa = (StorageAccount) accounts.get(i);
        System.out.println("Storage Account (" + i + ") " + sa.name()
                + " created @ " + sa.creationTime());
    }

### Delete the storage account

Finally, the sample deletes the storage account that it created:

    azure.storageAccounts().delete(storageAccount.id());

The sample deletes the new resource group that it created, which also deletes the additional storage account that was created when the sample ran.

## Additional information

- [Azure Developer Center for Java](http://azure.com/java)
- [Azure Storage Documentation](https://azure.microsoft.com/services/storage/)

---

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.