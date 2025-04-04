FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy and build domain-2 first
COPY domain-2/pom.xml domain-2/
COPY domain-2/src/ domain-2/src/
RUN cd domain-2 && mvn clean install -DskipTests

# Then build kitchen-domain
COPY kitchen-domain/pom.xml kitchen-domain/
COPY kitchen-domain/src/ kitchen-domain/src/
RUN cd kitchen-domain && mvn clean package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/kitchen-domain/target/*.jar app.jar

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl 

ARG JWT_SECRET
ARG JWT_EXPIRATION

ENV JWT_SECRET=${JWT_SECRET}
ENV JWT_EXPIRATION=${JWT_EXPIRATION}
ENV TZ=Asia/Ho_Chi_Minh

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8083/kitchen/actuator/health || exit 1

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]