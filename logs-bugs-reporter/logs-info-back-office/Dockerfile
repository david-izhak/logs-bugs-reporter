FROM adoptopenjdk/openjdk16:x86_64-alpine-jre16u-nightly
EXPOSE 8282
ADD ./target/logs-info-back-office-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]