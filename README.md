# spring-cgv-23rd
## CEOS 23기 백엔드 스터디 - CGV 클론 코딩 프로젝트

# ERD

과제 요구사항은 다음과 같습니다.

**구현 기능**
1. 영화관 조회
2. 영화관 찜
3. 영화 조회
4. 영화 예매, 취소
5. 영화 찜
6. 매점 구매 (환불X)
- 기타 기능 설명
    - 모든 영화관에 특별관과 일반관이 존재해요
    - 특별관, 일반관 종류가 같다면 좌석은 동일해요
    - 좌석은 직사각형 형태로 존재해요 (중간에 비어있는 곳 없음, 통로 고려X)
    - 영화관마다 매점이 있으며 재고를 따로 관리해요 (재고는 항상 1 이상이에요)
    - 모든 영화관의 매점 메뉴는 같아요

---

<img width="1635" height="834" alt="Image" src="https://github.com/user-attachments/assets/9cb8d25f-9a83-4f26-9eea-671dcee1282c" />

### 🎞️ MOVIE (영화)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| movie_id | PK | 영화 고유 ID |
| title | varchar | 영화 제목 |
| running_time | int | 러닝타임 (분) |
| rating | varchar | 관람 등급 |
| release_date | date | 개봉일 |
| genre | varchar | 장르 |
| prologue | text | 줄거리 |
| poster_url | varchar | 포스터 이미지 |

**관계**
- 1 : N → SCHEDULE
- 1 : N → MOVIE_WISH

---

### 🗓️ SCHEDULE (상영 일정)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| schedule_id | PK | 상영 일정 ID |
| start_time | datetime | 상영 시작 시간 |
| hall_id | FK | 상영관 ID |
| movie_id | FK | 영화 ID |

**관계**
- N : 1 → MOVIE
- N : 1 → HALL
- 1 : N → RESERVATION

---

### 🏢 THEATER (영화관)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| theater_id | PK | 영화관 ID |
| name | varchar | 지점명 |
| location | varchar | 지역 |
| address | varchar | 상세 주소 |

**관계**
- 1 : N → HALL
- 1 : N → STORE_ORDER
- 1 : N → THEATER_WISH

---

### 🎦 HALL (상영관)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| hall_id | PK | 상영관 ID |
| name | varchar | 상영관 이름 |
| theater_id | FK | 영화관 ID |
| type_id | FK | 상영관 종류 ID |

**관계**
- N : 1 → THEATER
- N : 1 → HALL_TYPE
- 1 : N → SCHEDULE

---

### 🧩 HALL_TYPE (상영관 종류)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| type_id | PK | 타입 ID |
| type_name | varchar | IMAX, 4DX, 일반관 |
| row_count | int | 좌석 행 수 |
| col_count | int | 좌석 열 수 |

---

## 🎟️ 2. Booking Domain (예매 시스템)

### 🧾 RESERVATION (예매)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| res_id | PK | 예매 ID |
| status | varchar | BOOKED / CANCELED |
| user_id | FK | 사용자 ID |
| schedule_id | FK | 상영 일정 ID |

**관계**
- N : 1 → USER
- N : 1 → SCHEDULE
- 1 : N → RESERVED_SEAT

---

### 💺 RESERVED_SEAT (예매 좌석)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| seat_row | int | 좌석 행 |
| seat_col | int | 좌석 열 |
| res_id | FK | 예매 ID |

**특이사항**
- (seat_row, seat_col, schedule_id) → 좌석 중복 방지
- 복합키 또는 Unique 제약 사용

---

## 👤 3. User & Convenience Domain

### 👤 USER (사용자)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| user_id | PK | 사용자 ID |
| login_id | varchar | 로그인 ID (Unique) |
| password | varchar | 비밀번호 |
| name | varchar | 사용자 이름 |

**관계**
- 1 : N → RESERVATION
- 1 : N → MOVIE_WISH
- 1 : N → THEATER_WISH
- 1 : N → STORE_ORDER

---

### 🎬 MOVIE_WISH (영화 찜)

| 컬럼명 | 타입 |
|--------|------|
| user_id | FK |
| movie_id | FK |

→ 복합 PK (user_id, movie_id)

---

### 🏢 THEATER_WISH (영화관 찜)

| 컬럼명 | 타입 |
|--------|------|
| user_id | FK |
| theater_id | FK |

→ 복합 PK (user_id, theater_id)

---

## 🍿 4. Store Domain (매점)

### 🛒 STORE_ORDER (매점 주문)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| order_id | PK | 주문 ID |
| total_price | int | 총 금액 |
| order_date | datetime | 주문 시간 |
| user_id | FK | 사용자 ID |
| theater_id | FK | 영화관 ID |

**관계**
- N : 1 → USER
- N : 1 → THEATER

---

### 🥤 ITEM (상품)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| item_id | PK | 상품 ID |
| name | varchar | 상품명 |
| price | int | 가격 |

---

### 📦 STORE_INVENTORY (재고)

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| theater_id | FK | 영화관 ID |
| item_id | FK | 상품 ID |
| stock | int | 재고 수량 |

→ 복합 PK (theater_id, item_id)

---

## 🔗 전체 관계 요약

- MOVIE → SCHEDULE → RESERVATION → RESERVED_SEAT
- THEATER → HALL → SCHEDULE
- USER → RESERVATION
- USER → MOVIE_WISH / THEATER_WISH
- THEATER → STORE_ORDER
- STORE_ORDER → ITEM (via order items 확장 가능)
- ITEM → STORE_INVENTORY (영화관별 재고 관리)

---

## ⚠️ 설계 핵심 포인트

- 좌석은 **행/열 기반으로 관리 (직사각형 구조)**
- 좌석 중복 방지는 **(schedule_id + row + col) unique**
- 매점 메뉴는 공통, 재고는 영화관별 관리
- 예매 취소는 상태값으로 관리 (삭제 X)
- 찜 기능은 **N:M 관계 → 교차 테이블로 분리**

---

## 3주차 미션 관련 내용 정리

### 1. Spring Security의 역할
- 애플리케이션의 **인증(Authentication)** 과 **인가(Authorization)** 를 담당한다.
- 이번 과제에서는 세션 방식 대신 **JWT 기반의 stateless 인증** 구조를 사용했다.
- `SecurityConfig`에서 어떤 요청을 허용할지, 어떤 요청에 인증이 필요한지, 어떤 요청이 관리자 권한이 필요한지를 한 곳에서 관리했다.

### 2. JwtTokenProvider의 역할
- JWT를 **생성하고, 파싱하고, 검증하는 역할**을 담당한다.
- 로그인에 성공하면 `userId`와 `loginId`를 담은 access token을 발급한다.
- 이후 요청에서는 토큰에서 사용자 정보를 다시 꺼내 인증에 활용한다.
- 즉, 토큰과 관련된 로직을 서비스나 필터에 흩어놓지 않고 **전담 클래스 하나로 분리**했다는 점이 중요했다.

### 3. JwtAuthenticationFilter의 역할
- 클라이언트 요청이 들어올 때마다 실행되며, `Authorization` 헤더에서 Bearer 토큰을 확인한다.
- 토큰이 유효하면 `loginId`를 꺼내 사용자 정보를 조회하고, Spring Security가 이해할 수 있는 `Authentication` 객체를 만든다.
- 그 인증 객체를 `SecurityContext`에 저장해서, 이후 컨트롤러나 인가 로직이 "현재 로그인한 사용자"를 사용할 수 있게 한다.
- 즉, **JWT 문자열을 실제 로그인 상태로 바꿔주는 연결 지점** 역할을 한다.

### 4. AuthenticatedUser의 역할
- `UserDetails`를 구현한 커스텀 인증 객체다.
- `userId`, `loginId`, `password`, `authorities`를 보관해서 Spring Security 내부 인증 객체로 사용된다.
- 컨트롤러에서 `@AuthenticationPrincipal AuthenticatedUser`로 바로 받아서 현재 로그인한 사용자의 `userId`를 꺼낼 수 있었다.
- 덕분에 매번 토큰을 직접 파싱하지 않고도 비즈니스 로직에서 사용자 식별이 쉬워졌다.

### 5. 인증 흐름 정리
1. 사용자가 로그인한다.
2. `AuthService`가 아이디/비밀번호를 검증한 뒤 JWT를 발급한다.
3. 이후 요청마다 클라이언트가 `Authorization: Bearer {token}` 형식으로 토큰을 보낸다.
4. `JwtAuthenticationFilter`가 토큰을 검증하고 `SecurityContext`에 인증 정보를 저장한다.
5. Spring Security가 인증/권한을 확인한 뒤 컨트롤러까지 요청을 전달한다.

### 6. Spring Security 예외 처리는 어떻게 했는가
- 일반 예외는 `GlobalExceptionHandler`에서 처리하지만, **Security 필터 단계에서 발생하는 인증/인가 예외는 별도로 처리**해야 했다.
- 인증이 안 된 사용자가 보호된 API에 접근하면 `CustomAuthenticationEntryPoint`가 동작해서 `401 Unauthorized` JSON 응답을 내려주도록 했다.
- 로그인은 했지만 권한이 없는 사용자가 접근하면 `CustomAccessDeniedHandler`가 동작해서 `403 Forbidden` JSON 응답을 내려주도록 했다.
- 그리고 이 두 클래스를 `SecurityConfig`의 `exceptionHandling()`에 등록해서, Spring Security 기본 HTML 에러 페이지 대신 **프로젝트 공통 형식의 JSON 에러 응답**을 주도록 맞췄다.

### 7. 이번 과제에서 중요하게 느낀 포인트
- JWT 인증은 "로그인 시 토큰 발급"보다도, **매 요청마다 토큰을 해석해서 SecurityContext에 넣는 과정**이 핵심이다.
- Spring Security를 쓰면 인증 정보를 컨트롤러까지 자연스럽게 전달할 수 있어서, 비즈니스 로직이 더 깔끔해진다.
- 예외 처리도 MVC 예외 처리와 Security 예외 처리가 나뉘기 때문에, **어디서 발생한 예외인지에 따라 처리 지점이 다르다**는 점을 배웠다.

---

## 4주차 미션 관련 내용 정리

## 1. 동시성 해결 방법 조사 및 적용

영화 예매 서비스에서 가장 중요한 동시성 문제는 **같은 상영 회차의 동일 좌석에 대해 여러 사용자가 동시에 예약을 시도하는 상황**이다.  
이번 과제에서는 이 문제를 해결하기 위해 여러 동시성 제어 방식을 비교했다.

### 1. 비관적 락 (Pessimistic Lock)

비관적 락은 충돌이 자주 발생할 것이라고 가정하고, 데이터를 조회하는 시점부터 락을 걸어 다른 트랜잭션의 접근을 제한하는 방식이다.  
정합성을 강하게 보장할 수 있다는 장점이 있지만, 락 범위가 넓어질수록 대기 시간이 길어지고 처리량이 줄어들 수 있다.

이 방식은 **이미 DB에 존재하는 row**를 기준으로 적용할 수 있다.  
따라서 경쟁이 치열하고, 충돌 가능성이 높으며, 데이터 정합성이 매우 중요한 경우에 적합하다.

이번 과제에서 고민한 방식은 `reserve()` 메서드 내에서 `schedule` 조회 시점에 비관적 락을 거는 방법이었다.  
하지만 이 경우 동일한 `schedule`에 대한 예약 요청 전체가 직렬화된다. 즉, 같은 상영 회차 안에서 서로 다른 좌석을 예약하는 요청도 함께 대기하게 된다.  
정합성은 확보할 수 있지만, **좌석 단위가 아니라 스케줄 단위로 경쟁을 묶어버린다는 점에서 락 범위가 너무 넓다**고 판단했다.

| 항목 | 내용 |
|------|------|
| 장점 | 정합성을 강하게 보장할 수 있음 |
| 단점 | 락 범위가 넓어지면 성능 저하와 대기 시간이 커짐 |
| 적합한 경우 | 충돌이 잦고, 동일 자원에 대한 동시 수정이 자주 발생하는 경우 |
| 과제에서의 판단 | `schedule` 단위로 직렬화되어 좌석 예매에는 락 범위가 과하다고 판단 |

### 2. 낙관적 락 (Optimistic Lock)

낙관적 락은 충돌이 자주 발생하지 않는다고 가정하고, 실제 저장 시점에 버전 정보를 비교하여 충돌 여부를 판단하는 방식이다.  
평상시에는 성능상 이점이 있지만, 충돌이 발생했을 때 재시도 로직이 필요하다.

즉, **읽기 비중이 높고 충돌 빈도가 상대적으로 낮은 환경**에서는 적합하지만, 좌석 예매처럼 같은 자원에 대한 동시 요청이 자주 몰릴 수 있는 상황에서는 재시도 비용이 커질 수 있다.  
특히 영화 예매는 인기 상영 시간대나 인기 좌석으로 요청이 집중될 수 있어, 현재 문제를 해결하는 핵심 방식으로는 적합도가 낮다고 판단했다.

| 항목 | 내용 |
|------|------|
| 장점 | 평상시 성능이 좋고 락 대기가 없음 |
| 단점 | 충돌 시 재시도 로직이 필요하고 실패 비용이 커질 수 있음 |
| 적합한 경우 | 읽기가 많고 충돌 빈도가 낮은 경우 |
| 과제에서의 판단 | 좌석 예매는 충돌 가능성이 높아 주된 해결책으로는 부적합 |

### 3. DB 유니크 제약조건 (Unique Constraint)

DB 차원에서 중복 데이터를 허용하지 않도록 제약조건을 설정하는 방식이다.  
이번 과제에서는 `(schedule_id, seat_row, seat_col)` 조합에 유니크 제약조건을 두어, 동일한 상영 회차의 동일 좌석이 중복 저장되지 않도록 했다.

이 방식은 구현이 비교적 단순하고, 애플리케이션 로직과 무관하게 DB가 최종적으로 데이터 무결성을 보장한다는 장점이 있다.  
다만 애플리케이션 레벨에서 경쟁을 미리 제어하는 것이 아니라, **실제로 insert를 시도한 뒤에야 예외가 발생한다**는 한계가 있다.  
즉, 이미 커넥션과 쿼리 비용이 발생한 이후라는 점에서 사전 제어 방식보다는 비효율적일 수 있다.

그럼에도 불구하고 이번 과제 범위에서는 구현 난이도와 실용성을 모두 고려했을 때 가장 현실적인 방식이라고 판단했다.  
현재 좌석 선점 로직은 이 유니크 제약조건을 기반으로 중복 예약을 방지하고, 충돌 시 `DataIntegrityViolationException`을 비즈니스 예외로 변환해 처리하고 있다.

| 항목 | 내용 |
|------|------|
| 장점 | 구현이 단순하고 DB가 최종 무결성을 강하게 보장함 |
| 단점 | insert 이후에야 충돌을 감지하므로 비용이 더 큼 |
| 적합한 경우 | 중복 생성 방지가 핵심이고, 단일 DB 기반으로 빠르게 안정성을 확보해야 하는 경우 |
| 과제에서의 판단 | **현재 적용한 방식**으로, 좌석 중복 예약 방지를 위한 가장 실용적인 선택 |

### 4. Redis 분산 락

Redis를 이용하면 DB row가 없어도 좌석별 key를 기준으로 락을 걸 수 있다.  
예를 들어 `schedule:{scheduleId}:seat:{seatRow}:{seatCol}` 같은 key를 사용하면, 좌석 단위로 세밀하게 락을 제어할 수 있다.

이 방식의 가장 큰 장점은 **아직 DB에 존재하지 않는 예약 대상에도 락을 걸 수 있다**는 점이다.  
즉, 현재처럼 `reserved_seat`가 예약 시점에 새로 생성되는 구조에서도 좌석 단위 경쟁을 자연스럽게 제어할 수 있다.  
또한 `schedule` 전체가 아니라 좌석 단위로 락을 걸 수 있어, 같은 상영 회차 내에서도 서로 다른 좌석 예약 요청은 병렬로 처리할 수 있다.

다만 Redis 도입, 락 해제 처리, TTL 설정, 장애 상황 대응 등 운영 복잡도가 커진다.  
이번 과제에서는 학습 및 구현 범위를 고려해 도입을 보류했지만, **장기적으로 실제 서비스 수준으로 확장한다면 가장 먼저 고려할 방식**이라고 판단했다.

| 항목 | 내용 |
|------|------|
| 장점 | DB row 없이도 좌석 단위로 세밀한 락 제어 가능 |
| 단점 | Redis 도입과 락 관리 로직 등 운영 복잡도가 증가 |
| 적합한 경우 | 다중 인스턴스 환경이거나, 좌석 단위의 정교한 경쟁 제어가 필요한 경우 |
| 과제에서의 판단 | 현재는 보류했지만, 추후 도입을 고려 중인 방식 |

### 현재 적용한 방식과 판단

이번 과제에서는 **DB 유니크 제약조건을 활용해 동일 상영 회차의 동일 좌석 중복 예약을 방지**하고 있다.  
이 방식은 애플리케이션 단계에서 미리 경쟁을 제어하지는 못하지만, 최소한의 구현으로도 데이터 무결성을 보장할 수 있다는 장점이 있다.

정리하면 현재 판단은 다음과 같다.

- 현재 적용: DB 유니크 제약조건 기반 좌석 중복 예약 방지
- 검토했지만 적용하지 않음: `schedule` 단위 비관적 락
- 추후 도입 고려: Redis 기반 좌석 단위 분산 락

---

## 2. Feign Client / Http Client 장단점 조사

외부 결제 서버(PortOne)와 통신하기 위해 사용할 수 있는 HTTP 클라이언트 방식도 함께 정리했다.  
Spring 환경에서 많이 사용하는 방식은 `Feign Client`, `RestClient`, `WebClient`, 그리고 보다 저수준의 `HttpClient` 계열로 나눌 수 있다.  
각 방식의 특징과 장단점은 다음과 같다.

### 1. Feign Client

Feign Client는 선언형 HTTP 클라이언트로, 인터페이스에 메서드와 어노테이션을 정의하면 구현체를 자동으로 생성해 주는 방식이다.  
코드가 간결하고, 외부 API 명세를 인터페이스 형태로 분리할 수 있어 가독성이 좋다.

반면 세부 요청/응답 제어가 필요할 때는 설정이 많아질 수 있고, 실제 요청이 추상화되어 보여 디버깅이 다소 불편할 수 있다.

| 항목 | 내용 |
|------|------|
| 장점 | 선언형이라 코드가 간결하고 인터페이스 기반으로 관리하기 쉬움 |
| 단점 | 세부 제어와 디버깅이 상대적으로 불편할 수 있음 |
| 적합한 경우 | 외부 API가 많고, 명세가 비교적 안정적이며, 클라이언트 코드를 일관되게 관리하고 싶은 경우 |

### 2. RestClient

`RestClient`는 Spring 6부터 제공되는 동기식 HTTP 클라이언트로, 기존 `RestTemplate`보다 현대적인 API를 제공한다.  
요청 URL, 헤더, 바디, 응답 파싱 과정을 코드에서 명시적으로 확인할 수 있어 흐름을 이해하기 쉽다.

요청과 응답 흐름이 코드에 직접 드러나기 때문에, 예외 처리나 응답 파싱을 세밀하게 다루기 좋다.

| 항목 | 내용 |
|------|------|
| 장점 | 요청/응답 흐름이 명확하고, Spring 환경에서 사용하기 편함 |
| 단점 | 선언형 방식에 비해 코드가 다소 길어질 수 있음 |
| 적합한 경우 | 외부 API 호출 흐름을 직접 제어하고, 예외 처리나 응답 파싱을 세밀하게 다뤄야 하는 경우 |

### 3. WebClient

`WebClient`는 비동기/논블로킹 기반의 HTTP 클라이언트다.  
대량의 외부 요청을 효율적으로 처리하거나, 반응형 프로그래밍이 필요한 환경에서 강점을 가진다.

반면 동기식 MVC 구조에서는 코드 복잡도가 증가할 수 있고, 프로젝트 전반이 반응형 구조가 아닐 경우 장점을 충분히 활용하기 어렵다.

| 항목 | 내용 |
|------|------|
| 장점 | 비동기/논블로킹 처리에 강하고 확장성이 좋음 |
| 단점 | 동기식 MVC 구조에서는 오히려 코드 복잡도가 증가할 수 있음 |
| 적합한 경우 | 대량 외부 호출, 반응형 프로그래밍, 논블로킹 처리가 중요한 경우 |

### 4. HttpClient (저수준 클라이언트)

Java 기본 `HttpClient`나 Apache HttpClient 같은 저수준 HTTP 클라이언트는 요청과 응답을 가장 세밀하게 제어할 수 있다.  
반면 Spring 애플리케이션에서 사용하는 경우, 예외 처리, 직렬화/역직렬화, 공통 설정 등을 직접 더 많이 관리해야 한다.

| 항목 | 내용 |
|------|------|
| 장점 | 세부 제어가 가장 자유롭고 라이브러리 의존이 적을 수 있음 |
| 단점 | 직접 관리해야 할 코드와 설정이 많아짐 |
| 적합한 경우 | 매우 세밀한 제어가 필요하거나, 프레임워크 의존을 최소화해야 하는 경우 |

---

## 3. API 테스트 결과

### 1. 좌석 선점 성공

- `POST /api/reservations`
- 동일 상영 회차에서 선택한 좌석이 정상적으로 선점되는 것을 확인했다.

<img width="886" height="141" alt="Image" src="https://github.com/user-attachments/assets/e40bbcab-609e-4ee3-a87e-3358e5946113" />

### 2. 결제 성공

- `POST /api/reservations/{id}/pay`
- 예매 결제가 정상적으로 완료되고, 결제 상태가 `PAID`로 반영되는 것을 확인했다.

<img width="891" height="310" alt="Image" src="https://github.com/user-attachments/assets/2eb6a589-9c83-4541-bddf-d3cb872a8872" />

### 3. 취소 성공

- `PATCH /api/reservations/{id}/cancel`
- 예약 취소와 함께 결제 취소가 정상적으로 수행되는 것을 확인했다.

<img width="884" height="180" alt="Image" src="https://github.com/user-attachments/assets/5b057b40-ab92-4200-87bb-f5cac289fa9e" />
