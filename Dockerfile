# Stage 1: Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copying pom.xml first for efficient dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copying the source code and building the application
COPY src ./src
# Build application
RUN mvn clean package -DskipTests
# Extract the jar file
RUN jar xf target/misc-tests-0.0.1-SNAPSHOT.jar
# Get modules to deps file
RUN jdeps --ignore-missing-deps -q --recursive --multi-release 17 --print-module-deps --class-path 'BOOT-INF/lib/*' target/misc-tests-0.0.1-SNAPSHOT.jar > deps.info && cat deps.info
# Create custom JRE
RUN jlink --module-path $JAVA_HOME/jmods --add-modules $(cat deps.info) --compress 2 --no-header-files --no-man-pages --output custom-jre/

FROM ubuntu:jammy AS runner

#Set the JAVA_HOME environment variable
ENV JAVA_HOME=/usr/java
ENV PATH=$JAVA_HOME/bin:$PATH

# Copy the custom JRE from the build stage
COPY --from=build /app/custom-jre $JAVA_HOME

WORKDIR /app

# Copy the application jar file
COPY --from=build /app/target/misc-tests-0.0.1-SNAPSHOT.jar /app/app.jar

# Run the application
CMD ["java", "-jar", "./app.jar"]