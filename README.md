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

