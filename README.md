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

###  Activiti Process Admin Login using Activiti Login Page
 Administrator have to login  
   
| Property Key | Expected Value |
| ------ | ------ | 
| keycloak.auth-server-url.to.login.in.realm | URL to Keycloak Server Ex : http://localhost:8180/auth, used to login in the realm |
| keycloak.realm.to.login | Realm to Login the Users and groups from |

| security.authentication.use-externalid | To Enable Authentication by external ID. In case of KeyCloak is Synchronised , Keycloak Username becomes external ID on Synchronisation |
| security.authentication.casesensitive | to Enable External ID Case Sensitivity |
| keycloak.api.authentication.header | Key for the token that is sent to the Rest API
| keycloak.client.user.name.key | Given flexibility to find the user name in the JWT Token, generally it is preferred_username
| keycloak.client.public.key    | Public Key of the realm, can be found in Realms Setting and  "Keys" Tab in RSA type , click Public Key Button

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
 - Then copy the Secret Key or regenerate and copy. And Update in the properties file in Project as described above(keycloak.client.secret). 
 - Create a Realm you want to work on and copy the realm name and add it to Property file in the application for "Client Realm" (Property Key :  keycloak.realm.to.sync)
 - Create Users and Groups in the created "Realm". Please note "First Name" and "Last Name" are must for Activiti.
 - To Enable Authenticate after the synchronisation, set the property true for "security.authentication.use-externalid"
 
### Token authentication
 - Set the Key name where the token can be found in the request. Order of finding is Header/Parameter/Attribute (Property Key keycloak.api.authentication.header)
 - Set the User Name Key that Keycloak is setup to send in JWT. By default it is  preferred_username (Property Key : keycloak.client.user.name.key)
 - Set the Public key from the realm, can be found in Realm setting Keys Tab and in RSA Type, click "Public Key" (Property Key : keycloak.client.public.key )
 
