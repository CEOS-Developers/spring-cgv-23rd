# spring-cgv-23rd
CEOS 23기 백엔드 스터디 - CGV 클론 코딩 프로젝트

### 1️⃣ DB를 모델링해봐요!
<img src="/images/0_erd.png" alt="ERD 다이어그램">
영화관 ||--o{ 상영관    

상영관 ||--o{ 좌석  

상영관 ||--o{ 상영일정  

영화 ||--o{ 상영일정 

상열일정 ||--o{ 예매내역

좌석 ||--o{ 상영일정  

사용자 ||--o{ 예매내역


### 3️⃣ CGV의 4가지 HTTP Method API 만들어요
<img src="/images/1_postman.png" alt="postman 연결">

### 5️⃣ Global Exception를 만들어봐요
<img src="/images/2_exception.png" alt="없는 아이템 not found로 나오게">

### 6️⃣ Swagger 연동 후 Controller 통합 테스트를 해봐요
<img src="/images/3_swagger.png" alt="swagger">
연동 성공

Post
<img src="/images/3-1_Post.png" alt="Post">

Get 전체
<img src="/images/3-2_get.png" alt="Get 전체">

Get 한 개
<img src="/images/3-3_get_item.png" alt="Get 한 개">

Delete
<img src="/images/3-4_delete.png" alt="Delete">

다시 Get 해보면
<img src="/images/3-5_get.png" alt="Get 다시">

### 7️⃣ Service 계층의 단위 테스트를 진행해요
<img src="/images/4_test.png" alt="테스트 완료">

---

(저번주에 이어서..)

<img src="/images/0-1_new_erd.png" alt="ERD 새 다이어그램">
erd 다시 그림

<img src="/images/5_h2.png" alt="h2">
h2 실행

<img src="/images/5-1_h2_connet.png" alt="h2">
connect 하면 이렇게 뜸

<img src="/images/5-2_h2_show.png" alt="h2">
테이블 명 한 번 더 보고 인서트 준비

<img src="/images/5-3_h2_insert.png" alt="h2">
테스트 데이터 insert

<img src="/images/6-1_post_theater.png" alt="postman">
영화관 post

<img src="/images/6-2_post_movie.png" alt="postman">
영화 post

<img src="/images/6-3_post_item.png" alt="postman">
매점 post


### 1️⃣JWT 인증(Authentication) 방법에 대해서 알아보기

#### 1. JWT 인증 방식이란?

JWT
-> 로그인한 사용자인지 확인하기 위해 사용하는 토큰 방식

사용자가 로그인하면 서버가 토큰을 만들어 주고, 사용자는 이후 요청마다 이 토큰을 함께 보내서 자신을 인증함

##### Access Token

* 실제 API 요청할 때 쓰는 토큰
* 보통 유효기간이 짧다

##### Refresh Token

* Access Token이 만료됐을 때 새 Access Token을 받기 위해 쓰는 토큰
* Access Token보다 보통 더 오래 간다

##### JWT 방식의 장점

* 서버가 로그인 상태를 따로 많이 저장하지 않아도 된다
* 모바일 앱이나 프론트/백 분리 구조에 잘 맞는다

##### JWT 방식의 단점

* 토큰이 탈취되면 위험할 수 있다
* 한 번 발급된 토큰은 바로 무효화하기가 어렵다



#### 2. 세션과 쿠키

##### 쿠키

쿠키
-> 브라우저에 저장되는 작은 데이터

로그인 정보를 유지할 때 많이 사용된다.

##### 세션

세션
-> 서버가 로그인 상태를 저장하는 방식

브라우저는 보통 쿠키에 담긴 세션 ID를 보내고, 서버는 그걸 보고 누가 로그인했는지 확인

##### 장점

* 서버가 로그인 상태를 직접 관리해서 제어하기 쉽다
* 로그아웃 처리나 강제 만료가 비교적 쉽다

##### 단점

* 사용자가 많아지면 서버가 세션을 계속 관리해야 해서 부담이 생긴다



#### 3. OAuth

OAuth
-> 다른 서비스의 계정을 이용해 로그인하거나 권한을 위임하는 방식
ex)
* 카카오 로그인
* 구글 로그인
* 네이버 로그인

즉, 우리 서비스가 사용자의 비밀번호를 직접 받지 않고
다른 서비스의 인증 기능을 빌려 쓰는 것

##### 장점

* 회원가입/로그인이 편하다
* 비밀번호를 우리 서비스가 직접 관리하지 않아도 된다

##### 단점

* 구현이 조금 더 복잡하다
* 외부 서비스 정책에 영향을 받을 수 있다



#### 4. 방식 비교

| 방식      | 설명                                    | 장점                                | 단점                             | 주로 쓰는 곳                  |
| ------- | ------------------------------------- | --------------------------------- | ------------------------------ | ------------------------ |
| JWT     | 토큰을 이용해 사용자를 인증하는 방식                  | 서버 부담이 비교적 적고, 프론트/백 분리 구조에 잘 맞음  | 토큰 탈취 시 위험하고, 바로 무효화하기 어려움     | 모바일 앱, REST API, SPA     |
| 세션 + 쿠키 | 서버가 로그인 상태를 저장하고, 브라우저는 쿠키로 세션 ID를 보냄 | 서버가 로그인 상태를 직접 관리해서 제어하기 쉬움       | 서버가 세션을 계속 관리해야 해서 부담이 생길 수 있음 | 전통적인 웹 서비스               |
| OAuth   | 다른 서비스 계정으로 로그인하거나 권한을 위임하는 방식        | 소셜 로그인이 가능하고, 비밀번호를 직접 관리하지 않아도 됨 | 구현이 더 복잡하고 외부 서비스에 영향을 받을 수 있음 | 카카오 로그인, 구글 로그인, 네이버 로그인 |


#### 5. 정리

* **JWT**: 토큰으로 로그인 상태를 확인하는 방식
* **Access Token**: 실제 요청에 사용하는 토큰
* **Refresh Token**: Access Token을 다시 발급받기 위한 토큰
* **세션**: 서버가 로그인 상태를 저장하는 방식
* **쿠키**: 브라우저에 저장되는 데이터
* **OAuth**: 구글/카카오 같은 외부 계정으로 로그인하는 방식

<img src="/images/7.auth.png" alt="auth">
회원가입

---

### 동시성 해결 방법에 대해 조사하고 적용하기

#### 동시성 해결 방법 조사
1. `synchronized`
    - JVM 내부에서만 동작하는 락 방식
    - 단일 서버에서는 사용할 수 있지만, 서버가 여러 대면 한계가 있다.

2. DB Lock
    - DB row에 락을 걸어 동시에 수정되지 않도록 제어한다.
    - 재고 차감처럼 같은 데이터를 동시에 수정하는 경우에 적합하다.

3. Unique 제약
    - DB에서 중복 저장 자체를 막는 방식이다.
    - 같은 상영 회차의 같은 좌석처럼 절대 중복되면 안 되는 데이터에 적합하다.

4. Redis 분산락
    - 여러 서버 간 동시성 제어에 사용할 수 있다.
    - 다만 Redis 인프라가 필요해 현재 프로젝트 규모에서는 과하다고 판단했다.


#### 문제 상황

CGV 서비스에서는 여러 사용자가 동시에 상품을 주문하거나 좌석을 예매할 수 있다.  
이때 재고가 중복 차감되거나 같은 좌석이 중복 예약되는 문제가 발생할 수 있다.

#### 적용 방법

| 대상 | 적용 방법 | 이유 |
|---|---|---|
| 상품 재고 | 비관적 락 `PESSIMISTIC_WRITE` | 같은 재고에 동시 주문이 들어올 수 있기 때문 |
| 좌석 예매 | `(screening_id, seat_id)` unique 제약 | 같은 상영 회차의 같은 좌석은 한 번만 예약되어야 하기 때문 |

#### 변경 내용

- `TheaterItemStockRepository`에 락 조회 메서드 추가
- `ItemOrderService`에서 재고 조회 시 락 메서드 사용
- `TheaterItemStock`에서 재고 검증과 차감 처리
- `Reservation`에 unique 제약 추가
- `ReservationService`에서 중복 예약 예외 처리
- 동시성 테스트 추가

#### 테스트

| 테스트 | 결과 |
|---|---|
| 재고 1개에 동시 주문 2건 요청 시 1건만 성공 | 성공 |
| 같은 좌석에 동시 예매 2건 요청 시 1건만 성공 | 성공 |

---

### 결제 시스템 연동

| 구분 | 대상 | 특징 |
|---|---|---|
| 티켓팅 | 좌석 예매 `Reservation` | 좌석을 먼저 선점하고, 결제 성공 시 예매 확정 |
| 커머스 | 매점 주문 `ItemOrder` | 결제 성공 후 재고 차감 및 주문 확정 |

티켓팅과 커머스는 결제 흐름이 다르기 때문에 각각 다른 방식으로 처리했다.



#### 결제 연동 방식

외부 결제 서버 연동에는 `OpenFeign`을 사용했다.

| 방식 | 특징 |
|---|---|
| Feign | 인터페이스 기반으로 외부 API를 간단하게 호출할 수 있음 |
| RestClient/WebClient | 세밀한 제어는 가능하지만 요청/응답 코드가 많아짐 |

이번 프로젝트에서는 결제 서버 명세가 정해져 있고, API 호출 구조가 단순하기 때문에 Feign을 선택했다.

적용 파일:

- `PaymentFeignClient`
- `PaymentGateway`
- `PaymentGatewayImpl`
- `PaymentProperties`
- `payment/dto/*`


#### 커머스 결제 흐름

매점 주문은 결제가 성공한 뒤 재고를 차감하도록 구성했다.

```text
주문 요청
→ 주문 생성(PENDING_PAYMENT)
→ 결제 서버 즉시 결제 요청
→ 결제 성공 시 재고 차감
→ 주문 상태 PAID 변경
```

#### Feign Client / Http Client 장단점

| 방식 | 장점 | 단점 |
|---|---|---|
| Feign Client | 선언형 인터페이스 방식이라 코드가 간결하고 API 명세를 매핑하기 쉽다. | 리트라이, 커넥션 제어 등 세밀한 설정은 추가 구성이 필요하다. |
| RestClient / WebClient | 요청 흐름을 직접 제어하기 쉽고, 세밀한 커스터마이징에 유리하다. | 요청/응답 처리와 에러 파싱 코드를 직접 작성해야 해서 구현량이 많아진다. |

이번 과제 적합:
- Feign Client: 명세 기반 연동이 빠르다.
- RestClient / WebClient: 학습에는 좋지만 구현량이 많다.