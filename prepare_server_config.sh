#!/bin/sh
cp /var/opt/olebo/config.properties Website/src/jvmMain/resources/config.properties
cp /etc/letsencrypt/live/olebo.fr-0001/keystore.jks Website/src/jvmMain/resources/keystore.jks
./gradlew :Website:installDist
