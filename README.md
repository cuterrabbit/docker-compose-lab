# Docker HealthCheck - Spring Boot + MySQL

## 1. 프로젝트 개요

Docker의 `healthcheck`와 `depends_on` 조건을 활용하여 컨테이너 기동 순서를 제어하는 실습 프로젝트입니다.

Spring Boot 애플리케이션이 MySQL이 완전히 준비되기 전에 먼저 기동될 경우 발생하는 DB 연결 실패 문제를 해결하는 것이 핵심 목표입니다.

---

## 2. Docker HealthCheck란?

Docker HealthCheck는 컨테이너가 **정상적으로 동작하는지 주기적으로 확인하는 메커니즘**입니다.

단순히 프로세스가 실행 중인지(running)를 넘어서, 실제로 서비스가 요청을 처리할 수 있는 상태인지를 판단합니다.

### 상태 종류

| 상태 | 의미 |
|------|------|
| `starting` | 컨테이너 기동 중, healthcheck 아직 미실행 |
| `healthy` | healthcheck 명령이 성공적으로 통과된 상태 |
| `unhealthy` | healthcheck 명령이 연속으로 실패한 상태 |

### depends_on 조건

`depends_on`의 `condition` 옵션으로 컨테이너 시작 순서를 제어할 수 있습니다:

| condition | 의미 |
|-----------|------|
| `service_started` | 컨테이너가 시작되기만 하면 됨 (기본값) |
| `service_healthy` | 컨테이너가 healthy 판정을 받아야 함 |
| `service_completed_successfully` | 컨테이너가 정상 종료(exit 0) 되어야 함 |

---

## 3. 핵심 설정

### docker-compose.yml - DB healthcheck

```yaml
services:
  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: fisa
      MYSQL_USER: user01
      MYSQL_PASSWORD: user01
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-uroot", "-proot"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 30s

  app:
    build: .
    ports:
      - "8081:8081"
    depends_on:
      db:
        condition: service_healthy  # db가 healthy 상태일 때만 app 시작
```

- `healthcheck.test`: `mysqladmin ping` 명령으로 MySQL이 실제로 쿼리를 받을 수 있는 상태인지 확인
- `start_period`: 컨테이너 기동 초기 30초는 healthcheck 실패를 무시 (MySQL 초기화 시간 확보)
- `condition: service_healthy`: `db` 컨테이너가 healthy 판정을 받기 전까지 `app` 컨테이너 시작을 대기

### Dockerfile - app healthcheck

```dockerfile
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
```

- `start_period: 60s`: Spring Boot 기동 시간을 고려하여 초기 60초는 체크 유예
- `CMD curl -f .../actuator/health`: Spring Boot Actuator의 health 엔드포인트로 앱 상태 확인

### application.properties - DB 연결 설정

```properties
spring.datasource.url=jdbc:mysql://db:3306/fisa?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=user01
spring.datasource.password=user01
```

- Docker Compose 내에서는 컨테이너 IP 대신 **서비스명(db)** 으로 통신

---

## 4. 실행 방법

```bash
# 이미지 빌드 및 컨테이너 기동
docker compose up --build
```

### 접속 URL

| URL | 설명 |
|-----|------|
| `http://localhost:8081/emp/deptall` | 전체 부서 목록 조회 |
| `http://localhost:8081/emp/get?ename=이름` | 직원 이름으로 조회 |
| `http://localhost:8081/emp/dept?deptno=10` | 부서별 직원 조회 |
| `http://localhost:8081/emp/actuator/health` | 앱 헬스체크 상태 |

---

## 5. 실험

### 실험 조건별 결과

| 실험 조건 | app 컨테이너 생성 여부 | 비고 |
|-----------|----------------------|------|
| MySQL 정상 기동 (healthy) | ✅ 생성됨 | 정상 케이스 |
| MySQL 기동 중 (starting) | ⏳ 대기 후 생성 | healthy 될 때까지 대기 |
| MySQL 중단 상태 (unhealthy) | ❌ 생성 안 됨 | `condition: service_healthy` 효과 |
| condition 설정 없는 경우 | ✅ 생성되나 앱 오류 | DB 준비 전 접속 시도 |

### 실험 1. 정상 기동

```bash
docker compose up --build
docker compose ps
```

> 스크린샷 첨부

### 실험 2. MySQL 기동 중 (starting) 상태에서 app 대기 확인

`docker compose up` 직후 빠르게 확인:

```bash
docker compose ps
```

> `db` 상태가 `starting`일 때 `app`이 대기하는 모습 스크린샷 첨부

### 실험 3. MySQL 강제 종료 후 app 동작 확인

```bash
docker compose stop db
docker compose ps -a
```

> `db`는 Exited, `app`은 Up 상태 스크린샷 첨부

```bash
curl http://localhost:8081/emp/deptall
```

> DB 연결 실패 에러 스크린샷 첨부

### 실험 4. condition 없이 실행 (비교 실험)

`docker-compose.yml`에서 condition 제거:

```yaml
depends_on:
  db:
    condition: service_started  # healthy → started 로 변경
```

```bash
docker compose up --build
docker compose logs app
```

> MySQL 준비 전 app이 먼저 뜨면서 발생하는 연결 실패 로그 스크린샷 첨부

---

## 6. 고찰

### depends_on의 역할과 한계

| 상황 | 동작 |
|------|------|
| 최초 기동 시 db가 준비 안 된 경우 | app 기동 대기 → **해결됨** |
| 기동 후 db가 장애 발생한 경우 | app은 계속 실행, 요청 시 에러 → **미해결** |

`depends_on: condition: service_healthy`는 **컨테이너 시작 시점의 순서만 보장**합니다.
일단 app이 기동된 이후 DB가 다운되면, app 컨테이너는 살아있지만 DB 요청 시 에러가 발생합니다.

### 실운영에서 추가로 필요한 대책

1. **Connection Pool 재연결 설정**
   ```properties
   spring.datasource.hikari.connection-timeout=30000
   spring.datasource.hikari.maximum-pool-size=10
   ```

2. **컨테이너 재시작 정책**
   ```yaml
   app:
     restart: on-failure
   ```

3. **애플리케이션 레벨 재시도 로직** (Spring Retry 등)

### 결론

Docker healthcheck와 `depends_on`은 **기동 순서 문제를 해결하는 첫 번째 방어선**입니다.
하지만 런타임 장애에 대응하려면 애플리케이션 레벨의 재연결 전략과 컨테이너 재시작 정책을 함께 설계해야 합니다.
