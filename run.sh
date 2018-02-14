#!/bin/bash

MAVEN_OPTS="-Xms1G -Xmx2G -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n" mvn clean install -DskipTests alfresco:run
