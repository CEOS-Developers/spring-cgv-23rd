# spring-cgv-23rd

CEOS 23기 백엔드 스터디 - CGV 클론 코딩 프로젝트

## 코드 리팩토링 정리

이번 리팩토링에서는 "서비스는 흐름을 조율하고, 도메인은 자신의 상태를 책임진다"는 기준으로 코드를 정리했습니다.

### 1. 예약 로직 책임 분리

- `ReservationService`의 예매 생성 로직에서 조회, 검증, 좌석 생성 단계를 메서드로 분리했습니다.
- 예매 목록 조회는 전체 조회 후 메모리에서 필터링하던 방식 대신 `findAllByUserId` repository query를 사용하도록 변경했습니다.
- `Reservation.cancel()`이 이미 취소된 예매인지 직접 검사하도록 바꿔 상태 변경 책임을 엔티티에 더 가깝게 옮겼습니다.

### 2. 매점 구매 로직 단순화

- `StorePurchaseService`에서 중복되던 조회 로직을 전용 메서드로 분리해 구매 흐름이 한눈에 보이도록 정리했습니다.
- `CinemaMenuStock.decreaseStock()`이 수량 검증과 재고 부족 검증을 함께 책임지도록 바꿔, 재고 관련 규칙이 한곳에 모이도록 개선했습니다.
- 매점 구매 실패 상황을 더 잘 드러내기 위해 `STORE_MENU_STOCK_NOT_FOUND`, `INSUFFICIENT_MENU_STOCK` 에러 코드를 추가했습니다.

### 3. 테스트 가능성 개선

- 변경된 예매 로직에 맞춰 `ReservationServiceTest`를 정리했습니다.
- `StorePurchaseServiceTest`를 추가해 재고 차감과 예외 흐름을 검증할 수 있게 했습니다.
- 테스트 전용 H2 설정을 추가해 로컬 MySQL 환경 없이도 `./gradlew test`가 실행되도록 개선했습니다.

<details>
<summary><h2>CGV 클론 코딩 : 찜, 구매</h2></summary>

## 1. 영화관 / 영화 찜 기능

### 핵심 구현
- User - Cinema / Movie 간 **N:M 관계를 별도 엔티티로 분리**
- (`CinemaLike`, `MovieLike`)

### 구현 방식
단순 boolean 필드가 아니라 **엔티티로 분리해서 확장 가능하게 설계**

- 찜 요청 시:
  - 이미 존재 → 삭제 (취소)
  - 없으면 → 생성 (찜)


---

## 2. 매점 구매 기능

### 핵심 구현
- `StoreMenu` + `StoreOrder` 구조로 설계
- 구매 시 재고 차감 + 주문 생성

### 구현 흐름
1. 메뉴 조회
2. 재고 확인
3. 재고 차감
4. 주문 생성

```java
if (menu.getStock() < quantity) {
    throw new IllegalArgumentException("재고 부족");
}
menu.decreaseStock(quantity);
storeOrderRepository.save(order);
```

</details>

<details>
<summary><h2> 1️⃣ JWT 인증(Authentication) 방법에 대해서 알아보기 </h2></summary>

# 1. JWT 인증(Authentication) 방법 정리

## 1. 인증(Authentication)과 인가(Authorization)

- **인증(Authentication)**: 사용자가 누구인지 확인하는 과정
  - 로그인을 통해 사용자가 실제 회원인지 확인하는 것
- **인가(Authorization)**: 인증된 사용자가 어떤 권한을 가지는지 확인하는 과정
  - 로그인한 사용자가 관리자 페이지에 접근할 수 있는지 판단하는 것

---

## 2. JWT란?

**JWT(Json Web Token)**

- 사용자 인증에 필요한 정보를 JSON 형태로 담아 만든 토큰
- 서버는 로그인 성공 후 JWT를 발급 -> 라이언트는 이후 요청마다 이 토큰을 함께 보내 인증을 수행
- JWT 구조
  - `Header.Payload.Signature`

### 2.1 Header

토큰 타입과 서명 알고리즘 정보가 들어감

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### 2.2 Payload

- 실제 사용자 정보나 토큰 속성이 들어감
- 이 안의 정보 조각을 Claim이라고 부름
- 대표적인 **Claim** 예시

  - `sub`: 사용자 식별자
  - `iss`: 토큰 발급자
  - `aud`: 토큰 대상자
  - `iat`: 발급 시간
  - `exp`: 만료 시간
  - `role`: 사용자 권한

```json
{
  "sub": "12345",
  "role": "USER",
  "iat": 1710000000,
  "exp": 1710003600
}
```

### 2.3 Signature

- Header와 Payload가 위조되지 않았는지 검증하기 위한 값
- 서버만 알고 있는 비밀키(secret key) 또는 개인키(private key)를 이용해 생성

---

## 3. JWT 인증 동작 과정

### 3.1 로그인 시

1. 사용자가 아이디와 비밀번호로 로그인 요청을 보낸다.
2. 서버가 회원 정보를 확인한다.
3. 로그인에 성공하면 서버가 JWT를 발급한다.
4. 클라이언트는 이 토큰을 저장한다.

### 3.2 이후 요청 시

1. 클라이언트는 API 요청 시 Authorization 헤더에 토큰을 담아 보낸다.
2. 서버는 토큰의 Signature와 만료 시간 등을 검증한다.
3. 유효한 토큰이면 인증된 사용자로 처리한다.
   예시 헤더:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...
```

---

## 4. Access Token과 Refresh Token

JWT 인증에서는 보통 **Access Token**과 **Refresh Token**을 함께 사용한다.

### 4.1 Access Token

- 실제 API 요청 시 사용하는 토큰
- 보통 유효기간을 짧게 둠
  - 이유 : 탈취 당했을 때 피해 시간을 줄이기 위함

### 4.2 Refresh Token

- Access Token이 만료되었을 때 새로운 Access Token을 발급받기 위한 토큰
- Access Token보다 훨씬 오래 유지됨

### 4.3 동작 과정

1. 사용자가 로그인한다.
2. 서버는 Access Token과 Refresh Token을 함께 발급한다.
3. 클라이언트는 Access Token으로 API 요청을 보낸다.
4. Access Token이 만료되면 Refresh Token을 이용해 재발급 요청을 보낸다.
5. 서버는 Refresh Token을 검증한 뒤 새로운 Access Token을 발급한다.

### 4.4 왜 두 개를 같이 쓸까?

- Access Token만 사용하면 토큰이 탈취되었을 때 만료 전까지 계속 악용될 수 있음
- **반대로** Access Token 유효기간을 너무 짧게 하면 사용자가 자주 로그인해야 해서 불편해짐

**따라서**

- **Access Token은 짧게, Refresh Token은 길게** 설정해서 보안성과 편의성을 함께 잡음

---

## 5. JWT 방식의 장점과 단점

### 5.1 장점

1. 서버가 세션 저장소를 크게 의존하지 않아도 됨
2. 모바일 앱과 웹에서 공통적으로 사용하기 좋음
3. 마이크로서비스 구조에 비교적 잘 어울림

### 5.2 단점

1. 토큰 자체가 탈취되면 만료 전까지 악용될 수 있음 --> HTTPS, 짧은 만료 시간, Refresh Token 관리가 중요함
2. Payload는 암호화가 아니라 인코딩이므로 민감한 개인정보를 직접 넣으면 안 됨
3. 로그아웃이나 강제 만료 처리가 세션 방식보다 복잡 --> 필요하면 Refresh Token DB 저장, 블랙리스트, 토큰 버전 관리 같은 추가 전략이 필요

---

## 6. 세션(Session)과 쿠키(Cookie) 방식

### 6.1 쿠키

- 브라우저에 저장되는 작은 데이터
- 서버는 응답 시 Set-Cookie 헤더를 통해 쿠키를 보내고 브라우저는 이후 요청마다 쿠키를 자동으로 함께 보냄

### 6.2 세션

- 서버 쪽에 사용자 상태를 저장하는 방식
- 서버는 로그인 성공 시 세션 저장소에 사용자 정보를 저장하고 브라우저에는 세션 ID만 쿠키로 내려보냄

**즉**

- 실제 로그인 상태 정보는 서버가 보관
- 브라우저는 세션 ID만 보관

### 6.3 세션/쿠키 인증 흐름

1. 사용자가 로그인한다.
2. 서버가 사용자 확인 후 세션 저장소에 로그인 정보를 저장한다.
3. 서버는 세션 ID를 쿠키에 담아 응답한다.
4. 브라우저는 이후 요청마다 해당 쿠키를 자동 전송한다.
5. 서버는 세션 ID를 확인하여 사용자를 식별한다.

### 6.4 장점

- 세션에 실제 사용자 상태를 서버가 보관하므로 로그아웃이나 강제 만료 처리가 비교적 쉬움
- 민감한 정보를 브라우저에 직접 저장하지 않아 JWT Payload에 정보를 넣는 것보다 통제가 쉬운 편임

### 6.5 단점

- 사용자가 많아질수록 세션 저장소 관리 비용이 증가
- 서버를 여러 대 운영할 경우 세션 공유(redis 등) 문제가 생길 수 있음

---

## 7. 쿠키 기반 인증과 JWT 기반 인증의 차이

### 7.1 공통점

클라이언트와 서버 사이에서 **로그인한 사용자임을 증명**하기 위한 방식

### 7.2 차이점

- 세션/쿠키 방식 : 서버가 상태를 저장하는 stateful 방식
- JWT 방식 : 토큰 기반으로 인증 정보를 처리하는 stateless 방식

**즉**

- **세션 방식**
  - 서버가 로그인 상태를 저장
  - 클라이언트는 session id만 전달
  - 로그아웃/강제 만료가 쉬움
  - 서버 확장 시 세션 공유 문제가 생길 수 있음
- **JWT 방식**
  - 서버가 토큰 검증 중심으로 처리
  - 클라이언트가 토큰 자체를 보관
  - 확장성에 유리
  - 탈취 시 대응 전략을 별도로 설계해야 함

---

## 8. OAuth 2.0이란 무엇인가

**OAuth 2.0**

- 로그인 방식이라기보다 **외부 서비스의 자원에 접근할 수 있도록 권한을 위임하는 프로토콜**
- 예시
  - `구글 로그인, 카카오 로그인, 네이버 로그인`
  - 단순히 소셜 로그인 버튼처럼 보이지만 내부적으로는 OAuth 2.0 기반 권한 위임이 사용됨

### 8.1 왜 필요한가

**비밀번호를 공유하지 않고도 제한된 권한만 위임**할 수 있게 만든 방식

### 8.2 주요 구성 요소

- `Resource Owner`: 사용자
- `Client`: 우리가 만든 서비스
- `Authorization Server`: 인증과 토큰 발급을 담당하는 서버
- `Resource Server`: 실제 사용자 정보를 가진 서버

### 8.3 기본 흐름

1. 사용자가 `구글 로그인` 버튼을 누른다.
2. 클라이언트는 사용자를 구글 인증 페이지로 보낸다.
3. 사용자가 로그인 및 동의를 진행한다.
4. 인증 서버가 authorization code를 발급한다.
5. 클라이언트가 이 코드를 이용해 access token을 발급받는다.
6. access token으로 사용자 정보를 요청한다.
7. 서비스는 이 사용자 정보를 기반으로 로그인 처리를 한다.

### 8.4 OAuth와 JWT의 차이

- `OAuth 2.0`: 권한 위임을 위한 프로토콜
- `JWT`: 토큰의 형식 또는 표현 방식

즉,
**OAuth 2.0에서 발급되는 access token이 JWT일 수도 있고 아닐 수도 있음**

---

## 9. JWT, 세션, 쿠키, OAuth 비교


| 구분      | 핵심 개념               | 상태 저장 위치          | 장점                         | 단점                  |
| --------- | ----------------------- | ----------------------- | ---------------------------- | --------------------- |
| 쿠키      | 브라우저에 데이터 저장  | 클라이언트              | 구현이 단순함                | 민감 정보 저장에 취약 |
| 세션      | 서버가 로그인 상태 저장 | 서버                    | 로그아웃/만료 제어 쉬움      | 서버 확장 시 부담     |
| JWT       | 토큰으로 인증 정보 전달 | 클라이언트 중심         | 확장성 좋고 API 환경에 적합  | 탈취 대응이 복잡      |
| OAuth 2.0 | 외부 서비스 권한 위임   | 서비스 구조에 따라 다름 | 소셜 로그인/권한 위임에 적합 | 개념과 흐름이 복잡    |

---

## 10. 실무에서 자주 쓰이는 방식

### 10.1 전통적인 웹 서비스

- 세션 + 쿠키

### 10.2 SPA / 모바일 앱 / REST API

- JWT Access Token + Refresh Token

### 10.3 소셜 로그인

- OAuth 2.0 + 자체 세션 또는 JWT

---

## 11. Spring Security에서의 JWT 관점

**Spring Security**

- JWT 기반 인증을 구현할 때 보통 세션을 사용하지 않는 `stateless` 정책을 설정
- 즉,
  **서버가 세션에 로그인 상태를 저장하지 않고 매 요청마다 전달된 JWT를 검증해서 인증을 처리하는 방식**
- 대표적으로 아래와 같은 설정 사용 : JWT 기반 API 서버에서 자주 사용

```java
.sessionManagement(session -> 
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

---

## 12. 총 정리

### 1. 인증(Authentication)과 인가(Authorization)

- **인증(Authentication)**: 사용자가 누구인지 확인하는 과정
- **인가(Authorization)**: 인증된 사용자가 어떤 권한을 가지는지 확인하는 과정

---

### 2. 로그인 인증 방식 4가지

- **쿠키(Cookie)**: 브라우저에 데이터를 저장하는 방식
- **세션(Session)**: 서버가 로그인 상태를 저장하는 방식
- **JWT(JSON Web Token)**: 토큰 기반 인증 방식
- **OAuth 2.0**: 외부 서비스 로그인과 권한 위임 방식

---

### 3. 세션 방식과 JWT 방식의 핵심 차이

#### 세션 방식

- 서버가 로그인 상태를 저장
- 클라이언트는 세션 ID만 전달
- 로그아웃, 강제 만료 처리가 쉬움
- 서버 부담이 커질 수 있음

#### JWT 방식

- 서버는 토큰 검증만 수행
- 클라이언트가 토큰 자체를 보관
- 확장성이 좋고 API 환경에 적합
- 토큰 탈취 시 위험할 수 있음

#### 한 줄 정리

- **세션 = 서버 중심 인증**
- **JWT = 토큰 중심 인증**

---

### 4. JWT 구조

```text
Header.Payload.Signature
```

- `Header` : 토큰 타입, 서명 알고리즘
- `Payload` : 사용자 정보, 토큰 정보(Claim)
- `Signature`: 위조 여부 검증

---

### 5. JWT 동작 흐름

1. 사용자가 로그인한다.
2. 서버가 JWT를 발급한다.
3. 클라이언트가 토큰을 저장한다.
4. 이후 요청마다 토큰을 함께 보낸다.
5. 서버가 토큰을 검증하고 인증 처리한다.

---

### 6. Access Token과 Refresh Token

**Access Token**

- 실제 API 요청에 사용하는 토큰
- 유효기간이 짧음

**Refresh Token**

- Access Token이 만료되었을 때 재발급에 사용하는 토큰
- 유효기간이 김

**왜 둘 다 사용할까?**

- Access Token만 쓰면 탈취 시 위험
- 너무 짧게 하면 사용자가 자주 로그인해야 함
- Access Token은 짧게, Refresh Token은 길게 설정함

---

### 7. JWT 방식의 장점과 단점

**장점**

- 서버가 세션 저장소를 크게 의존하지 않음
- 확장성이 좋음
- 모바일 앱, SPA, REST API에 적합

**단점**

- 토큰 탈취 시 만료 전까지 악용 가능
- 로그아웃 처리와 강제 만료가 비교적 복잡
- Refresh Token 관리가 중요함

---

### 8. OAuth 2.0 핵심

- 로그인 방식 자체라기보다 권한 위임 프로토콜
- 예: 구글 로그인, 카카오 로그인, 네이버 로그인
- 핵심 의미
  - 비밀번호를 직접 공유하지 않고도 로그인 가능
  - 외부 서비스의 인증 기능을 활용할 수 있음

---

### 9. 실무에서 자주 쓰는 방식

- **전통적인 웹 서비스**: 세션 + 쿠키
- **모바일 앱 / SPA / REST API**: JWT + Refresh Token
- **소셜 로그인**: OAuth 2.0 + 자체 세션 또는 JWT

</details>

<details>
<summary><h2> 2️⃣ 액세스 토큰 발급 및 검증 로직 구현 </h2></summary>

### 1. Access Token 발급
- 로그인 성공 시 `TokenProvider`에서 토큰 생성
- userId는 subject에, 권한은 claim에 저장

```java
public String createAccessToken(Long id, Authentication authentication) {
    return Jwts.builder()
        .setSubject(String.valueOf(id))
        .claim("auth", authorities)
        .setExpiration(...)
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
}
```
---

### 2. 토큰 검증 및 인증 처리
- 요청마다 `JwtAuthenticationFilter`에서 토큰 검증
- 유효한 경우 `SecurityContext`에 인증 정보 저장
```java
String token = tokenProvider.getAccessToken(request);

if (token != null && tokenProvider.validateAccessToken(token)) {
    Authentication authentication = tokenProvider.getAuthentication(token);
    SecurityContextHolder.getContext().setAuthentication(authentication);
}
```

---

### 3. 핵심 흐름

**로그인 → 토큰 발급 → 요청 시 토큰 검증 → 사용자 인증 처리**

- 세션 없이 토큰 기반으로 사용자 인증을 처리하는 구조로 구현

</details>

<details>
<summary><h2> 3️⃣ 회원가입 및 로그인 API 구현하고 테스트하기 </h2></summary>

### 1. 구현 내용
- 회원가입 API 구현을 위해 `SignupRequest` DTO를 추가
- `AuthController`와 `AuthService`를 작성
- `SecurityConfig`에 `PasswordEncoder`를 등록해 회원 비밀번호를 암호화하여 저장하도록 구성
- 로그인 기능을 위해 `LoginRequest`, `LoginResponse` DTO를 추가
- `AuthController`에 로그인 API를 구현
- `AuthService`에서는 이메일로 사용자를 조회하고 비밀번호를 검증
- 앞서 구현한 `TokenProvider`를 이용해 Access Token을 발급하도록 연결
- 회원가입 후 로그인 테스트를 진행
- DB 저장과 토큰 발급이 정상 동작하는 것 확인

---

### 2. 결과

![로그인](images/W3/3_login.png)

</details>

<details>
<summary><h2> 4️⃣ 토큰이 필요한 API 1개 이상 구현하고 테스트하기 </h2></summary>

### 1. 구현 내용
- `@AuthenticationPrincipal`을 사용해 로그인한 사용자 정보를 컨트롤러에서 바로 받아오도록 구현
- 이를 활용해 토큰이 필요한 API로 영화관 찜, 영화 찜, 매점 구매 기능을 구현
- 각 API에서는 `CustomUserDetails`에서 사용자 id를 꺼내 서비스 레이어로 전달하도록 구성
- `SecurityConfig`에서 `/api/cinemas/*/likes`, `/api/movies/*/likes`, `/api/store/purchases` 경로를 인증이 필요한 요청으로 설정
- 토큰이 없는 요청은 보호된 API에 접근할 수 없도록 처리

---

### 2. 결과

**로그인**
![로그인](images/W3/4_login.png)

**찜 요청 보내기**
![찜 요청 보내기](images/W3/4_likes.png)

**찜 요청 보내기 취소**
![찜 요청 보내기 취소](images/W3/4_likes_delete.png)

**영화 요청 보내기**
![영화 요청 보내기](images/W3/4_movie_likes.png)

**영화 요청 보내기 취소**
![영화 요청 보내기 취소](images/W3/4_movie_likes_delete.png)

**매점 구매 전 상황**
![매점 구매 전 상황](images/W3/4_before_store_1.png)
![매점 구매 전 상황](images/W3/4_before_store_2.png)

**매점 구매**
![매점 구매](images/W3/4_purchase.png)

**매점 구매 후 상황**
![매점 구매 후 상황](images/W3/4_after_store_1.png)
![매점 구매 후 상황](images/W3/4_after_store_2.png)

</details>

<details>
<summary><h2> 5️⃣ (도전 과제) Refresh Token 발급 로직 구현 </h2></summary>

- **기존 로그인 로직**
	- access token만 발급하도록 구현
- **도전 과제**
	- refresh token 발급 및 재발급

- **access token**
	- 유효 시간이 짧기 때문에 만료되면 다시 로그인이 필요하다는 불편함
  - 인증이 필요한 API 요청에 사용
- **refresh token**
	- 사용자는 매번 다시 로그인하지 않고도 새로운 access token을 발급받을 수 있게 됨
  - access token이 만료되었을 때 새로운 access token 재발급을 위한 용도로 사용

---

### 구현 방식

- 로그인 성공 시 `access token`과 `refresh token`을 함께 발급
- refresh token은 DB에 저장하여 사용자별로 관리
- access token 만료 시 클라이언트가 refresh token을 서버에 전달
- 서버는 refresh token의 유효성, 만료 여부, DB 저장값 일치 여부를 검증
- 검증이 완료되면 새로운 access token을 재발급
- 보안 강화를 위해 refresh token도 함께 새로 발급하는 방식으로 확장 가능
- 로그아웃 시 DB에 저장된 refresh token을 삭제하여 더 이상 재사용할 수 없도록 처리

---

### 핵심 코드

#### 1. Refresh Token 생성 (TokenProvider)

```java
public String createRefreshToken(Long id) {
    long now = (new Date()).getTime();
    Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

    return Jwts.builder()
            .setSubject(String.valueOf(id))
            .claim("type", "refresh")
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
}
```

#### 2. 로그인 시 Access + Refresh Token 발급
```java
String accessToken = tokenProvider.createAccessToken(user.getId(), authentication);
String refreshToken = tokenProvider.createRefreshToken(user.getId());

saveOrUpdateRefreshToken(user.getId(), refreshToken);

return new LoginResponse(accessToken, refreshToken);
```

#### 3. Refresh Token 검증 및 재발급
```java
if (!tokenProvider.validateRefreshToken(refreshToken)) {
    throw new BadRequestException(ErrorCode.BAD_REQUEST);
}

RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
        .orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST));

if (!savedToken.getToken().equals(refreshToken) || savedToken.isExpired()) {
    throw new BadRequestException(ErrorCode.BAD_REQUEST);
}

String newAccessToken = tokenProvider.createAccessToken(user.getId(), authentication);
String newRefreshToken = tokenProvider.createRefreshToken(user.getId());
```

---

### Refresh Token은 어떻게 사용할 수 있을까?
- `refresh token` : 평소 API를 호출할 때 사용하는 토큰이 아님
- 실제 요청에서는 access token을 `Authorization` 헤더에 담아 사용
- access token이 만료되었을 때만 refresh token을 이용해 새로운 access token을 발급 받음

**전체 흐름**

1. 사용자가 로그인한다.
2. 서버가 access token과 refresh token을 함께 발급한다.
3. 클라이언트는 access token으로 인증이 필요한 API를 호출한다.
4. access token이 만료되면, refresh token을 `/api/auth/refresh` 같은 재발급 API에 전달한다.
5. 서버가 refresh token을 검증한 뒤 새로운 access token을 발급한다.
6. 클라이언트는 새 access token으로 다시 요청을 보낸다.

---

### 장점
- access token의 수명을 짧게 유지할 수 있어 보안상 유리
- 사용자는 access token이 만료되더라도 다시 로그인하지 않고 인증을 이어갈 수 있음
- refresh token을 DB에 저장해두면 로그아웃 처리, 토큰 무효화, 중복 로그인 관리 같은 기능으로도 확장 가능

---

### 결과 화면

#### 로그인 결과

![회원가입](images/W3/5_signup.png)

![로그인](images/W3/5_login.png)

![refresh token](images/W3/5_refresh.png)

![로그아웃](images/W3/5_logout.png)

![만료](images/W3/5_wrong_refresh.png)

</details>
