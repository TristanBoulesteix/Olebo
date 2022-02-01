FROM openjdk:16
EXPOSE 8080:8080
EXPOSE 8443:8443
RUN mkdir /app
RUN mkdir "/etc/letsencrypt/live/olebo.fr-0001/"
COPY ./Website/build/install/Website/ /app/
WORKDIR /app/bin
CMD ["./Website"]