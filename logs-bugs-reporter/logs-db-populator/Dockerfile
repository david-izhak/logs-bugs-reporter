FROM adoptopenjdk/openjdk16:x86_64-alpine-jre16u-nightly
ADD ./target/logs-db-populator-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]