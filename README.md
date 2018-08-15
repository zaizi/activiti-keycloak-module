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

###  Synchronization  
Synchronization process is used to synchronize users and groups from keycloak to acitivi
   
| Property Key | Expected Value |
| ------ | ------ |
| keycloak.auth-server-url.to.sync | URL to Keycloak Server Ex : http://localhost:8180/auth, used to synch all users |
| keycloak.realm.to.sync | Realm of the login User to get the KeyCloak Client.(Mostly it is master Realm) |
| keycloak.sync.realms | Comma separated realm's from which users and groups need to synchronize |
| keycloak.userName | User who is authorised and authenticated to access the release and Clients |
| keycloak.password | Password for the user |
| keycloak.clientId | Clients defined in the Key cloak |
| keycloak.client.secret | Secret Key Generation, on selecting the Access type to Confidential, you get Credentials Tab, where you can find Secret Key |
| keycloak.synchronization.full.cronExpression | Cron Pattern to run the process on scheduled time(Full Load) Ex : */2 * * * * * |
| keycloak.synchronization.enabled | Flag which is used to enable the synchronization |

###  Process design Admin screen authentication   

Users login thorugh the Admin screens for process design , to be authenticate with Keycloak.

| Property Key | Expected Value |
| ------ | ------ |
| keycloak.auth-server-url.to.login.in.realm | Key Cloak URL used to authenticate the enter User against the realm |
| keycloak.realm.to.login | Realm name on which user to be authenticated |
| keycloak.realm.clientId | Client Id in the realm|
| keycloak.realm.client.secret | Client Secret |
| keycloak.realm.login.enabled | Keylogin is enabled in the admin screen |
| security.authentication.use-externalid | Security authentication using the external identifier (Out of box property) |
| security.authentication.casesensitive | Case Sensitive flag (Out of box property) |


###  JWT Authentication

Authentication process to enable using the JWT for the API's and Rest services

| Property Key | Expected Value |
| ------ | ------ |
|keycloak.jwt.login.enabled | Enable JWT authentication |
|keycloak.api.authentication.header | In which header key JWT tocken can be found |
|keycloak.client.user.name.key | In which attribute User name can be found in the JWT|
|keycloak.client.public.key | Key cloak realm public key|


# Keycloak Docker Container
```sh
docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin --expose 9990 -p 9990 -p 8180:8080 jboss/keycloak
```

Connect to ``http://localhost:8180/auth/ ``

This command creates "admin" user with the password as "admin"

## KeyCloak Setup

### Synchronisation
 - Create the user with the admin role in master "Realm". Above Docker does that for us.
 - Create Client in master "Realm" and save it. Update the property ``keycloak.clientId`` in the activities-keycloak.properties
 - Set the Access Type of the Client to "Confidential" and set the Valid Redirect URL(Example : http://localhost:8080/activiti-app) and click Save
 - You should be able to see "Credentials" Tab for Client, click that , choose "Client ID and Secret" in Client Authenticator if not selected by default. 
 - Then copy the Secret Key or regenerate and copy. And Update in the properties file in Project as described above. 
 - Create a Realm you want to work on and copy the realm name and add it to Property file in the application for "Client Realm" 
 - add all the realms from which Users need to synchronize from the Keycloak to activiti
 - Create Users and Groups in the created "Realm". Please note "First Name" and "Last Name" are must for Activiti.
 - To Enable Authenticate after the synchronisation, set the property true for "security.authentication.use-externalid"
 
 ### Process Admin Login authentication
 - Set the URL from which the the User need to be authenticated
 - Set the Realm Name where the user is registered to authenticate
 - Set the Client ID and Client Secret in the realm
 - Enable the flag to authenticate the using the keycloak
 - Enable the Flags to authenticate using the External Identifier.
### Token authentication
 - Set the Key name where the token can be found in the request. Order of finding is Header/Parameter/Attribute
 - Set the User Name Key that Keycloak is setup to send in JWT. By default it is  preferred_username
 - Set the Public key from the realm, can be found in Realm setting Keys Tab and in RSA Type, click "Public Key"
 
 
