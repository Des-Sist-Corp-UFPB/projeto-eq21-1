# =============================================================================
# Dockerfile de PRODUÇÃO — Multi-stage build
# =============================================================================
# Estágio 1 (frontend): builda o React com Vite e gera os arquivos estáticos.
# Estágio 2 (builder):  compila o Spring Boot e copia o dist do React para
#                       src/main/resources/static/ — Spring Boot os serve.
# Estágio 3 (runtime):  imagem mínima com apenas o JRE.
# =============================================================================

# ---- Estágio 1: Build do Frontend -------------------------------------------
FROM node:20-alpine AS frontend-builder

WORKDIR /frontend

COPY frontend/package.json ./
RUN npm install

COPY frontend/ ./
RUN npm run build

# ---- Estágio 2: Build do Backend --------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B -q || true

COPY src ./src

# Copia o dist do React para os recursos estáticos do Spring Boot
COPY --from=frontend-builder /frontend/dist ./src/main/resources/static/

RUN mvn clean package -DskipTests -B

# ---- Estágio 3: Runtime -----------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

RUN groupadd --gid 1001 mercado && \
    useradd --uid 1001 --gid mercado --shell /bin/false mercado

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

RUN chown mercado:mercado app.jar

USER mercado

EXPOSE 8121

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8121/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Dspring.profiles.active=prod", \
    "-jar", "app.jar"]
