---
services: storage
platforms: java
author: selvasingh
---

#Getting Started with Storage - Manage Storage Accounts - in Java #

Storage Manage Storage Accounts Sample (for 1.0.0-beta2) - demonstrates how to perform common management tasks using the Microsoft Azure Storage service.


- Create a storage account
- Get or regenerate storage account access keys
- Create another storage account
- List storage accounts
- Delete a storage account.
 

## Running this Sample ##

To run this sample:

Set the environment variable `AZURE_AUTH_LOCATION` with the full path for an auth file. See [how to create an auth file](https://github.com/Azure/azure-sdk-for-java/blob/master/AUTH.md).

    git clone https://github.com/Azure-Samples/storage-java-manage-storage-accounts.git

    cd storage-java-manage-storage-accounts

    mvn clean compile exec:java

## More information ##

[http://azure.com/java](http://azure.com/java)

[Azure Storage](https://azure.microsoft.com/en-us/services/storage/)

If you don't have a Microsoft Azure subscription you can get a FREE trial account [here](http://go.microsoft.com/fwlink/?LinkId=330212)

---

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.