FROM eclipse-temurin:21-jre-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 비관리자 계정 생성
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 필요한 도구 설치
RUN apk add --no-cache curl

#  파일 복사 및 권한 설정
COPY --chown=appuser:appgroup build/libs/*.jar app.jar

# 비관리자 계정으로 전환
USER appuser

#  포트 설정
EXPOSE 8080

# HealthCheck
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -fs http://localhost:8080/actuator/health || exit 1

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]