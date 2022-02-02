#!/bin/sh
cp /var/opt/olebo/config.properties Website/src/jvmMain/resources/config.properties
./gradlew :Website:installDist
