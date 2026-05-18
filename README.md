# spring-cgv-23rd

## 1️⃣ 캐시 도입하기

### 캐시란?

- 자주 조회되지만 자주 바뀌지 않는 데이터를 잠깐 저장해두고 다음 요청에서는 원본 저장소(DB)까지 다시 가지 않도록 도와주는 장치
- 목적: `응답 시간 단축`, `DB 부하 감소`, `같은 조회 반복 방지`
- 모든 데이터를 캐시하면 좋은 건 아님
- 자주 바뀌는 데이터까지 무리하게 캐시하면 오래된 값을 내려주는 문제가 생길 수 있음

### CGV 클론 코딩에 캐시를 넣은 이유

- 현재 CGV 프로젝트는 조회 API와 인증 요청이 반복적으로 많이 들어올 수 있는 구조였음
- `GET /api/screenings`, `GET /api/screenings/{screeningId}/seats`, `GET /api/store/menus` 같은 read 경로는 계속 호출될 가능성이 높다고 봤음
- 좌석 조회 쪽은 예약 상태뿐 아니라 좌석 배치 정보까지 같이 읽고 있어서 정적인 데이터와 동적인 데이터를 나눠서 보는 게 필요했음
- JWT 인증이 필요한 API에서는 요청마다 사용자 정보를 다시 조회하는 비용이 있었고, 이 부분도 캐시 후보라고 판단했음

### 어떤 캐시를 선택했는지

- `Spring Cache + Caffeine`
- 이유
  - 현재 프로젝트는 단일 Spring Boot 애플리케이션 중심 구조였음
  - Redis 같은 외부 캐시를 따로 붙이기보다 애플리케이션 내부 메모리 캐시가 더 단순했음
  - Caffeine은 Spring Cache와 붙이기 쉬웠고, TTL이나 최대 크기 설정도 비교적 간단했음

### 어떤 캐싱 전략을 썼는지

- 기본 전략은 `Cache-Aside`로 잡았음
  - 조회 시 캐시에 값이 있으면 캐시를 그대로 사용
  - 없으면 DB를 조회한 뒤 캐시에 저장하는 방식으로 구현했음
- 쓰기 요청이 발생해서 데이터가 바뀌는 경우에는 `cache update`보다 `cache evict`가 안전하다고 봤음
- 매점 메뉴처럼 구매 이후 재고가 달라지는 데이터는 write 시점에 캐시를 비우고, 다음 조회 때 다시 채우도록 했음

### 어디에 캐시를 적용했는지

| 대상 | 캐시 이름 | 전략 | TTL | 이유 |
| --- | --- | --- | --- | --- |
| `GET /api/screenings` | `screenings` | Cache-Aside | 3분 | 상영 목록은 조회가 자주 일어나고 매 요청마다 DB를 다시 읽지 않아도 될 가능성이 높다고 봤음 |
| 좌석 배치 정적 정보 (`SeatTemplate`) | `seatTemplates` | Cache-Aside | 30분 | 좌석 배치는 거의 바뀌지 않지만 좌석 조회 API에서 반복적으로 사용되기 때문이었음 |
| `GET /api/store/menus?cinemaId=...` | `storeMenus` | Cache-Aside + Evict on Write | 60초 | 메뉴 조회는 반복되지만 구매가 발생하면 재고가 바뀌기 때문에 eviction이 필요했음 |
| JWT 인증 시 `UserDetails` 조회 | `authUserDetails` | Cache-Aside | 10분 | 보호 API 요청마다 같은 사용자 정보를 반복 조회하는 비용을 줄이기 위해 넣었음 |

### 왜 좌석 상태 전체는 캐시하지 않았는지

- 처음에는 `GET /api/screenings/{screeningId}/seats` 전체 응답을 캐시하는 것도 고려했음
- 그런데 이 응답에는 `reserved` 여부가 포함되어 있었음
  - 예매 생성, 결제 완료, 취소, 만료에 따라 값이 자주 바뀜
  - 이 상태까지 통째로 캐시하면 실제로는 이미 예약된 좌석이 비어 있는 것처럼 보이거나, 반대로 이미 풀린 좌석이 계속 예약된 것처럼 보일 위험이 있었음
- 그래서 `SeatTemplate` 같은 정적 데이터만 캐시하고, 실제 예약 상태는 매번 DB에서 계산하도록 했음
- 정리하면 `정적 데이터는 캐시`, `동적 예약 상태는 실시간 조회`로 나눴음

### 실제 구현 내용

- `build.gradle`에 `spring-boot-starter-cache`, `caffeine` 의존성을 추가했음
- `CacheConfig`에서 캐시별 TTL, 최대 크기, stats 기록 여부를 설정했음
- `ScreeningQueryService.getScreenings()`에 상영 목록 캐시를 적용했음
- `SeatTemplateCacheService`를 따로 두고 좌석 배치 정보를 스냅샷 형태로 캐시했음
- `StoreQueryService.getStoreMenus()`는 DTO 응답 기준으로 캐시했음
- `StorePurchaseService.purchase()`가 성공하면 해당 영화관 메뉴 캐시를 비우도록 했음
- `CustomUserDetailsService.loadUserByUsername()`에도 캐시를 붙여서 JWT 인증 필터 경로에서 같은 사용자 조회가 반복되지 않게 했음

### 기대한 효과

- `GET /api/screenings`
  - 반복 조회 시 DB 접근 횟수를 줄일 수 있게 되었음
- `GET /api/store/menus`
  - 자주 열리는 화면에서 응답 속도와 DB 부하 측면에서 이점이 생기도록 했음
- `GET /api/screenings/{screeningId}/seats`
  - 좌석 상태 전체를 캐시하지 않으면서도 정적인 좌석 배치 조회 비용은 줄일 수 있게 했음

### 검증

- `./gradlew test` 통과
- `ScreeningQueryServiceTest`
  - 상영 목록 캐시와 좌석 배치 캐시가 채워지는지 확인했음
- `StoreQueryServiceTest`
  - 메뉴 캐시가 채워지고, 구매 후에는 eviction 되는지 확인했음
- `CustomUserDetailsServiceTest`
  - 인증용 사용자 조회가 첫 조회 이후 캐시에서 재사용되는지 확인했음

## 2️⃣ 로그 리팩토링 하기

- 운영 관점에서 요청 흐름을 한 번에 따라가기 위해서
- 예매 / 결제 / 매점 구매처럼 장애가 나면 바로 확인해야 하는 구간은 있음
  - 하지만 어떤 요청이 왜 실패했는지 맥락이 충분하지 않음

### 이번에 잡은 방향

1. 모든 요청에 `requestId`를 붙여서 요청 단위 추적이 가능하게 했음
2. 운영 로그와 감사성 로그를 분리했음
3. 중요한 도메인 이벤트는 JSON 형태로 남기게 했음
4. 로그 리팩토링과 함께 Prometheus + Grafana로 볼 수 있는 지표도 같이 추가했음

### 어떻게 적용했는지

#### 1. Request 단위 추적

- `RequestLoggingFilter`에서 요청마다 `X-Request-Id`를 생성하거나 기존 헤더를 그대로 사용하게 했음
- `MDC`에 `requestId`, `method`, `path`를 넣어서 같은 요청에서 발생한 로그를 묶어 볼 수 있게 했음
- 요청 종료 시점에는 `status`, `latencyMs`, `userId`, `clientIp`까지 같이 남기도록 했음

예시

```text
2026-05-16 21:40:12.123 INFO  [c4d5a40f-1e0f-4c73-9d28-0f7d7c5f3a2b] [http-nio-8080-exec-1] ... - event=request_complete method=POST path=/api/reservations status=200 latencyMs=41 userId=1 clientIp=127.0.0.1
```

#### 2. 운영 로그 / 감사 로그 분리

- 일반 운영 로그는 `logs/application.log`에 남기도록 했음
- 감사성 이벤트 로그는 `logs/audit/audit.log`에 따로 남기도록 했음
- 운영 로그는 사람이 빨리 읽기 좋은 형식으로 두고, 감사 로그는 JSON 한 줄 구조로 남기게 했음
- 이렇게 나누면 장애 대응용 로그와 이벤트 추적용 로그를 따로 보기 쉬웠음

예시

```json
{"timestamp":"2026-05-16T21:40:12.140","level":"INFO","event":"reservation_created","requestId":"c4d5a40f-1e0f-4c73-9d28-0f7d7c5f3a2b","method":"POST","path":"/api/reservations","userId":1,"screeningId":3,"reservationId":15,"paymentId":"pay-123","seatCount":2}
```

#### 3. 어떤 지점에 로그를 넣었는지

| 위치 | 남긴 로그 | 이유 |
| --- | --- | --- |
| `RequestLoggingFilter` | 모든 요청의 성공/실패, 응답 시간, 상태 코드 | 요청 단위 흐름을 가장 바깥에서 공통으로 보기 위해 |
| `GlobalExceptionHandler` | `request_error`, `request_validation_error`, `request_unhandled_error` | 예외가 어디서 얼마나 발생하는지 바로 보기 위해 |
| `AuthService` | 회원가입 / 로그인 / 토큰 재발급 / 로그아웃 성공·실패 | 인증 실패율과 보안성 이벤트를 확인하기 위해 |
| `ReservationService` | 예매 생성 / 결제 확정 / 취소 / 만료 배치 | 핵심 도메인인 좌석 선점 흐름을 추적하기 위해 |
| `PaymentService` | 결제 시작 / 완료 / 취소 / 만료 | 결제 단계별 실패 지점을 분리해서 보기 위해 |
| `StorePurchaseService` | 매점 구매 성공·실패 | 재고 부족, 메뉴 없음 같은 실패 상황을 구분하기 위해 |

### 어떤 로그 전략을 썼는지

- 모든 메서드에 무조건 상세 로그를 넣기보다 의미 있는 이벤트 위주로 남기려고 했음
- Controller 진입/종료는 필터에서 공통 처리했음
- Service에서는 실제로 운영에서 궁금한 도메인 이벤트만 남겼음
- 성공 로그는 `INFO`, 처리 가능한 문제는 `WARN`, 예상하지 못한 예외는 `ERROR`로 나눴음
- 로그 때문에 본 기능이 깨지면 안 된다고 생각
  - 로그 payload는 null-safe 하게 처리

### 추가한 지표

| 지표 | 의미 | 어디에 사용 |
| --- | --- | --- |
| `http.server.requests` | API 처리량, 상태 코드별 요청 수, 응답 시간 | 전체 트래픽 / 지연 / 에러율 확인 |
| `cgv.auth.events` | 로그인, 회원가입, refresh, logout 성공·실패 수 | 인증 실패율 확인 |
| `cgv.reservation.events` | 예매 생성 / 확정 / 취소 / 만료 이벤트 수 | 예매 흐름 이상 징후 확인 |
| `cgv.payment.events` | 결제 시작 / 완료 / 취소 / 만료 이벤트 수 | 결제 단계별 장애 확인 |
| `cgv.store.purchase.events` | 매점 구매 성공·실패 수 | 재고 관련 오류나 구매 실패 모니터링 |
| `cgv.reservation.expired.count` | 결제 대기 만료 건수 | 좌석 홀드 만료가 비정상적으로 늘어나는지 확인 |

### Grafana 대시보드에 추가한 패널

- `API Throughput`
  URI / Method 기준 요청 처리량을 봄
- `API P95 Latency`
  어느 API가 느려지는지 빠르게 보기 위해 넣었음
- `API Error Rate`
  4xx / 5xx 비율이 어디서 올라가는지 보기 위해 넣었음
- `Auth Events`
  로그인 / refresh 실패가 갑자기 치솟는지 보기 위해 넣었음
- `Reservation Events`
  예매 생성, 취소, 만료 흐름을 시간대별로 보기 위해 넣었음
- `Store Purchase Events`
  매점 구매 성공/실패 추이를 보기 위해 넣었음

### 왜 이 지표들이 유용할까?

- 예매 서비스는 `느려졌는지`, `실패가 늘었는지`, `어느 단계에서 터졌는지`를 빨리 알아야 함
- 단순한 CPU / 메모리보다도 실제 사용자 요청에 가까운 지표를 먼저 보게 했음
- `Reservation Events`, `Payment Events`
  - 기능 장애를 서비스 흐름 기준으로 바로 파악할 수 있음
- `requestId`: 로그에 같이 남음
  - Grafana에서 이상 징후를 보고 다시 로그 파일로 내려가서 원인 추적하기도 쉬움

### 실행 방법

```bash
docker compose -f docker-compose.observability.yml up -d
```

3. Prometheus: `http://localhost:9090`
4. Grafana: `http://localhost:3000`
5. 애플리케이션 메트릭은 `http://localhost:8080/actuator/prometheus` 에서 수집
6. 대시보드는 provisioning 파일로 자동 등록되게 구성

### 해보면서 느낀 점

- 로그는 많이 찍는 것보다 맥락 있게 남기는 게 훨씬 중요
- `requestId` 하나만 붙여도 장애 추적 난이도가 꽤 많이 내려갔음
- 도메인 이벤트를 로그와 지표 둘 다로 남기니까 "무슨 일이 있었는지","얼마나 자주 있었는지"를 같이 볼 수 있음
