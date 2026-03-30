# 1단계: 빌드 스테이지
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# 2단계: 실행 스테이지 (경량 이미지)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# curl 설치 (healthcheck 용도)
RUN apk add --no-cache curl

# 보안: root가 아닌 일반 유저로 실행
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/emp/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
