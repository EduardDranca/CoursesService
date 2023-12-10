FROM public.ecr.aws/amazonlinux/amazonlinux:latest
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk
ARG SERVICE_VERSION
ADD build/libs/FreeCoursesService-$SERVICE_VERSION.jar webserver.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "webserver.jar"]
