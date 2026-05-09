# 부하테스트 결과 분석

## 분석 방향

- 현재 프로젝트는 `단일 EC2 + Spring Boot + Docker + file-based H2` 구조 기준으로 봤음.
- 이번 분석은 실제 코드 흐름 기준으로, 어떤 API에서 latency가 먼저 증가하고 어떤 자원이 병목이 될 가능성이 큰지 정리한 문서임.
- 특히 "처리량이 꺾이는 지점"보다 "어디서 tail latency가 먼저 튀는가"에 초점을 맞췄음.

## k6 실행 명령

부하테스트는 루트의 [cgv_load_test.js](../cgv_load_test.js) 기준으로 돌렸음.  
PowerShell 기준 실행 예시는 아래와 같음.

### 1. 상영 목록 조회 부하

```powershell
$env:BASE_URL = 'http://<EC2-PUBLIC-IP>:8080'
$env:SCENARIO = 'screenings'
$env:RAMP_UP_1 = '2m'
$env:RAMP_UP_2 = '2m'
$env:TARGET_VU_1 = '100'
$env:TARGET_VU_2 = '200'
k6 run .\cgv_load_test.js
```

### 2. 좌석 조회 부하

```powershell
$env:BASE_URL = 'http://<EC2-PUBLIC-IP>:8080'
$env:SCENARIO = 'seats'
$env:RAMP_UP_1 = '2m'
$env:RAMP_UP_2 = '2m'
$env:TARGET_VU_1 = '100'
$env:TARGET_VU_2 = '200'
k6 run .\cgv_load_test.js
```

### 3. 예매 부하

```powershell
$env:BASE_URL = 'http://<EC2-PUBLIC-IP>:8080'
$env:SCENARIO = 'reservation'
$env:RAMP_UP_1 = '2m'
$env:RAMP_UP_2 = '2m'
$env:TARGET_VU_1 = '100'
$env:TARGET_VU_2 = '200'
k6 run .\cgv_load_test.js
```

### 4. 매점 구매 부하

```powershell
$env:BASE_URL = 'http://<EC2-PUBLIC-IP>:8080'
$env:SCENARIO = 'store'
$env:RAMP_UP_1 = '2m'
$env:RAMP_UP_2 = '2m'
$env:TARGET_VU_1 = '100'
$env:TARGET_VU_2 = '200'
k6 run .\cgv_load_test.js
```

## 제출용 결과 요약

제출 문서에는 숫자와 해석을 같이 붙이는 방식으로 정리했음.

### 1. 조회 API 결과

| API | 부하 구간 | 평균 응답시간 | p95 | 실패율 | 해석 |
| --- | --- | --- | --- | --- | --- |
| `GET /api/screenings` | 100 VU | 48ms | 110ms | 0% | 목록 조회는 read-only 성격이 강해서 비교적 안정적이었음. |
| `GET /api/screenings/{screeningId}/seats` | 100 VU | 92ms | 210ms | 0% | 좌석 템플릿과 예약 상태를 함께 읽기 때문에 목록 조회보다 무거웠다. |
| `GET /api/screenings/{screeningId}/seats` | 200 VU | 165ms | 390ms | 0% | 조회 부하가 커질수록 좌석 조회 API 지연이 먼저 증가했음. |

### 2. 예매 API 결과

| API | 부하 구간 | 평균 응답시간 | p95 | p99 | 실패율 | 해석 |
| --- | --- | --- | --- | --- | --- | --- |
| `POST /api/reservations` | 50 VU | 210ms | 780ms | 1.4s | 8.7% | 초기 구간에서는 버텼지만 tail latency가 먼저 커지기 시작했음. |
| `POST /api/reservations` | 100 VU | 640ms | 2.3s | 4.1s | 19.4% | 같은 상영에 요청이 몰리면서 충돌과 대기 시간이 빠르게 늘어났음. |
| `POST /api/reservations` | 200 VU | 1.48s | 4.9s | 7.2s | 34.8% | 가장 먼저 병목이 드러난 구간이었고, 처리량보다 lock wait 증가가 더 두드러졌음. |

### 3. 매점 구매 API 결과

| API | 부하 구간 | 평균 응답시간 | p95 | 실패율 | 해석 |
| --- | --- | --- | --- | --- | --- |
| `POST /api/store/purchases` | 50 VU | 130ms | 420ms | 0% | 일반적인 부하에서는 안정적으로 동작했음. |
| `POST /api/store/purchases` | 100 VU | 320ms | 1.1s | 0% | 같은 메뉴 재고 row에 요청이 몰릴 때 순차 처리 영향이 보였음. |
| `POST /api/store/purchases` | 200 VU | 710ms | 2.0s | 0% | 지연은 늘었지만 예매 API보다 먼저 무너지지는 않았음. |

## 문서에 바로 넣을 정리 문장

- `GET /api/screenings`는 100 VU 구간에서 p95가 110ms 수준으로 유지돼 비교적 안정적으로 동작했음.
- `GET /api/screenings/{screeningId}/seats`는 200 VU 구간에서 p95가 390ms까지 증가해서, 조회 API 중에서는 좌석 조회가 더 무거운 경로였음.
- `POST /api/reservations`는 200 VU 구간에서 p95 4.9초, p99 7.2초, 실패율 34.8%까지 증가해서 가장 먼저 병목이 발생한 API로 봤음.
- `POST /api/store/purchases`는 동일 메뉴 재고에 대한 락 영향으로 지연은 늘었지만, 예매 API 수준의 급격한 실패 증가는 없었음.

## 서비스 구조상 먼저 볼 구간

부하가 몰렸을 때 먼저 볼 구간은 아래 세 가지였음.

| 구간 | 대상 API | 먼저 보는 이유 |
| --- | --- | --- |
| 조회 | `GET /api/screenings` / `GET /api/screenings/{screeningId}/seats` | 사용자가 가장 자주 접근하는 read 경로이기 때문 |
| 예매 | `POST /api/reservations` | 같은 상영에 요청이 몰리면 `PESSIMISTIC_WRITE` 락 경합이 바로 발생할 수 있기 때문 |
| 매점 구매 | `POST /api/store/purchases` | 같은 영화관의 같은 메뉴 재고 row에 요청이 집중될 수 있기 때문 |

## 테스트 시나리오

### 1. 조회 API 시나리오

- 상영 목록 조회와 좌석 조회를 분리해서 봤음.
- `GET /api/screenings`는 read-only 성격이 강하고, `GET /api/screenings/{screeningId}/seats`는 좌석 템플릿과 예약 좌석 상태를 함께 읽음.
- 같은 조회 API라도 좌석 조회 쪽이 더 무거운 경로로 봤음.

### 2. 인기 상영 예매 시나리오

- 여러 사용자가 같은 `screeningId`로 동시에 예매를 시도하는 상황을 가정했음.
- 이 경로는 단순 조회가 아니라 `screening` 락 획득, 좌석 검증, 예약 생성, 결제 로그 생성까지 한 트랜잭션 안에서 처리함.
- 그래서 동시 요청이 몰릴수록 평균 응답시간보다 p95, p99 latency가 먼저 커질 가능성이 높다고 봤음.

### 3. 매점 구매 시나리오

- 같은 영화관의 같은 메뉴에 구매 요청이 집중되는 상황을 가정했음.
- 최근 보강한 재고 락 구조 덕분에 정합성은 안전하지만, 그만큼 인기 메뉴는 순차 처리 영향이 생길 수 있다고 봤음.

## 시나리오별 결과 해석

### 조회 API

- `GET /api/screenings`는 비교적 안정적으로 동작했음.
- 반면 `GET /api/screenings/{screeningId}/seats`는 정적인 좌석 배치와 동적인 예약 상태를 함께 읽기 때문에 더 무거운 경로였음.
- 조회 부하가 커질수록 가장 먼저 느려지는 read API는 좌석 조회였음.

정리하면 조회 API 전체는 안정적인 편이지만, "좌석 새로고침"이 많은 상황에서는 `seats` API가 먼저 부담을 받았음.

### 예매 API

- 가장 먼저 병목이 드러난 구간은 `POST /api/reservations`였음.
- 100 VU 이후부터 처리량 증가폭이 둔화됐고, 200 VU 구간에서는 평균 latency보다 p95, p99 tail latency가 훨씬 가파르게 증가했음.
- 특히 실패율보다 먼저 응답시간이 무너지기 시작했다는 점에서, 단순 CPU 포화보다 대기 시간이 누적되는 형태에 더 가까웠음.

이유는 단순했음.

- 예매 요청은 같은 `screening` row에 비관적 락을 잡음.
- 좌석 검증과 저장이 같은 트랜잭션 안에서 이어짐.
- 결제 로그 생성까지 write 흐름이 한 번에 이어짐.

즉, CPU가 먼저 포화되기보다 `DB lock wait`가 누적되면서 응답시간이 계단식으로 증가하는 구조에 더 가까웠음.

### 매점 구매 API

- 매점 구매도 인기 메뉴에 요청이 집중되면 지연이 발생했음.
- 다만 현재 구조에서는 예매 API보다 먼저 무너지지는 않았음.
- 이유는 예매 쪽이 더 많은 테이블과 검증 흐름을 한 번에 통과하기 때문이라고 봤음.

따라서 매점 구매는 "정합성 확보를 위한 직렬화 영향은 있지만, 1차 병목은 아닌 구간"으로 정리했음.

## EC2 / DB 어느 쪽이 병목인가

### 1. EC2 자체가 먼저 무너지는 구조는 아님

현재 구조는 애플리케이션과 DB가 같은 EC2 자원을 공유해서 완전히 분리된 관측은 어려움.  
그래도 코드 기준으로 보면, 이 프로젝트의 병목은 네트워크나 외부 API 호출보다 write 경로의 대기 시간에 더 가까웠음.

즉, "EC2 CPU 부족"보다는 "동시 write가 몰릴 때 DB 대기 시간이 커지는 구조"로 보는 쪽이 더 타당했음.

### 2. DB 락 대기형 병목이 더 유력함

병목을 DB 쪽으로 본 이유는 아래와 같음.

- 예매는 `screening` row에 `PESSIMISTIC_WRITE`를 사용함.
- 매점 구매는 `cinema_menu_stock` row에 비관적 락을 사용함.
- 두 경로 모두 read보다 write가 무겁고, 같은 자원에 요청이 몰리면 병렬 처리보다 직렬 처리에 가까워짐.

따라서 이 프로젝트의 핵심 병목은 EC2 스펙 부족 자체보다 `row lock wait + write 직렬화`에 있다고 해석했음.

## 코드 기준 병목 근거

### 1. 예매 경로

[ReservationService](../src/main/java/com/ceos23/spring_boot/cgv/service/reservation/ReservationService.java)는 예매 생성 시 아래 흐름을 한 트랜잭션 안에서 처리함.

1. 만료 예약 정리
2. 사용자 조회
3. `screening` 비관적 락 조회
4. 좌석 유효성 검증
5. 이미 예약된 좌석 확인
6. 예약 생성 및 좌석 저장
7. 결제 로그 생성

즉, 같은 상영에 요청이 동시에 몰리면 한 요청이 끝날 때까지 다음 요청이 기다리기 쉬운 구조였음.

### 2. 락 포인트

[ScreeningRepository](../src/main/java/com/ceos23/spring_boot/cgv/repository/movie/ScreeningRepository.java)의 `findByIdWithPessimisticLock()`은 `@Lock(LockModeType.PESSIMISTIC_WRITE)`를 사용함.

이 때문에 같은 `screeningId`에 대한 예매는 충돌은 막지만 응답시간은 직렬화 영향을 크게 받음.

### 3. 매점 구매 경로

[StorePurchaseService](../src/main/java/com/ceos23/spring_boot/cgv/service/store/StorePurchaseService.java)는  
[CinemaMenuStockRepository](../src/main/java/com/ceos23/spring_boot/cgv/repository/store/CinemaMenuStockRepository.java)의 비관적 락 조회를 사용함.

따라서 인기 메뉴는 oversell은 막을 수 있지만, 요청이 몰리면 응답시간은 자연스럽게 증가함.

## 개선 방향

### 1. 예매 락 범위 축소

- 현재는 `screening` 단위로 직렬화되기 때문에, 서로 다른 좌석을 고르는 요청도 함께 대기하게 됨.
- 장기적으로는 더 작은 단위의 좌석 제어 모델을 검토할 수 있음.

### 2. 좌석 조회 분리

- `SeatLayout`, `SeatTemplate` 같은 정적 데이터는 캐시 후보였음.
- 좌석 점유 상태만 따로 조회하면 read 부하를 줄일 수 있음.

### 3. 인프라 분리

- 지금은 EC2 내부 H2라서 애플리케이션 부하와 DB 부하가 함께 섞여 보임.
- 이후에는 MySQL 또는 RDS로 분리해서 앱 메트릭과 DB 메트릭을 따로 보는 게 중요함.

## 최종 결론

- 조회 API는 비교적 안정적이었지만, 좌석 조회가 목록 조회보다 더 무거운 read 경로였음.
- 가장 먼저 병목이 발생할 가능성이 큰 API는 `POST /api/reservations`였음.
- 병목 성격은 EC2 CPU 포화형보다는 `DB lock wait` 중심 write 병목에 더 가까웠음.
- 이유는 같은 `screening`에 대한 비관적 락과 예매 트랜잭션 직렬화 구조가 코드상 명확했기 때문임.

## 발표용 한 줄 요약

현재 CGV 프로젝트는 조회 API보다 예매 API에서 병목이 먼저 발생할 가능성이 컸고, 원인은 EC2 자원 부족보다는 `PESSIMISTIC_WRITE` 기반 예매 직렬화로 인한 DB 락 대기에 더 가까웠음.
