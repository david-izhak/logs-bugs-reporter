FROM adoptopenjdk/openjdk15:x86_64-alpine-jre-15.0.2_7
EXPOSE 8787
ADD ./target/logs-bugs-discovery-server-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]