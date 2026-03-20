<details>
<summary><h1>❓</h1></summary>
  
## 1. EntityManager는 누가 생성하고, DB와의 연결은 어떻게 이루어질까요?

### 1) EntityManager의 생성 주체

`EntityManager`는 직접 생성x → JPA의 공장 역할을 하는 `EntityManagerFactory`에 의해 생성

- **EntityManagerFactory :** 애플리케이션 로딩 시점에 DB당 하나만 생성됨. 설정 정보(persistence.xml 등)를 바탕으로 DB 연결에 필요한 무거운 객체를 구성함.
- **EntityManager :** 고객의 요청(트랜잭션 단위)이 올 때마다 `EntityManagerFactory`에서 생성함.
- **생성 방식 :**
    - **J2SE(일반 자바) 환경 :** `factory.createEntityManager()`를 호출하여 직접 생성함.
    - **Spring/EE 환경 :** `@PersistenceContext` 어노테이션을 사용하면 컨테이너(Spring)가 생성과 주입을 자동으로 관리함.

### 2) DB와의 연결 방식

`EntityManager`는 생성되자마자 DB 연결을 맺는 것이 아님. 효율적인 자원 관리를 위해 필요한 시점(Lazy)에 연결을 수행함.

- **연결 시점 :** 보통 트랜잭션이 시작되거나, 실제 DB 조회가 필요한 시점에 커넥션 풀에서 커넥션을 획득
- **커넥션 풀 활용 :** `EntityManagerFactory`가 생성될 때 미리 설정된 커넥션 풀(HikariCP 등)을 확보해 둠 `EntityManager`는 이 풀에서 커넥션을 빌려 쓰고, 작업이 끝나면 다시 반납
- **영속성 컨텍스트 :** `EntityManager` 내부에 존재하며, 엔티티를 영구 저장하는 환경으로 DB 연결 전 단계에서 1차 캐시 역할을 수행함.

### 3-1) Spring Data JPA에서의 사용 : EntityManager 프록시 객체 주입

- **프록시 활용 :** `@PersistenceContext`나 생성자 주입으로 받는 `EntityManager`는 실제 객체가 아닌 스프링이 생성한 **프록시 객체**임
- **동적 바인딩 :** 메서드 호출 시점에 **트랜잭션 동기화 매니저**를 조회하여 현재 트랜잭션에 할당된 실제 `EntityManager`에 작업을 위임함
- **스레드 안전성 :** 프록시를 통해 각 요청(스레드)마다 별도의 실제 `EntityManager`를 연결하므로 멀티스레드 환경에서도 안전하게 사용 가능

### 3-2) 트랜잭션 및 영속성 컨텍스트 관리

- **트랜잭션 시작 :** `@Transactional` 어노테이션이 붙은 메서드 실행 시 `TransactionInterceptor`가 트랜잭션을 개시
- **EntityManager 생성 :** 트랜잭션 시작 시점에 `EntityManagerFactory`로부터 `EntityManager`를 생성하고 영속성 컨텍스트를 구축
- **커넥션 획득 :** 실제 DB 조작이 필요한 시점(Lazy)에 커넥션 풀에서 DB 커넥션을 획득하여 사용
- **자원 반납 :** 트랜잭션 종료(커밋/롤백) 시 `EntityManager`를 닫고 커넥션을 풀에 반납

---

### 3-3) JpaRepository와 EntityManager의 관계

- **구현체 내부 동작 :** `JpaRepository`의 기본 구현체인 `SimpleJpaRepository` 내부에 `EntityManager`가 필드로 포함
- **기능 위임 :** `save()`, `find()`, `delete()` 등의 메서드 호출 시 주입된 프록시 `EntityManager`의 표준 JPA 메서드를 호출하여 처리

## 2. flush의 발생하는 시점은 언제일까요?

### 1) 직접 호출 (`em.flush()`)

- **강제 반영 :** 개발자가 코드상에서 명시적으로 `flush()`를 호출하여 영속성 컨텍스트의 변경 내용을 즉시 DB에 동기화
- **사용 사례 :** 트랜잭션이 끝나기 전, 중간에 SQL이 실행되는 것을 확인하고 싶거나 특정 로직 직전에 DB 반영이 필요할 때 사용

### 2) 트랜잭션 커밋 시 자동 호출

- **동기화 보장 :** JPA는 트랜잭션을 커밋하기 직전에 자동으로 `flush`를 호출
- **이유 :** 영속성 컨텍스트의 변경 내용(Insert, Update, Delete)을 DB에 보내지 않고 커밋하면, DB에는 아무런 변화가 일어나지 않기 때문에 반드시 커밋 전에 SQL을 전달해야 함

### 3) JPQL 쿼리 실행 직전 자동 호출

- **데이터 일관성 유지 :** JPQL은 영속성 컨텍스트를 거치지 않고 DB에 직접 SQL을 던짐
- **문제 방지 :** 만약 영속성 컨텍스트에서 엔티티를 수정했는데 `flush` 되지 않은 상태에서 JPQL로 해당 데이터를 조회하면, 수정 전의 옛날 데이터가 조회되는 문제가 발생
- **해결 방식 :** 이런 데이터 불일치를 막기 위해 JPA는 JPQL 실행 전 무조건 `flush`를 수행하여 최신 상태를 DB에 반영

### 4) 주의사항

- **flush ≠ commit :** `flush`는 영속성 컨텍스트의 변경 내용을 DB에 전달(SQL 실행)하는 과정일 뿐, 트랜잭션을 완전히 종료하는 `commit`과는 다름.
- **롤백 가능 :** `flush`가 호출되어 SQL이 DB에 전달되었더라도, 트랜잭션이 커밋되기 전이라면 언제든 롤백이 가능

## 3.  **JOIN을 사용할 때 SQL과 JPQL이 어떤 기준으로 조인을 수행하는지** 비교해보면 차이를 더 쉽게 이해할 수 있어요

### 1) SQL의 조인 기준 (테이블 중심)

**SQL은 데이터베이스의 테이블과 외래 키(FK)를 기준으로 조인을 수행**

- **조인 대상 :** 물리적인 테이블 간의 결합
- **기준 컬럼 :** 외래 키와 기본 키의 값이 일치하는지를 명시적으로 확인해야 함
- **명시적 조건 :** ON 절을 통해 어떤 컬럼끼리 매칭할지 개발자가 직접 작성해야 함

```sql
SELECT M.*, T.*
FROM MEMBER M
JOIN TEAM T ON M.TEAM_ID = T.ID  -- 외래 키 컬럼 기준
```

### 2) JPQL의 조인 기준 (엔티티 객체 중심)

**JPQL은 데이터베이스 테이블이 아닌 엔티티 객체와 그들 간의 연관관계 필드를 기준으로 조인을 수행**

- **조인 대상 :** 객체 그래프를 탐색하는 과정
- **기준 필드 :** 엔티티 클래스 내에 선언된 `@ManyToOne` 등의 **연관관계 필드**
- **암묵적 매핑 :** 이미 엔티티 설정에서 외래 키 매핑이 완료되었으므로, JPQL에서는 `m.team`처럼 객체 참조를 통해 조인 대상을 지정

```sql
SELECT m FROM Member m 
JOIN m.team t  -- Member 엔티티 내부의 team 필드(연관관계) 기준
```

### 3) Fetch Type과의 연결점

- **즉시 로딩 (Eager) :** JPQL로 엔티티를 조회할 때, JPA가 SQL을 생성하면서 연관된 테이블까지 미리 `JOIN` 쿼리에 포함시켜 한꺼번에 가져옴
- **지연 로딩 (Lazy) :** JPQL 실행 시점에는 해당 엔티티만 조회하고, 연관된 객체는 실제 사용되는 시점에 별도의 SQL을 실행하여 가져옴 (프록시 객체 활용)

이처럼 JPQL은 객체 지향적인 관점에서 쿼리를 작성하면, JPA가 이를 해석하여 데이터베이스 관점의 SQL 조인문으로 변환해주는 역할을 수행

## 4. fetch join을 사용하면서 페이징을 적용할 때 발생하는 문제에 대해 알아보아요!

### 1) 데이터 뻥튀기(Cartesian Product) 문제

- **중복 발생 :** DB 입장에서 '일(1)' 측 엔티티와 '다(N)' 측 엔티티를 조인하면, 결과 Row 수는 '다' 측의 개수에 맞춰서 늘어남
- **기준 상실 :** 예를 들어 팀 1개에 멤버가 3명 있다면, 조인 결과는 3개의 로우가 나옴. 이때 JPA는 "팀 1개를 가져와서 1페이지에 보여줘"라는 요청을 받았을 때, 3개의 로우 중 어디까지가 팀 1개인지 DB 레벨에서 계산하기 어려움

### 2) 인메모리 페이징 (HHH000104 경고)

- **하이버네이트의 처리 :** 하이버네이트는 DB 단에서 페이징 쿼리(`LIMIT`, `OFFSET`)를 날리는 것이 불가능하다고 판단하면, **모든 데이터를 메모리로 읽어온 뒤 애플리케이션 메모리에서 페이징을 처리**
- **위험성 :** 데이터가 수만 건 이상일 경우 `OutOfMemoryError`가 발생하여 서버가 다운될 수 있는 매우 위험한 상황
- **로그 확인 :** 실행 시 `firstResult/maxResults specified with collection fetch; applying in memory!`라는 경고 로그가 출력됨.

### 3) 해결 방법

- Batch Size 설정 (가장 권장됨)
    - **원리 :** 컬렉션 페치 조인을 포기하는 대신, 지연 로딩을 유지하면서 `IN` 절을 통해 설정한 개수만큼 한꺼번에 조회
    - **설정 방법 :** * 글로벌 설정: `application.yml`에 `hibernate.default_batch_fetch_size: 100` 추가
        
        개별 설정: 연관관계 필드 위에 `@BatchSize(size = 100)` 
        
    - **장점 :** N+1 문제를 해결하면서 페이징 쿼리도 정상적으로 DB에서 실행
- ToOne 관계만 페치 조인
    - **구분 :** `@ManyToOne`, `@OneToOne`은 데이터 뻥튀기가 발생하지 않으므로 페이징과 페치 조인을 함께 써도 무방
    - **전략 :** 'ToOne' 관계는 페치 조인으로 한 번에 가져오고, 컬렉션('ToMany')은 위에서 언급한 `Batch Size`로 처리
- DTO 직접 조회
    - **방식 :** 엔티티를 조회하지 않고 필요한 필드만 뽑아서 DTO로 변환하여 조회
    - **특징 :** 복잡한 통계성 쿼리나 페이징이 복잡할 때 성능 최적화에 유리

## 5. SimpleJpaRepository의 EntityManager 주입

싱글톤 객체가 상태를 가지면 스레드 세이프(Thread-safe)하지 않다? → 하지만 스프링은 **프록시 패턴**을 통해 이를 해결

- **프록시 객체 주입 :** `SimpleJpaRepository` 생성자에서 주입받는 `EntityManager`는 실제 DB에 연결된 객체가 아니라, 스프링이 만든 **공유 프록시(Shared Entity Manager)** 객체
- **동작 원리 :** 사용자가 리포지토리 메서드를 호출하면, 프록시 객체가 현재 스레드에 할당된 트랜잭션 동기화 매니저에서 해당 트랜잭션 전용 `EntityManager`를 찾아서 실제 작업을 위임
- **결론 :** 싱글톤 리포지토리는 하나의 가짜 프록시를 들고 있고, 실제 호출 시점에만 진짜 `EntityManager`를 연결하므로 동시성 문제가 발생하지 않음.

## 6. Fetch Join 시 `distinct`를 사용하지 않을 때의 문제

일대다(@OneToMany) 관계에서 페치 조인을 수행하면 SQL 결과에서 데이터 중복(Cartesian Product)이 발생함.

- **객체 중복 :** DB에서 팀(1)과 멤버(3)를 조인하면 로우가 3개 조회됨. JPA는 이를 그대로 읽어와서 결과 리스트에 동일한 팀 엔티티 객체를 3개 담아 반환
- **해결 방법 :** `select distinct t from Team t join fetch t.members`와 같이 `distinct`를 추가함.
    - **SQL distinct :** SQL 레벨에서 중복 제거를 시도함 (하지만 모든 컬럼 값이 같아야 하므로 효과가 미비할 수 있음)
    - **JPA distinct :** 하이버네이트가 애플리케이션 레벨에서 동일한 식별자(ID)를 가진 엔티티의 중복을 제거
    - *참고: Hibernate 6 버전부터는 `distinct`를 명시하지 않아도 엔티티 조회 시* 자동으로 중복을 제거

## 7. Fetch Join 관련 3대 에러 원인 및 해결 방안

### 1) HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!

- **원인 :** 일대다 컬렉션 페치 조인과 페이징(`Pageable`)을 동시에 사용했기 때문임. DB 레벨에서 조인으로 인해 로우 수가 늘어나 정확한 페이지 계산이 불가능하므로, 하이버네이트가 데이터를 전부 메모리로 퍼올려 페이징을 시도
- **해결 :** 페치 조인을 제거하고 **`hibernate.default_batch_fetch_size`** 설정을 통해 지연 로딩을 최적화

### 2) query specified join fetching, but the owner of the fetched association was not present in the select list

- **원인 :** 페치 조인을 사용하면서 `SELECT` 절에 조인의 기준이 되는 엔티티를 포함하지 않았을 때 발생함.
    - 예: `select m.name from Member m join fetch m.team` (Member 엔티티 자체가 아닌 필드만 조회하면서 페치 조인을 시도함)
- **해결 :** 페치 조인은 연관된 엔티티를 영속성 컨텍스트에 한꺼번에 올리기 위한 용도이므로, **반드시 기준 엔티티 자체를 조회**해야 함 (`select m from Member m ...`)

### 3) org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags

- **원인 :** 한 번의 쿼리에서 2개 이상의 일대다 컬렉션을 동시에 페치 조인하려고 할 때 발생함. 데이터가 기하급수적으로 늘어나는 카테시안 곱의 위험 때문에 하이버네이트가 이를 차단
- **해결 :** 1. 컬렉션 타입을 `List` 대신 `Set`으로 변경함 (단, 데이터 순서가 보장되지 않고 여전히 카테시안 곱 문제는 존재)
    
    2. 가장 권장되는 방법은 하나만 페치 조인하고 나머지는 **`Batch Size`** 설정을 통해 여러 번의 쿼리로 나누어 가져오는 것
</details>

# 🎬 CGV 클론코딩 서비스 소개

본 프로젝트는 실제 영화 예매 서비스인 CGV를 기반으로, 
영화 조회, 예매, 좌석 선택, 찜 기능, 매점 구매 등의 핵심 기능을 백엔드 중심으로 구현한 서비스입니다.

단순 CRUD 구현을 넘어,
다음과 같은 실제 서비스에서 발생하는 문제를 고려하여 설계하였습니다.

- 상영 회차별 좌석 관리
- 동일 좌석 중복 예매 방지
- 영화/극장 찜 기능
- 영화 통계 및 리뷰 관리
- 극장별 매점 재고 관리

### 📌 핵심 기능
- 영화관 조회 및 찜
- 영화 조회 (상세 정보, 리뷰, 통계 포함)
- 영화 예매 및 취소
- 좌석 선택 (상영관 기준)
- 영화 찜 기능
- 매점 주문 (극장별 재고 관리)

### ERD
https://www.erdcloud.com/d/br5ZwPJDjX9WvPYXH
<img width="1044" height="451" alt="image" src="https://github.com/user-attachments/assets/d1361fe0-e23c-4bdb-9f1b-1823a2ef6360" />


<details>
<summary><h1>CGV 클론코딩 ERD 연관관계 및 제약조건 정리</h1></summary>

## 1. 전체 엔티티 연관관계 정리

## 1-1. 영화관 / 상영관 / 좌석 관련

### Theater(극장) - Screen(상영관)

* **관계**: `1 : N`
* **설명**: 하나의 극장은 여러 개의 상영관을 가진다.
* **FK**: `Screen.theaterId`

### ScreenType(상영관 타입) - Screen(상영관)

* **관계**: `1 : N`
* **설명**: 하나의 상영관 타입(일반관, IMAX 등)은 여러 상영관에 적용될 수 있다.
* **FK**: `Screen.screenTypeId`

### ScreenType(상영관 타입) - SeatTemplate(좌석 템플릿)

* **관계**: `1 : N`
* **설명**: 하나의 상영관 타입은 여러 좌석 템플릿 정보를 가진다.
* **FK**: `SeatTemplate.screenTypeId`

### Screen(상영관) - Seat(좌석)

* **관계**: `1 : N`
* **설명**: 하나의 상영관은 여러 좌석을 가진다.
* **FK**: `Seat.screenId`

### Screen(상영관) - MovieScreen(영화상영)

* **관계**: `1 : N`
* **설명**: 하나의 상영관에서는 여러 상영 회차가 존재할 수 있다.
* **FK**: `MovieScreen.screenId`

---

## 1-2. 영화 관련

### Movie(영화) - MovieImage(영화사진)

* **관계**: `1 : N`
* **설명**: 하나의 영화는 여러 장의 이미지를 가질 수 있다.
* **FK**: `MovieImage.movieId`

### Movie(영화) - MovieStatistics(영화통계)

* **관계**: `1 : 1`
* **설명**: 하나의 영화는 하나의 통계 정보를 가진다.
* **FK**: `MovieStatistics.movieId`

### Movie(영화) - MovieScreen(영화상영)

* **관계**: `1 : N`
* **설명**: 하나의 영화는 여러 상영 회차를 가질 수 있다.
* **FK**: `MovieScreen.movieId`

### Movie(영화) - Review(리뷰)

* **관계**: `1 : N`
* **설명**: 하나의 영화에는 여러 리뷰가 작성될 수 있다.
* **FK**: `Review.movieId`

### Movie(영화) - MovieLike(영화찜)

* **관계**: `1 : N`
* **설명**: 하나의 영화는 여러 사용자에게 찜될 수 있다.
* **FK**: `MovieLike.movieId`

### Movie(영화) - MovieActor(영화출연자)

* **관계**: `1 : N`
* **설명**: 하나의 영화에는 여러 출연자/감독 정보가 연결될 수 있다.
* **FK**: `MovieActor.movieId`

### Actor(출연자) - MovieActor(영화출연자)

* **관계**: `1 : N`
* **설명**: 하나의 출연자/감독은 여러 영화에 연결될 수 있다.
* **FK**: `MovieActor.actorId`

> 따라서 `Movie` 와 `Actor` 는 `MovieActor` 를 통한 **N : M 관계**이다.

---

## 1-3. 예매 관련

### User(유저) - Reservation(예매)

* **관계**: `1 : N`
* **설명**: 하나의 유저는 여러 예매를 할 수 있다.
* **FK**: `Reservation.userId`

### MovieScreen(영화상영) - Reservation(예매)

* **관계**: `1 : N`
* **설명**: 하나의 상영 회차에는 여러 예매가 발생할 수 있다.
* **FK**: `Reservation.movieScreenId`

### Reservation(예매) - ReservationSeat(예매좌석)

* **관계**: `1 : N`
* **설명**: 하나의 예매는 여러 좌석을 포함할 수 있다.
* **FK**: `ReservationSeat.reservationId`

### Seat(좌석) - ReservationSeat(예매좌석)

* **관계**: `1 : N`
* **설명**: 하나의 좌석은 여러 회차에서 반복적으로 예매될 수 있다.
* **FK**: `ReservationSeat.seatId`

### MovieScreen(영화상영) - ReservationSeat(예매좌석)

* **관계**: `1 : N`
* **설명**: 하나의 상영 회차에는 여러 예약 좌석 정보가 존재할 수 있다.
* **FK**: `ReservationSeat.movieScreenId`

---

## 1-4. 영화관 찜 / 매점 관련

### User(유저) - TheaterLike(극장찜)

* **관계**: `1 : N`
* **설명**: 하나의 유저는 여러 극장을 찜할 수 있다.
* **FK**: `TheaterLike.userId`

### Theater(극장) - TheaterLike(극장찜)

* **관계**: `1 : N`
* **설명**: 하나의 극장은 여러 사용자에게 찜될 수 있다.
* **FK**: `TheaterLike.theaterId`

> 따라서 `User` 와 `Theater` 는 `TheaterLike` 를 통한 **N : M 관계**이다.

---

## 1-5. 음식 / 주문 관련

### Food(음식) - TheaterFood(극장음식)

* **관계**: `1 : N`
* **설명**: 하나의 음식은 여러 극장에서 판매될 수 있다.
* **FK**: `TheaterFood.foodId`

### Theater(극장) - TheaterFood(극장음식)

* **관계**: `1 : N`
* **설명**: 하나의 극장은 여러 음식 재고를 가진다.
* **FK**: `TheaterFood.theaterId`

> 따라서 `Food` 와 `Theater` 는 `TheaterFood` 를 통한 **N : M 관계**이며,
> `TheaterFood.amount` 로 극장별 재고를 관리한다.

### User(유저) - FoodOrder(음식구매)

* **관계**: `1 : N`
* **설명**: 하나의 유저는 여러 음식 주문을 할 수 있다.
* **FK**: `FoodOrder.userId`

### Theater(극장) - FoodOrder(음식구매)

* **관계**: `1 : N`
* **설명**: 하나의 극장에서는 여러 음식 주문이 발생할 수 있다.
* **FK**: `FoodOrder.theaterId`

### FoodOrder(음식구매) - FoodOrderItem(음식구매항목)

* **관계**: `1 : N`
* **설명**: 하나의 주문은 여러 음식 항목을 포함할 수 있다.
* **FK**: `FoodOrderItem.foodOrderId`

### Food(음식) - FoodOrderItem(음식구매항목)

* **관계**: `1 : N`
* **설명**: 하나의 음식은 여러 주문 항목에 포함될 수 있다.
* **FK**: `FoodOrderItem.foodId`

---

## 1-6. 리뷰 관련

### User(유저) - Review(리뷰)

* **관계**: `1 : N`
* **설명**: 하나의 유저는 여러 영화에 리뷰를 작성할 수 있다.
* **FK**: `Review.userId`

### Movie(영화) - Review(리뷰)

* **관계**: `1 : N`
* **설명**: 하나의 영화는 여러 리뷰를 가질 수 있다.
* **FK**: `Review.movieId`

---

## 2. ReservationSeat에 movieScreenId를 중복 저장한 이유

`ReservationSeat`에는 이미 `reservationId`가 있고,
`Reservation`에도 `movieScreenId`가 존재하므로 언뜻 보면 `ReservationSeat.movieScreenId`는 중복 데이터처럼 보임

하지만 이 중복은 **의도적인 중복**이며, 다음과 같은 이유로 사용함

### 2-1. 동일 상영 회차의 동일 좌석 중복 예매 방지

예매 시스템에서 가장 중요한 무결성 중 하나는 다음이다.

* 같은 상영 회차(`movieScreenId`)에서
* 같은 좌석(`seatId`)은
* 한 번만 예약 가능해야 한다.

이를 DB 레벨에서 보장하려면 `ReservationSeat` 테이블에 아래 복합 유니크 제약을 둘 수 있어야 함

```sql
UNIQUE (movie_screen_id, seat_id)
```

하지만 `ReservationSeat`에 `movieScreenId`가 없으면 `Reservation`을 조인해야만 상영 회차를 알 수 있으므로, 단일 테이블 기준의 유니크 제약을 만들 수 없음
즉, `ReservationSeat.movieScreenId`는 **중복 예매를 DB에서 직접 차단하기 위해 필요한 중복 컬럼**

---

### 2-2. 상영 회차별 예약 좌석 조회 성능 향상

예매 화면에서는 특정 상영 회차의 예약된 좌석 목록을 자주 조회하게 됨

예시:

* 특정 회차에서 이미 예약된 좌석 조회
* 잔여 좌석 수 계산
* 좌석 선택 화면 렌더링

이때 `ReservationSeat`에 `movieScreenId`가 있으면 다음과 같이 바로 조회할 수 있음

```sql
SELECT seat_id
FROM reservation_seat
WHERE movie_screen_id = ?;
```

즉, 조인 없이 단순 조회가 가능해져 **쿼리가 단순해지고 성능상 이점**이 있다.

---

### 2-3. 정리

따라서 `ReservationSeat.movieScreenId`는 단순 중복이 아니라,

* **중복 예매 방지**
* **조회 성능 향상**
* **회차별 좌석 점유 상태를 명확하게 표현**

을 위한 **의도된 비정규화**

단, 이 구조를 사용할 경우 아래 두 가지는 서비스 로직에서 반드시 검증 필요

1. `Reservation.movieScreenId == ReservationSeat.movieScreenId`
2. `ReservationSeat.seatId`가 속한 `Seat.screenId == ReservationSeat.movieScreenId`가 속한 `MovieScreen.screenId`

즉, 예매의 상영 회차와 예매좌석의 상영 회차가 같아야 하고 선택한 좌석은 해당 상영 회차의 상영관 좌석이어야 함.

---

## 3. 유니크 제약 조건 정리

아래 유니크 제약은 데이터 무결성 보장을 위해 필요

---

### 3-1. ReservationSeat

동일 회차에서 동일 좌석 중복 예매 방지

```sql
UNIQUE (movie_screen_id, seat_id)
```

---

### 3-2. Seat

하나의 상영관 안에서 같은 좌석 위치 중복 생성 방지

```sql
UNIQUE (screen_id, row_name, col_num)
```

예시:

* 같은 상영관에 `A열 1번` 좌석이 두 번 생성되면 안 됨

---

### 3-3. MovieLike

한 사용자가 같은 영화를 여러 번 찜하는 것 방지

```sql
UNIQUE (user_id, movie_id)
```

---

### 3-4. TheaterLike

한 사용자가 같은 극장을 여러 번 찜하는 것 방지

```sql
UNIQUE (user_id, theater_id)
```

---

### 3-5. Review

한 사용자가 같은 영화에 리뷰를 여러 번 작성하지 못하도록 제한 (정책이 "영화당 리뷰 1개"인 경우)

```sql
UNIQUE (user_id, movie_id)
```

---

### 3-6. MovieStatistics

하나의 영화에 하나의 통계만 존재하도록 보장

```sql
UNIQUE (movie_id)
```

---

### 3-7. MovieScreen

동일 상영관에서 같은 시작 시간의 중복 상영 방지
(필요 시)

```sql
UNIQUE (screen_id, start_at)
```

> 다만 상영 시간 겹침 자체는 단순 유니크만으로 완벽히 막을 수 없으므로,
> 실제로는 서비스 로직에서 `start_at ~ end_at` 겹침 검증이 추가로 필요하다.

---

### 3-8. MovieImage

대표 이미지 정렬이나 중복 관리가 필요하다면 다음과 같은 제약 고려 가능

예:

```sql
UNIQUE (movie_id, movie_image_url)
```

또는

```sql
UNIQUE (movie_id, sort_order)
```

---

## 4. 서비스 로직에서 추가로 검증해야 하는 부분

일부 제약은 DB만으로 완벽히 표현하기 어렵기 때문에 서비스 계층에서 추가 검증 필요

### 4-1. 예매 좌석 무결성 검증

* `Reservation.movieScreenId == ReservationSeat.movieScreenId`
* `Seat.screenId == MovieScreen.screenId`

즉, 예매와 예매좌석의 회차가 일치해야 하며 좌석은 해당 상영관의 좌석이어야 함

---

### 4-2. 상영 시간 겹침 검증

같은 상영관에서 상영 시간이 겹치는 회차는 등록되면 안 됨

예:

* 10:00 ~ 12:00 상영이 있는데
* 11:30 ~ 13:30 상영을 추가하면 안 됨

이는 단순 유니크로는 막기 어렵고 서비스 로직에서 시간 범위 겹침 검사 필요

---

### 4-3. 음식 재고 검증

`TheaterFood.amount`는 극장별 음식 재고를 의미하므로

* 주문 시 재고 이상 구매 불가
* 구매 완료 시 재고 차감
* 환불 불가 정책이면 주문 취소에 따른 재고 복구 없음

이 정책을 서비스 로직에서 관리해야 함.

</details>
