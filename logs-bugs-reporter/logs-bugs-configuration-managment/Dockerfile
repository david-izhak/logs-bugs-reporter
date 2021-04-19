FROM adoptopenjdk/openjdk16:x86_64-alpine-jre16u-nightly
EXPOSE 8888
ADD ./target/logs-bugs-configuration-managment-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]