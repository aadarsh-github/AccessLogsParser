FROM openjdk:8
EXPOSE 8080
ADD target/AccessLogsParser-1.0-SNAPSHOT.jar AccessLogsParser-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/AccessLogsParser-1.0-SNAPSHOT.jar"]