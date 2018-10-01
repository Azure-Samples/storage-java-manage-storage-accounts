---
services: Storage
platforms: java
author: viananth
---

## Getting Started with Storage - Manage Storage Account - in Java ##


  Azure Storage sample for managing storage accounts -
   - Create a storage account
   - Get | regenerate storage account access keys
   - Create another storage account
   - Create another storage account 
   - List storage accounts
   - Delete a storage account.
 

## Running this Sample ##

To run this sample:

1. Clone the repository using the following command:

    git clone https://github.com/Azure-Samples/storage-java-manage-storage-accounts.git

2. Create an Azure service principal and assign a role to access the subscription. For instructions on creating a service principal, see [Use Azure PowerShell to create a service principal with a certificate](https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-create-service-principals).

3. Set the following required environment variable values:

    * AZURE_TENANT_ID

    * AZURE_CLIENT_ID

    * AZURE_CLIENT_SECRET

    * AZURE_SUBSCRIPTION_ID

    * ARM_ENDPOINT

    * RESOURCE_LOCATION

4. Change directory to Hybrid sample:
    
    * cd storage-java-manage-storage-accounts\Hybrid

5. Run the sample:
    * mvn clean compile exec:java

## More information ##

[http://azure.com/java](http://azure.com/java)

If you don't have a Microsoft Azure subscription you can get a FREE trial account [here](http://go.microsoft.com/fwlink/?LinkId=330212)

---

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
