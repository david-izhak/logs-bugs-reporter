FROM adoptopenjdk/openjdk16:x86_64-alpine-jre16u-nightly
EXPOSE 9191
ADD ./target/logs-bugs-gateway-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]