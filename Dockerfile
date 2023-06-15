
FROM eclipse-temurin:17


COPY api/build/libs/api.jar /api.jar

ENTRYPOINT ["java", "-jar", "api.jar"]