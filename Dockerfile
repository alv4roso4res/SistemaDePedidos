# ---------------------------------------------------------------------------
# Estagio 1: build
# ---------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Copia so o pom primeiro: enquanto as dependencias nao mudarem, o Docker
# reaproveita esta camada e nao baixa tudo de novo a cada build.
COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------------------------------------------------------------------------
# Estagio 2: runtime
# ---------------------------------------------------------------------------
# Imagem final so com a JRE: nao carrega Maven, codigo-fonte nem cache do build.
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Roda como usuario sem privilegios em vez de root
RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=build /build/target/*.jar app.jar

USER spring

# Perfil de producao por padrao. As credenciais do banco NAO ficam na imagem:
# sao injetadas em tempo de execucao (docker run -e / --env-file, ou as
# variaveis de ambiente da plataforma de deploy).
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# MaxRAMPercentage deixa a JVM respeitar o limite de memoria do container.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
