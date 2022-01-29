FROM openjdk:16
EXPOSE 8080:8080
RUN mkdir /app
COPY ./Website/build/install/Website/ /app/
WORKDIR /app/bin
CMD ["./Website"]