FROM eclipse-temurin:11
ADD build/libs/FreeCoursesService-0.0.1-SNAPSHOT.jar webserver.jar
ADD native-libs/* native-libs/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "webserver.jar", "-Dnative.libs=/native-libs"]
