#!/bin/sh
cd "/home/tristan/Olebo" || exit
cp /var/opt/olebo/config.properties Website/src/jvmMain/resources/config.properties
cp /etc/letsencrypt/live/olebo.fr-0001/keystore.jks Website/src/jvmMain/resources/keystore.jks
./gradlew :Website:installDist
docker build -t olebo-website .
docker stop website
docker run -p 8080:8080 -p 8443:8443 -v /home/tristan/Olebo_releases:/app/bin/releases --name website olebo-website
