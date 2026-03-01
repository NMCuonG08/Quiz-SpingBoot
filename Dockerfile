# ================================
# Stage 1: Build
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml và download dependencies trước (tận dụng Docker cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code và build (skip tests - tests chạy riêng)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ================================
# Stage 2: Runtime
# ================================
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Employee Management System"
LABEL version="1.0.0"

# Tạo user không phải root để bảo mật
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy jar từ builder stage
COPY --from=builder /app/target/Employee-1.0-SNAPSHOT.jar app.jar

# Tạo thư mục logs
RUN mkdir -p /app/logs && chown -R spring:spring /app

USER spring

# Expose port ứng dụng
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# JVM tuning cho container
ENV JAVA_OPTS="-Xms256m -Xmx512m \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=prod"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
