# ---- Build -------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B -q

COPY src ./src
RUN mvn clean package -DskipTests -B -q

# ---- Runtime -----------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

RUN groupadd --gid 1001 app && \
    useradd --uid 1001 --gid app --shell /bin/false app

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

RUN chown app:app app.jar

USER app

EXPOSE 8121

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8121/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Dspring.profiles.active=prod", \
    "-jar", "app.jar"]
