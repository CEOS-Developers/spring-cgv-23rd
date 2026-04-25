# spring-cgv-23rd
## CEOS 23기 백엔드 스터디 - CGV 클론 코딩 프로젝트


## 1. 서비스 개요
   본 프로젝트는 다음과 같은 핵심 기능을 지원합니다.

* **영화 및 상영 일정**: 영화 정보 관리 및 영화관별 상영 스케줄 제공.

* **예매 시스템**: 사용자별 좌석 선택 및 예매 상태 관리.

* **관심 서비스**: 사용자 중심의 영화 및 영화관 '찜' 기능.

* **스토어(매점)**: 영화관별 상품 재고 관리 및 주문·결제 시스템.

---

## 2. ERD

<img width="1096" height="608" alt="Image" src="https://github.com/user-attachments/assets/db4f9d4a-df9a-431e-852a-0e18b2c5885a" />

## 3. 상세 모델링 설명
   데이터 모델은 크게 영화 예매 도메인, 사용자 활동 도메인, 스토어 도메인의 세 파트로 구분됩니다.


🎥 영화 및 예매 

영화 관람의 핵심 프로세스를 담당하는 영역입니다.

* movies & schedules: 하나의 영화(movies)는 여러 상영 일정(schedules)을 가질 수 있으며, 각 일정은 특정 상영관(screens)과 매핑됩니다.

* screens & seats: 상영관은 고유의 좌석 구조를 가집니다. seats 테이블은 상영관 종류에 따른 좌석 배치 정보를 담습니다.

* reservations & reservation_seats: 사용자가 예매를 진행하면 reservations 레코드가 생성되며, 1:N 관계인 reservation_seats를 통해 한 명의 사용자가 여러 좌석을 한 번에 예매할 수 있도록 설계되었습니다.

---
👤 사용자 및 소셜

사용자의 정보와 개인화된 취향을 관리합니다.

* users: 기본 회원 정보를 관리합니다.

* movie_likes & cinema_likes: 사용자가 선호하는 영화와 영화관을 저장합니다.

---
🍿 스토어 및 재고

영화관 내 매점 상품 판매와 재고를 관리합니다.

* cinemas & stocks: 상품(products)은 전 지점 공통이지만, 재고(stocks)는 각 영화관(cinemas) 지점별로 독립적으로 관리됩니다.

* orders & order_items: 사용자가 상품을 구매하면 주문서(orders)가 생성됩니다. 하나의 주문에는 여러 종류의 상품이 포함될 수 있으므로 order_items 테이블을 통해 상세 내역을 관리합니다.

---

## 인증(Authentication) 방법



## 1. 세션 & 쿠키

- 서버가 '누가 로그인했는지'에 대한 상태를 직접 들고 있는 Stateful 구조
- 특징: 사용자가 로그인하면 서버는 메모리나 DB에 세션 정보를 저장하고, 사용자에게는 이 정보에 접근할 수 있는 열쇠인 세션 ID를 쿠키에 담아 보냄
- 실제 정보는 서버에 존재

### 과정

1. 사용자의 로그인
2. 서버에서 사용자 정보를 확인 후 세션 ID를 생성하고 세션 저장소에 기록
3. 생성한 세션 ID를 쿠키에 담아 클라이언트에게 전달
4. 클라이언트는 이후 요청마다 헤더에 쿠키를 담아 서버에게 전달
5. 서버는 전달받은 쿠키를 세션 저장소에서 대조하며 사용자 식별
6. 대조(인증)가 완료되면 서버는 사용자에게 데이터 반환

### 장점

- 보안성: 실제 정보를 서버에서 관리하므로 상대적으로 안전(쿠키는 출입증 역할 수행)
- 통제권: 서버에서 강제로 특정 세션을 만료시키기 용이

### 단점

- 서버 부하: 서버에서 세션 저장소를 사용하기 때문에 접속자가 많아질수록 서버 메모리나 DB 점유율이 높아짐
- 확장성 제약: 서버를 여러 대 운영할 경우 세션 정보 공유를 위한 별도 설정 필요



## 2. JWT(**Access Token**) 기반 인증

- JWT: 사용자를 인증하고 식별하기 위한 정보들을 암호화시킨 토큰
- Stateless 방식
- 특징: 서버가 상태를 저장하는 대신, 인증 정보를 암호화된 문자열(Token)로 만들어 클라이언트에게 전달 → 클라이언트는 이후 요청마다 이 토큰을 HTTP 헤더에 담아 보냄
- 서버는 토큰이 유효한지만 검증(별도의 저장소를 조회할 필요 없음)

### 구조

- Header
    - 토큰 타입(ex. JWT) + 알고리즘(ex. SHA256)
- Payload
    - 토큰에 담을 정보: 사용자 정보, 발급/만료 일시, 권한 정보 등 포함
- Signature
    - Payload가 위조/변조되지 않음을 증명하는 문자열(Header + Payload + Secret Key 기반)
- Header, Payload는 인코딩될 뿐, 따로 암호화되지 않음

### 과정

1. 사용자의 로그인
2. 서버에서 사용자 확인 후 JWT 생성
3. SECRET KEY를 통해 암호화된 Access Token을 HTTP 응답 헤더에 실어 사용자에게 전송
4. 사용자는 Access Token을 저장하고 인증이 필요한 요청을 보낼 때 토큰을 HTTP 헤더에 실어 전송
5. 서버는 사용자가 전송한 토큰의 Signature를 SECRET KEY로 복호화한 후 검증이 완료되면 데이터 반환

### 장점

- 확장성: 서버가 상태를 보관하지 않으므로 인증이 간편

### 단점

- 보안 취약점: 토큰이 탈취됨녀 유효 기간이 만료될 때까지 서버가 이를 제어하기 어려움
- 데이터 크기: 토큰에 담긴 정보가 많아지면 네트워크 부하가 커짐



## **3. Access Token + Refresh Token을 이용한 인증**

- Access Token: 실제 서비스 자원에 접근하기 위한 인증
    - 유효기간이 짧음
    - 저장위치: 클라이언트 메모리 or 쿠키
- Refresh Token: Access Token이 만료되었을 때 재발급
    - 유효기간이 상대적으로 김
    - 저장위치: 보안이 강화된 저장소(DB, Redis, HTTP-only 쿠키)

### 과정

1. 사용자의 로그인
2. 서버는 Access Token과 Refresh Token 발급 → 클라이언트에게 전송
3. 사용자는 Refresh Token은 안전한 저장소에 저장 후, Access Token을 HTTP 요청 헤더에 실어 요청 전송
4. Access Token이 만료되면 서버는 `401 Unauthorized` 에러 전송
5. 클라이언트는 보관하던 Refresh Token을 서버에 전송(토큰 재발급)
6. 서버는 DB에 저장된 값과 대조하여 Refresh Token이 유효하면 새로운 Access Token ****발급
7. Refresh Token도 만료되면 재로그인 필요

### 장점

- 보안성 향상: Access Token의 수명을 아주 짧게 설정 → 보안 취약 시간이 짧음
- **사용자 편의성:** Refresh Token이 살아있는 동안에는 사용자가 다시 로그인 창을 볼 필요 없음(자동 연장)

### 단점

- Refresh Token을 검증하기 위해 서버가 별도의 저장소(DB나 Redis) 조회 필요
- 복잡한 구현



## 4. OAuth(소셜 로그인) 인증

- OAuth: 외부 서비스에서도 인증을 가능하게 하고 해당 서비스의 API를 이용하게 해주는 프로토콜

### 참여자

- **Resource Owner** : 실제 사용자
- **Client** : 웹 어플리케이션
- **Authorization Server** : 권한 관리 및 Access Token, Refresh Token을 발급해주는 서버(구글, 카카오)
- **Resource Server** : 실제 데이터가 있는 서버

### 과정

1. 사용자가 Client를 통한 로그인 요청 → 로그인 페이지로 이동 후 인증 완료
2. 인증 성공 시 인증 서버는 Client에게 토큰을 주는 대신 Authorization Code를 URL을 통해 전달
3. Client는 받은 Code를 인증 서버에 보내고, 제 자원에 접근할 수 있는 Access Token과 Refresh Token을 발급 받음
4. Client는 발급받은 Access Token을 사용해 Resource Server에 데이터를 요청하고, 유효성이 확인되면 필요한 사용자 정보를 가져옴
5. Access Token 토큰 만료 시 Refresh Token으로 갱신

### 장점

- 사용자 경험: 복잡한 가입 절차 생략
- 보안 신뢰: 검증된 기업의 보안 시스템을 활용

### 단점

- 의존성: 인증 서비스에 장애가 발생하면 서비스 로그인 불가능
- 복잡한 구현

# 동시성 문제

- 동시성 문제: 여러 프로세스나 스레드가 동일한 공유 자원(메모리, DB 데이터, 파일 등)에 동시에 접근하여 조작할 때, 접근 순서나 타이밍에 따라 예상치 못한 결과가 발생하는 현상

  → 공유 자원이 있는 곳이라면 어디에서든 생길 수 있음

- 동시성 관련 발생 문제
    - **Race Condition(경쟁 상태)**
        - 두 개 이상의 프로세스가 공통 자원을 병행적으로 읽거나 쓸 때, 마지막에 작업을 수행한 프로세스의 결과가 최종 상태를 결정짓는 상황
    - **Deadlock(교착 상태)**
        - 두 개 이상의 프로세스가 각자 가지고 있는 자원을 무한히 대기하며, 서로의 자원을 점유하기 위해 기다리는 상태
    - **Starvation(기아 상태)**
        - 특정 프로세스의 우선순위가 낮아 필요한 자원을 계속해서 할당받지 못하고 무한정 기다리는 상태
- OS 레벨에서의 해결책
    - 공유 자원의 독점을 보장하기 위해 **Critical Section(임계 구역)**을 보호하는 메커니즘을 사용
        - Mutex (뮤텍스)
            - 한 번에 하나의 스레드만 자원을 점유할 수 있게 하는 **Locking** 메커니즘
        - Semaphore (세마포어)
            - 공유 자원에 접근할 수 있는 스레드의 수를 제어하는 **Signaling** 메커니즘
        - Monitor(모니터)
            - Mutex와 Condition Variable을 결합한 고수준 동기화 도구
- Spring에서 동시성 문제 해결
    - synchronized(Java 내부 구현)
        - 방법: 메서드나 블록 단위로 사용해 한 번에 하나의 스레드만 진입하도록 보장
        - 한계: 인스턴스 단위로 Lock이 걸리기 때문에, 서버가 2대 이상인 분산 환경에서는 동시성을 보장하지 못함

        ```java
        public class StockService {
            private Long stockCount = 100L;
        
            // synchronized를 통해 한 번에 하나의 스레드만 접근 허용
            public synchronized void decrease(Long quantity) {
                if (stockCount >= quantity) {
                    stockCount -= quantity;
                }
            }
        }
        ```

    - DB Lock
        - **비관적 락(Pessimistic Lock)**
            - 데이터를 읽을 때부터 물리적인 Lock을 걸어 다른 트랜잭션의 접근을 차단
            - **장점:** 데이터 충돌이 잦을 때 정합성을 확실히 보장
            - **단점:** 성능 저하 및 데드락 위험

            ```java
            public interface StockRepository extends JpaRepository<Stock, Long> {
                @Lock(LockModeType.PESSIMISTIC_WRITE)
                @Query("select s from Stock s where s.id = :id")
                Stock findByIdWithPessimisticLock(Long id);
            }
            ```

        - **낙관적 락(Optimistic Lock)**
            - Lock을 걸지 않고, 수정 시점에 내가 읽은 버전이 맞는지 확인
            - **장점:** 물리적 Lock이 없어 성능상 이점
            - **단점:** 충돌 발생 시 개발자가 직접 재시도 로직을 작성

            ```java
            @Entity
            public class Stock {
                @Id @GeneratedValue
                private Long id;
            
                @Version // 버전 관리용 필드
                private Long version;
            
                private Long quantity;
            }
            ```

        - **Named Lock**
            - 데이터 자체에 락을 거는 것이 아니라, 별도의 공간에 존재하는 이름(String)을 점유하는 방식
                - 데이터와 상관없이 임의의 이름을 지정하여 락 설정
            - 과정
                - 사용자가 `A`라는 이름으로 락을 요청
                - DB는 `A`라는 이름이 이미 점유 중인지 확인
                - 점유 중이 아니라면 해당 세션이 락을 획득하고 작업을 수행
                - 작업이 끝나면 반드시 락을 해제
            - 주의사항:
                - 락을 획득하고 유지하는 동안 DB 커넥션을 계속 점유 → 커넥션 풀 고갈
                - 트랜잭션이 종료된다고 해서 락이 자동으로 해제되지 않는 경우 → 명시적으로 해제
    - **Redis(분산 락)**
        - 여러 대의 서버가 하나의 공통된 저장소(Redis)를 바라보게 하여 Lock을 획득하는 방식
            - **Lettuce**
                - 스핀 락(Spin Lock) 방식으로 구현
                - 장점: 별도의 라이브러리 설치 없이 Spring 기본 설정만으로 구현 가능
                - 단점: 실패 시 반복적으로 시도(spin) → Redis 부하
                    - 타임아웃 부재: 락을 획득한 서버가 죽으면 락이 영원히 해제되지 않는 위험이 있어 만료 시간 직접 계산 필요
            - **Redisson**
                - Pub/Sub 기반으로 구현하여 스핀 락의 단점을 보완 + 효율적인 Lock 획득을 지원
                - 장점: 불필요한 재시도 로직이 없어 Redis 서버가 쾌적
                    - Watchdog 기능: 락 점유 시간이 길어질 경우 자동으로 연장해 주어, 비즈니스 로직 도중 락이 풀리는 불상사를 방지
                    - 락 타임아웃 제공: 락 획득 대기 시간을 설정할 수 있어 무한 대기를 방지

                ```java
                public void withdraw(String lockKey) {
                    RLock lock = redissonClient.getLock(lockKey);
                
                    try {
                        // waitTime: 락 획득을 위해 대기할 시간
                        // leaseTime: 락 점유 시간 (이후 자동 해제)
                        boolean available = lock.tryLock(10, 2, TimeUnit.SECONDS);
                
                        if (available) {
                            // 비즈니스 로직 수행
                        }
                    } catch (InterruptedException e) {
                        // 예외 처리
                    } finally {
                        if (lock.isHeldByCurrentThread()) {
                            lock.unlock();
                        }
                    }
                }
                ```
<img width="726" height="265" alt="Image" src="https://github.com/user-attachments/assets/0fffd449-cdc5-4b6b-b3ce-b35a28d46526" />

# Client

### RestTemplate

- Spring 3부터 지원된 가장 오래된 동기 방식의 HTTP 클라이언트
- 장점:
    - 오래된 만큼 자료가 방대하고 사용법이 직관적
    - Spring 프로젝트라면 별도의 의존성 추가 없이 바로 사용 가능
- 단점:
    - Blocking I/O 방식이라 요청을 보내고 응답이 올 때까지 스레드가 대기
    - 직관적이지 않은 사용법
    - Template 클래스에 너무 많은 HTTP 기능이 노출

### OpenFeign

- Netflix에서 개발하고 Spring Cloud가 관리하는 클라이언트
- 장점:
    - 가독성 → 비즈니스 로직과 HTTP 호출 로직이 완벽히 분리
    - Spring MVC 어노테이션(`@GetMapping` 등)을 그대로 사용할 수 있어 러닝 커브가 낮음
    - Ribbon(로드밸런싱), Hystrix(서킷 브레이커) 등 Spring Cloud 생태계와 결합이 매우 쉬움
- 단점:
    - 기본적으로 동기 방식으로 동작
    - HTTP 상세 설정(타임아웃 등)을 커스터마이징하려면 별도의 설정 클래스가 필요

### WebClient

- Spring 5에서 도입된 클라이언트
- 장점:
    - 비차단(Non-Blocking): `WebClient`는 비동기 처리에 특화되어 적은 스레드로 많은 요청 처리
- 단점:
    - `WebClient`는 Reactor(Flux, Mono) 개념을 알아야 하므로 러닝 커브가 높음

### RestClient

- `RestTemplate`의 동기 방식 장점과 `WebClient`의 현대적인 Fluent API 디자인을 결합한 최적의 대안
- 함수형 인터페이스 (Fluent API): 빌더 패턴과 유사한 체이닝 방식을 통해 가독성이 좋음
- 동기식 처리 (Synchronous): 결제와 같이 순차적 실행과 결과 확인이 중요한 비즈니스 로직에 적합
- 선언적 에러 핸들링: `.onStatus()` 메서드를 통해 HTTP 상태 코드별 예외 처리를 직관적으로 구현 가능