FROM public.ecr.aws/amazonlinux/amazonlinux:latest
RUN apt-get install openjdk-11-jdk
ARG SERVICE_VERSION
ADD build/libs/FreeCoursesService-$SERVICE_VERSION.jar webserver.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "webserver.jar"]
