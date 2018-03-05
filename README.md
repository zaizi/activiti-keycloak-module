# Activiti - Keycloak Module

## Alfresco Activiti JAR Module - SDK 3

To run use `mvn clean install alfresco:run` or `./run.sh` and verify that it 

 * Runs the embedded Tomcat + H2 DB 
 * Runs Activiti Explorer
 * Optionally runs Activiti REST
 * Packages both a JAR with customization
  
## Few things to notice

 * No parent pom
 * WAR assembly is handled by the Alfresco Maven Plugin configuration
 * Standard JAR packaging and layout
 * Works seamlessly with Eclipse and IntelliJ IDEA
 * JRebel for hot reloading, JRebel maven plugin for generating rebel.xml, agent usage: `MAVEN_OPTS=-Xms256m -Xmx1G -agentpath:/home/martin/apps/jrebel/lib/libjrebel64.so`
 * [Configurable Run mojo](https://github.com/Alfresco/alfresco-sdk/blob/sdk-3.0/plugins/alfresco-maven-plugin/src/main/java/org/alfresco/maven/plugin/RunMojo.java) in the `alfresco-maven-plugin`
 * No unit testing/functional tests just yet
 
## How to setup the module

Key properties to set (Property File can be find in resources folder as activities-keycloak.properties)

| Property Key | Expected Value |
| ------ | ------ |
| keycloak.auth-server-url | URL to Keycloak Server Ex : http://localhost:8180/auth |
| keycloak.realm | Realm of the User to get the KeyCloak Client.(Mostly it is master Realm) |
| keycloak.userName | User who is authorised and authenticated to access the release and Clients |
| keycloak.password | Password for the user |
| keycloak.clientId | Clients defined in the Key cloak
| keycloak.client.secret | Secret Key Generation, on selecting the Access type to Confidential, you get Credentials Tab, where you can find Secret Key |
| keycloak.synchronization.full.cronExpression | Cron Pattern to run the process on scheduled time(Full Load) Ex : */2 * * * * * |
| keycloak.synchronization.differential.cronExpression | Cron Pattern to run the process on scheduled time(Differentials Load) Ex :0 0 */4 * * ? |
| keycloak.client.realm | Realm to Load the Users and groups from |
| security.authentication.use-externalid | To Enable Authentication by external ID. In case of KeyCloak is Synchronised , Keycloak Username becomes external ID on Synchronisation |
| security.authentication.casesensitive | to Enable External ID Case Sensitivity |

# Keycloak Docker Container
```sh
docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin --expose 9990 -p 9990 -p 8180:8080 jboss/keycloak
```

Connect to ``http://localhost:8180/auth/ ``

This command creates "admin" user with the password as "admin"

## KeyCloak Setup
 - Create the user with the admin role in master "Realm". Above Docker does that for us.
 - Create Client in master "Ream"  With the given ID
 - Set the Access Type of the Client to "Confidential" and set the Valid Redirect URL(Example : http://localhost:8080/activiti-app) and click Save
 - You should be able to see "Credentials" Tab for Client, click that , choose "Client ID and Secret" in Client Authenticator if not selected by default. 
 - Then copy the Secret Key or regenerate and copy. And Update in the properties file in Project as described above(keycloak.client.secret). 
 - Create a Realm you want to work on and copy the realm name and add it to Property file in the application for "Client Realm" (Property Key :  keycloak.client.realm)
 - Create Users and Groups in the created "Realm". Please note "First Name" and "Last Name" are must for Activiti.
 - To Enable Authenticate after the synchronisation, set the property true for "security.authentication.use-externalid"
 
