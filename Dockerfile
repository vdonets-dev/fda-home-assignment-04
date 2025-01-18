FROM gradle:8.1.1-jdk17-jammy AS build
WORKDIR /app

COPY build.gradle settings.gradle /app/
RUN gradle dependencies --no-daemon

COPY . /app
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
