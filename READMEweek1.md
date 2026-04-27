<<<<<<< HEAD
![스크린샷 2026-03-21 113600.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-21%20113600.png)
스터디에서도 소개했지만 자바 또는 스프링을 이용할 때의 계층구조는 위의 사진처럼 이루어져있다.

## JDBC (JavaDataBaseConnectivity)

자바에서 DB 접근기능을 제공하는 표준화된 API다.

DB마다 자바와 통신방식, SQL을 전달하는 방법 등이 전부 다른데, 만약 JDBC가 없다면 DB마다 새로이 연결하는 코드, SQL을 보내는코드, 응답을 받는 코드를 새로 만들어야한다.

따라서 모든 DB에 사용할 수 있는 표준화된 API에 대한 필요성이 생겨났고, 그래서 JDBC가 등장했다.

![스크린샷 2026-03-21 113628.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-21%20113628.png)

이런 구조로 이루어져있다.

DB 제공회사 측에서는 JDBC 표준 인터페이스를 구현한 라이브러리를 제공하는데, 여기서 Connection (연결), Statement (SQL 전달), ResultSet (결과 응답)에 대한 기능을 제공한다. 따라서 MySQL이라면 MySQL 드라이버를 통해, 오라클DB라면 오라클 드라이버를 통해 같은 메서드로 연결, SQL 보내기, 결과받기를 전부 수행할 수 있는 것이다.

```java
package Repository;

import Domain.Member;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRepository {
    private final Connection con;

    public MemberRepository(Connection con) {
        this.con = con;
    }

    public void save(Member member) throws IOException {
        try {
            con.setAutoCommit(false);
            saveMember(member);
            con.commit();

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e){
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean existsByUserid(String userid) throws IOException {
        String sql = "SELECT * FROM members WHERE USERID = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)){
            preparedStatement.setString(1, userid);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
```

JDBC는 표준화된 API를 제공한다는 장점이 있으나, 위와같이 비즈니스 로직에 불필요한 DB 커넥션과 접근 기능이 많은 편이다. 위는 에러가 발생했을 시 롤백 기능을 담은 코드이며, 여기에 finally 구문을 추가하여 오류가 발생했을 때 Connection을 닫는 코드도 필요하다. (또는 TWR을 사용할 수도 있다)

---

## Hibernate → JPA

따라서 반복적이며 불필요한 SQL문의 작성을 줄이고 DB 접근기능만 제공하고자 등장한 것이 ORM

ORM은 객체 중심의 자바와 테이블 중심의 SQL 간 상호호환을 위해 등장했다. 기존에는 자바의 객체를 테이블로 바꿔서 SQL문을 만들어야했지만, ORM의 등장으로 자동으로 호환이 가능해졌다.

ORM은 Hibernate, EclipseLink, OpenJPA 등 여러 종류가 있었지만, 서로 호환이 되지 않는다는, JDBC가 등장한 배경과 비슷한 문제점이 다시 등장했고, JPA가 등장해 서로 다른 여러 ORM에 포괄적으로 적용할 수 있는 인터페이스를 제공하기 시작했다.

---

## EntityManager

Driver가 JDBC의 주체였다면 JPA의 주체는 EntityManager다.

![스크린샷 2025-03-14 오전 12.50.26.png](attachment:fa4d882d-258d-4478-89b6-3ff73ed00c95:스크린샷_2025-03-14_오전_12.50.26.png)

EntityManager는 영속성 컨텍스트를 관리한다.

영속성 컨텍스트란 Entity를 잠시 저장하는, 데이터베이스와 애플리케이션 사이의 징검다리 역할을 한다. EntityManager를 통해 영속성 컨텍스트에 엔티티를 수정, 삭제, 추가, 조회 등의 기능을 수행하면 엔티티가 영속성 컨텍스트에 저장 및 관리된다. 그리고 EntityManager의 flush() 메서드를 통하면 영속성 컨텍스트의 진행상황이 데이터베이스에 반영되는 식으로 동작한다.

### 엔티티의 상태

엔티티의 상태는

- **비영속**

    ```java
    Member member = new Member();
    ```

    - 객체(엔티티)가 생성되었을 뿐 아직 영속성 컨텍스트에 등록되지 않은 상태

- **영속**

    ```java
    EntityManagerFactory emf = Persistence.createManagerFactory("example");
    EntityManager em = emf.createEntityManager();
    
    em.persist(member);
    ```

    - 객체(엔티티)가 영속성 컨텍스트에 등록된 상태
    - 영속상태를 위해서는 반드시 식별자값이 필요하다.

- **준영속**

    ```java
    em.detach(member);
    em.clear();
    em.close();
    ```

    - 객체(엔티티)가 영속성 컨텍스트에 등록되었으나, 모종의 사유(detach, EntityManager가 비워지거나 종료됨)로 인해 영속성 컨텍스트에서 떨어져나온경우가 해당한다.

  ![스크린샷 2026-03-21 113655.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-21%20113655.png)
    - 준영속과 비영속의 차이는 준영속은 영속상태에 돌입한 적이 있어 식별자를 필수로 갖고 있어야한다는 점이다. 이외에는 다른 점이 없다.


- **삭제**

    ```java
    em.remove(member);
    ```

    - 객체(엔티티)가 영속성 컨텍스트 및 데이터베이스에서 삭제된 상태.
    - detach와 remove의 차이는 detach 시에는 영속만 제외해서 변경감지 기능이 꺼져있지만 DB에는 엔티티가 남아있고, EntityManager를 통해 조회나 merge는 가능한 반면, 삭제는 DB에서 엔티티를 지워버린다.

영속성 컨텍스트를 사용한다면

1. 캐싱: DB까지 조회할 필요 없이 영속성 컨텍스트에서 값을 꺼내볼 수 있다.
2. 변경감지: 영속 엔티티를 수정하면 굳이 save나 flush를 직접 진행하지 않더라도 commit 시 영속성 컨텍스트와 DB에 자동반영된다.
3. 쓰기지연: EntityManager의 메서드를 불러올 때마다 DB에 접근하는 것이 아니라 트랜잭션이 commit, 또는 flush 될 때만 DB에 접근하므로 불필요한 DB접근을 줄일 수 있다.

그래서 flush는 기본적으로 트랜잭션이 커밋될 때 발생한다. 때로는 EntityManager에서 직접 flush()를 호출하거나, JPQL 쿼리 실행 직전에 발생한다.

트랜잭션 커밋은 원래 flush()를 기반으로 동작하고, EntityManager에서 직접적으로 flush()를 호출하면 flush가 일어나는게 당연하지만, JPQL 쿼리 실행 직전에 flush가 일어나는 이유는

```java
em.persist(memberA); // 아직 DB에는 반영되지 않음

List<Member> result = em.createQuery("select m from Member m", Member.class).getResultList();
```

JQPL 쿼리는 테이블을 중심으로 데이터베이스에 저장된 엔티티를 관리하는 SQL과 단리 엔티티를 중심으로 데이터베이스를 관리하는 언어다. JPQL의 가장 큰 특징은 영속성 컨텍스트를 사용하지 않고 그대로 데이터베이스에 쿼리를 보내는 방식이라, JPQL 쿼리를 실행하기 직전에 영속성 컨텍스트와 데이터베이스를 동기화할 필요가 있기 때문이다.

### 커밋 vs 플러시

flush()는 영속성 컨텍스트의 변경사항을 그즉시 DB에 반영한다. 쓰기지연 때문에 쓰기 지연 저장소에 있던 SQL들이 DB로 전송된다. 중요한 것은 **영속성 컨텍스트는 유지한 채, DB와 영속성 컨텍스트의 동기화**가 flush. flush 진행과정은 변경감지 → 변경기록을 쓰기 지연 저장소에 저장 → flush 시 저장된 SQL을 DB로 전송 정도로 볼 수 있다.

트랜잭션 커밋은 영속성 컨텍스트의 변경사항을 즉시 DB에 반영함과 더불어 **동기화된 정보를 DB에 영구히 반영하며, 영속성 컨텍스트도 비워버리므로 롤백이 불가능**하다. 메서드 위에 @Transactional 어노테이션을 사용하면 메서드의 실행부터 return 까지를 하나의 트랜잭션으로 관리한다. return 될 때 커밋이 일어나고, 에러가 발생하면 롤백하는 식으로..

### EntityManager의 생성

```java
EntityManagerFactory emf = Persistence.createManagerFactory("example");
EntityManager em = emf.createEntityManager();
```

EntityManager는 EntityManagerFactory에서 생성되며, 기본적으로 요청 한 개 당 하나의 EntityManager를 사용한다. 동시에 여러 요청이 발생하면 동시에 여러 EntityManager가 생성되는 셈.

EntityManagerFactory는 생성 시 자원이 많이 소모되는 이유등으로 인해 최소한 적게 생성되어야한다.

---

## 스프링 JPA

순수 JPA는 EntityManager를 사용해야한다는 단점이 있었고, 직접 JPQL 쿼리를 작성해야하는 경우도 있었다.

따라서 스프링 JPA가 등장해 메서드 이름을 기반으로 자동으로 SQL이나 JPQL 쿼리를 생성하고, EntityManager를 직접 사용하지 않아도 된다는 장점을 가지게 되었다.

---

## JPA 사용하기

```java
package com.ceos23.spring_cgv_23rd.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;
    
    public User() {}

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
}

```

엔티티 객체를 만든다.

@GeneratedValue() 어노테이션은 id 값을 어떻게 설정할지를 정할 수 있다.

- AUTO: 설정파일에 hibernate.dialect에 DB 종류를 설정할 수 있는데, 이에따라 hibernate가 자동으로 id 값을 채운다.
- IDENTITY: DB에 전적으로 id 값 설정을 위임한다. 자동으로 ID값이 증가하는 형태로 사용되며, 따라서 영속화 과정에서 flush하지 않더라도 이전 엔티티의 ID값을 알아야하므로 DB로 INSERT 문이 보내진다.
- SEQUENCE:  DB 시퀀스에 따라서 값을 증가시킨다. 오라클, Postgre, DB2, H2에서만 사용가능하다.
- TABLE: DB 테이블에 따라서 값을 증가시킨다. 모든 DB에 사용할 수 있으나 성능에 문제가 있다.

```java
package com.ceos23.spring_cgv_23rd.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}

```

리포지토리 인터페이스를 만든 후에 JpaRepository를 상속하면 된다.  JpaRepository<T, ID> 중에서 T는 저장할 엔티티의 타입, ID는 식별자 ID값이다.

```java
@Override
@Transactional
public <S extends T> S save(S entity) {

    Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);

    if (entityInformation.isNew(entity)) {
        entityManager.persist(entity);
        return entity;
    } else {
        return entityManager.merge(entity);
    }
}
```

UserRepository 인터페이스를 사용하면 JpaRepository의 기본 구현체인 SimpleJpaRepository를 사용하게되는데, 거기서 이미 등록된 메서드도 사용할 수 있다.

![스크린샷 2026-03-21 114831.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-21%20114831.png)
스프링 JPA를 사용하다보면 이렇게 인터페이스에 메서드만 선언해도 자동으로 사용할 수 있는 경우가 있는데, 이는 프록시 객체가 담당한다.

```java
public void test(){
    System.out.println(memberRepository.getClass());
}
```

![image.png](attachment:35ac0a2b-c088-4082-afd3-e0ac9771feaa:image.png)

```java
JpaRepository를 상속한 Repository 인터페이스 호출

↓

프록시 객체가 대신 생성되어 메서드 호출을 가로챔

↓

프록시 객체가 메서드 이름을 분석하여 적절한 쿼리문을 수행
```
![스크린샷 2026-03-21 114911.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-21%20114911.png)
이래서 프록시 객체는 메서드이름만을 통해서 적절히 쿼리문을 수행할 수 있는 것이다.

---

# DB관련 어노테이션

![스크린샷 2026-03-21 113730.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-21%20113730.png)
정말 순수한 DB를 다룰 때에는 위와 같이 FK가 필요할 때 외래테이블의 ID값을 FK로 저장하고, 매번 FK 컬럼을 조사하려면 ID값을 토대로 다른 테이블에 조회 쿼리를 보내야했다.

```java
class TheaterFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private int menu_Id;
    
    private boolean isSoldOut;
}
```

이렇게 다뤘어야했다.

```java
class TheaterFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private Menu menu;
    
    private boolean isSoldOut;
}
```

하지만 JPA 환경에서는 객체의 참조관계를 엔티티에 적용시킬 수 있게되면서, 객체, 엔티티 간 관계를 표시해야한다.

---

## JPA 연관관계

RDBMS에는 관계에 방향이 없지만, JPA에는 방향이 존재하는데, User와 Order 객체 관계에서 예를 들면

- **@ManyToOne**

    ```java
    class Order {
        @ManyToOne
        User user;
    }
    ```

    - 엔티티가 Many고, 참조 중인 객체가 One이다. 위의 경우에는 여러 Order가 하나의 User를 가진다.
    - N:1 관계


- **@OneToMany**

    ```java
    class User {
        @OneToMany(mappedBy = "user")
        List<Order> orders = new ArrayList<>();
    }
    ```

    - ManyToOne의 역방향으로, 엔티티가 One이고, 참조 중인 객체가 Many다. 위의 경우에는 한 User 객체를 여러 Order 객체에서 참조 중이다.
    - 1:N 관계


이렇게 있다. 만약 ManyToOne 어노테이션만 적어두었다면 DB를 통해서 Order → User 방향으로는 접근이 가능하지만 반대로 User → Order 방향으로는 접근이 가능하지 않다. 두 개의 어노테이션을 사용하면 마치 두 테이블을 양방향으로 동작하는 것처럼 사용할 수 있다.

### FK의 주인

ManyToOne과 OneToMany 방식을 통해 양방향으로 관계를 표현할 수 있지만, 실제로 FK는 ManyToOne 어노테이션이 붙은 컬럼에만 저장된다. 실제 FK가 저장되는 테이블을 연관관계의 주인이라고 칭한다.

따라서 연관관계의 주인에는 FK값과 더불어 JPA 내에서 객체로 사용할 수 있지만, 비주인은 JPA를 통해 객체로 사용할 수는 있지만 DB에는 FK가 저장되지 않는다.

---

ㄹ

```java
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    User user;
}

class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String username;
    
    @OneToMany(mappedBy = "user")
    List<Order> orders = new ArrayList<>();
}
```

그래서 위와 같은 구조에서..

```java
User user = new User();
user.getorders.add(new Order());
```

비주인 객체의 OneToMany 컬럼에 직접 값을 추가하면 User 객체에만 변경이 저장되고 Order 객체와 DB에는 변경사항이 저장되지 않는다.

```java
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String username;
    
    @OneToMany(mappedBy = "user")
    List<Order> orders = new ArrayList<>();
    
    public void addOrder(Order order) {
        this.orders.add(order);
        order.setUser(this);
    }
}
```

그래서 주인 객체와 비주인 객체의 컬럼을 모두 변경하는 메서드를 제공해야한다.

```java
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    User user;
    
    public void setUser(User user) {
        this.user = user;
        user.getOrders.add(this);
    }
}
```

반대도 마찬가지

---

### mappedBy

이렇게 양방향에서 객체와 DB에 접근해 값을 수정할 수 있지만, 주인/비주인을 명확하게 구분하기 위해서, 그리고 JPA 내부적으로 하나의 엔티티를 관리 중이라는 것을 명확하게 알리기 위해 mappedBy를 사용해야한다.

mappedBy가 정의되지 않은 쪽이 주인객체에 해당하며, 비주인객체는 데이터 조회만 가능하고 수정은 가능하지 않다. 정확히는 수정은 되지만 DB에 반영되지는 않는다. 비주인객체에서도 접근자/수정자 메서드를 통해서 값을 수정하게하는게 아니라, 주인-비주인 간 동기화가 이루어지는 메서드를 제공해야한다. 원칙적으로는 수정/삭제는 주인객체를 통해서만, 조회는 어느 객체나 상관없다.

그래서 양방향 연결의 경우에는 보다 신경써야할 것이 많기 때문에 기본은 단방향 ManyToOne을 쓰는 것이 낫다고..

---

### @JoinColumn으로 컬럼이름 명시하기

- **@JoinColumn**
    - 이 어노테이션으로 FK를 저장할 컬럼의 이름을 명시할 수 있다. 없어도 되지만 그러면 컬럼의 이름이 필드이름_id로 설정된다.

---

## N + 1 문제
![스크린샷 2026-03-21 113747.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-21%20113747.png)
이런 구조를 가진 모델이 있다고 치자.

만약 레포지토리에서 모든 user를 가져오는 쿼리문을 보낸다면, 팀을 가져오는 쿼리 1개에 팀 한 개 당 관계를 가지고 있는 모든 Team 엔티티를 가져온다.

즉, 원래는 유저의 전체 목록을 가져오는 쿼리 1개만 필요했지만, 이 쿼리 때문에 N개의 유저가 가지고있는 모든 team 엔티티를 가져오는 쿼리 N개가 추가로 실행되는, 따라서 N+1개이 쿼리가 생성되는 문제가 생긴다.

---

### 지연로딩으로는 해결할 수 없다

```java
@ManyToOne(fetch = FetchType.EAGER)
private Team team;
```

ManyToOne 어노테이션에서 로딩에 대한 설정을 만질 수 있는데, 만약 EAGER로 설정한 경우 모든 유저 목록을 가져오면 그에따라 team 엔티티도 즉시 가져온다. 그래서 **즉시로딩**이라고한다. ManyToOne 어노테이션은 즉시로딩이 기본값이다. 어차피 ManYToOne이기 때문에 가져올 데이터가 상대적으로 적기 때문이다.

```java
@ManyToOne(fetch = FetchType.LAZY)
private Team team;
```

반대로 LAZY로 설정한 경우 엔티티를 실제로 사용하기 전까지 DB에서 엔티티를 로딩하지 않는다. 즉, 위와같이 설정하면 user 엔티티를 불러올 때 team 객체를 따로 사용하지 않는 이상 team 엔티티를 조회하지 않는다. 이를 **지연로딩**이라고하며, OneToMany 어노테이션은 지연로딩이 기본이다. OneToMany기 때문에 가져올 데이터가 상대적으로 많기 때문이다.

그래서 지연로딩을 사용한다면 N + 1 문제를 방지할 수 있을 것 같지만, 사실 지연로딩을 사용하더라도 불러온 엔티티를 사용하는 즉시 N개의 엔티티마다 관계를 가지고 있는 모든 엔티티를 가져오는 N개의 쿼리가 실행된다. 즉, 지연로딩은 N + 1 문제를 엔티티를 불러올 때만 일어나지 않도록 미루는 것과 비슷하다.

---

### 해결법1: fetch join
![스크린샷 2026-03-21 113811.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-21%20113811.png)

데이터베이스 내에서 여러 테이블의 값을 조합해 하나의 결과로 보여주는 것을 join이라고 칭한다.

```java
@Query("select u from User u **left join fetch** u.team")
List<User> findUserFetchJoin()
```

레포지토리에 fetch join용 메서드를 추가하면 된다.

- **left join**

    ```java
    UserA | TeamA
    UserS | TeamB
    UserT | TeamB
    UserW | TeamC
    UserC | null
    ```

    - 왼쪽 테이블을 기준으로 오른쪽 테이블과 합친다. 왼쪽 테이블에 있는 모든 컬럼들이 결과값에 표시되게되고, 왼쪽테이블에 매칭되는 오른쪽테이블이 없다면 null로 처리한다.

따라서 1개의 쿼리에 N개의 쿼리가 더해지던 기존 쿼리문을 1개로 처리할 수 있게되면서 N+1 문제를 해결할 수 있다.

```java
@Query("select t from Team t left join fetch t.users")
List<Team> findAllFetchJoin();
```

OneToMany 관계에서도 fetch join을 사용할 수 있다.

```java
TeamA | UserA
TeamB | UserS
TeamB | UserT
TeamC | UserW
TeamD | null
```

필요에 따라서 fetch join을 잘 사용해야할 듯하다.

가장 대중적으로 사용되면서 쿼리문 한 개로 간단히 N + 1 문제를 해결할 수 있지만 하나하나 JPQL 문을 짜야한다는 단점도 있다.

---

### 해결법2: EntityGraph

```java
@EntityGraph(attributePaths = {"team"}, type = EntityGraph.EntityGraphType.LOAD)
Optional<User> findWithAddressesById(Long userId);
```

fetch join 기능을 어노테이션으로 만들어 제공한다. attributePath에 지정된 테이블 이름을 자동으로 fetch join으로 가져온다. type갑ㅅ은 EntityGraph.EntityGraphType 중에서 LOAD는 attributePaths에 명시된 테이블만 EAGER, 나머지는 LAZY로 가져오는 것이고, 이외에 FETCH가 있는데, 이는 명시된 attribute만 EAGER, 나머지는 기본설정값으로 가져오는 방식이다.

JPQL 쿼리를 만들지 않아도된다는 장점이 있으나 쿼리 설정이 꼬이면 다소 머리아파진다는 단점이 있다.

---

### 해결법3: Batch Size 설정하기

Batch Size는 한 번의 쿼리에서 가져오는 데이터의 최대값이다. 10이라면 한 번에 최대 10개의 데이터를 가져오고, 100이라면 100개를 가져온다.

그래서 N + 1개의 쿼리를 1개의 쿼리로 바꾸는 위의 방식과 달리 N + 1 개의 쿼리를 시스템이 감당 가능한 쿼리로 쪼개서 보내는 방식이다.

```java
@ManyToOne(fetch = FetchType.LAZY)
@BatchSize(size = 100)
private Team team;
```

```java
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100
	...

```

전역적으로 설정할 수도 있고, 지역적으로 설정할 수도 있다.

일반적으로 100~1000 이내의 값이 사용되며, 너무 작으면 쿼리가 많이 보내져야하고, 너무 크다면 한 쿼리에 가져오는 요청이 매우 많아지므로 적절한 값을 선택해야한다.

---

### **MultipleBagFetchException**

N + 1 문제를 해결하기 위해서 fetch join을 널리 사용하는 편이지만, 특정 환경에서는 문제가 발생한다.

```java
class Movie {
    ...
    @OneToMany(mappedBy = "movie")
    private List<Comment> comments;

    @OneToMany(mappedBy = "movie")
    private List<ActorInfo> actors;
    ...
}
```

하나의 테이블에서 다른 테이블과 OneToMany, 또는 ManyToMany 관계를 2개 이상 맺고 있는 경우, fetch join을 할 수 없다.

만약 Movie 엔티티를 가져오려고했을 때, 그냥 가져온다면 N + 1 문제가 발생할 것이고, 이를 방지하기위해서 fetch join을 사용한다고하면

```java
@Query("select m from Movie m left join fetch m.comments left join fetch m.actors")
List<Movie> findAllMovie();
```

이렇게 fetch join 을 사용하면 N + 1 문제를 해결할 수 있지만 MultipleBagFetchException 오류가 발생한다.

JPA와 자바는 객체 기반이고, DB는 행렬 중심이기 때문에, 하이버네이트가 중간에 DB 탐색결과를 자바의 객체구조로 변환해야한다.

```java
//join을 사용한 DB 탐색결과
//Hibernate는 카테시안 곱을 사용해서 DB 탐색결과를 가져온다
movie: m1, comment: c1, c2, actor: a1, a2, a3
(m1, c1, a1) / (m1, c1, a2) / (m1, c1, a3)
(m1, c2, a1) / (m1, c2, a2) / (m1, c2, a3)

↓

//JPA에서 사용하는 값
m1
comments: c1, c2
actor: a1, a2, a3
```

하이버네이트는 탐색결과를 Bag이라는 자료구조를 통해 가져오는데, Bag은 순서가 없고 중복을 허용하는 자료구조다. 따라서 Bag이 중복을 구분하지 못하고 자바에서 사용할 객체구조로 변환하지 않으며 에러를 발생시킨다.

그래서 MultipleBagFetchException을 터뜨리지 않으며 N + 1 문제를 해결하기 위해서는 join fetch 및 이를 기반으로 동작하는 EntityGraph를 사용하지 못하며, BatchSize를 통해서 해결할 수 있다.

---

### fetch와 pagination 문제

- **pagination은?**
    - 성능 상의 이점을 위해 SQL의 limit절 등을 사용하여 가져온 데이터를 여러 페이지로 나누는 방법이다.

```java
HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
```

OneToMany 관계에서 fetch를 사용한 후, pagination을 사용할 경우 fetch를 통해 연관관계 테이블을 전부 메모리에 싣고 pagination이 동작한다. 이 과정에서 pagination이 DB가 아니라 애플리케이션 계층에서 동작하기 때문에 성능오류가 발생할 수 있어 위의 오류가 발생한다.

---

### DTO와 fetch join

```java
queryFactory
    .select(qMember)
    .from(qMember)
    .leftJoin(qMember.team, qTeam)
    .fetchJoin()
    .fetch();
```

JQPL 또는 QueryDSL에서는 leftJoin(SQL에서 여러 테이블의 값을 하나로 합치기)과 fetchJoin(연관된 엔티티를 하나의 쿼리로 조회) 기능이 있다.

```java
List<MemberOrderDto> result = query
    .select(new MemberOrderDto(
        member.id,
        member.name
        order.ids
    ))
    .from(member)
    .leftJoin(member.orders, order)
    .fetchJoin()
    .fetch();
```

하지만 JPQL에서 fetchJoin을 수행하기 위해서는 엔티티를 select 해야한다.

```java
query specified join fetching, but the owner of the fetched association was not present in the select list
```

다만 DTO를 select하는등 엔티티가 제대로 select되지 않으면 위 에러가 발생한다. 이를 해결하기 위해서는 순수 join만 사용하고 fetch는 직접해야한다.

---

# REST와 RESTful API

## REST란?

**RE**presentational **S**tate **T**ransfer의 약어

HTTP 프로토콜의 장점을 극대화하고자 대두된 클라이언트-서버 간 설계 스타일. 모든 자원을 표현(Representation)으로 구분해 자원의 상태(정보, state)를 주고 받는(transer) 모든 과정을 REST하다고한다. REST를 따르는 API를 RESTful한 API라고 칭한다.

### REST 3요소

- **자원(Resource)** → URL
- **행위(Verb)** → HTTP METHOD
- **표현(Representations)** → 자원을 표현하는 방법. 주로 HTTP 요청 또는 반환에서 JSON데이터가 담당

### REST 특징

1. **클라이언트 - 서버 구조**

   서버는 데이터만 내려주고 클라이언트가 데이터를 적절히 배치하는 구조. REST 서버는 비즈니스 로직을 책임지며, 클라이언트 측에서는 서버의 데이터를 유저에게 보여주는 역할만을 담당해 상호의존성이 줄어든다.

2. **무상태성(stateless)**

   기존 HTTP 인프라를 그대로 사용하기 때문에 HTTP 프로토콜의 장점을 REST에서도 활용할 수 있다.

   HTTP 요청에는 별다른 데이터를 저장하지 않기 때문에 클라이언트의 상태나 정보를 고려하지 않고 HTTP 요청만 처리하기 때문에 간단하다.

3. **유니폼 인터페이스**

   어느 플랫폼에서든 HTTP 프로토콜만 사용한다면 RESTful API를 설계, 구현, 사용가능하다. 따라서 특정 플랫폼에 종속되지 않고 널리 사용가능하다.

4. **캐시(Cacheable)**

   캐시와 쿠키 기능으로 인해 성능개선도 가능하다.


### 세부규칙

```java
GET movies ⭕
GET get/movies ❌
```

1. URI는 자원의 이름을 표시하기 위해 사용하며, 동사형은 잘 사용하지 않는다. 자원은 복수형을 주로 사용한다.

```java
GET users/students ⭕
GET users/students/ ❌
```

1. 계층관계를 위해서는 / (슬래시)를 사용한다. URI의 분명한 시작과 끝을 위해 마지막에는 슬래시를 붙이지 않는다.

```java
POST movie_comments ❌
POST movie-comments ⭕
```

1. URI 주소가 길어진다면 - (하이픈)을 주로 사용하며, _ (언더바)는 사용하지 않는다.

```java
POST document.hwp ❌
POST document ⭕
```

1. 파일 확장자는 URI 주소에 포함하지 않는다.

### HTTP 반환메서드

| 100
Continue | 요청이 서버에 도달했으며 클라이언트가 계속해서 요청을 보내도됨 |
| --- | --- |
| 101
Switching Protocol | 클라이언트가 보낸 프로토콜 변경 요청을 승인함
HTTP 1.1에서 웹소켓으로 프로토콜을 변경할 때 이 응답을 반환한다. |
| 102
Processing | 서버가 요청을 처리하고 있지만 아직 값을 반환할 수 없음 |
| 103
Early Hints | 서버가 응답을 준비하는동안 사용자가 사전로딩을 가능하게함 |

**100번대:** 요청을 받았고 현재 수행 중인 상태

| 200
OK | 요청성공 |
| --- | --- |
| 201
Created | 주로 POST 요청에 대해 반환되는 성공응답
요청이 성공적으로 처리되었으며 새로운 리소스가 생성됨 |
| 202
Accepted | 요청을 처리했으나 요청에 응답할 수 없음
현재 요청이 트래픽 과다, 응답시간 초과 등으로 수행될 수 없거나 비동기로 처리될 경우 |
| 203
Non-Authoritative information | 요청처리 성공
클라이언트가 수신한 정보가 서버가 아니라 다른소스에서 제공됨 |
| 204
No Content | 클라이언트 요청이 정상적으로 처리됐으나 반환할 바디가 없음
헤더값에는 의미있는 정보가 있을 수 있다. |
| 205
Rest Content | 클라이언트 요청이 정상적으로 처리됐으나 반환할 바디가 없음
클라이언트 측의 새로고침을 요구한다. |
| 206
Partial Content | 클라이언트 요청의 일부분만 반환함 |
| 207
Multi-Status | 응답 바디가 여러 개 혼합되어 사용됨 |
| 208
Already Reported | 이미 앞에서 열거됨 |
| 226
IM used | 서버가 GET 요청에 대한 의무를 다했음
HTTP Data Encoding 전용 |

**200번대**: 요청을 정상적으로 수행함

| 300
Multiple Choices | 응답이 여러 개 있음 |
| --- | --- |
| 301
Moved Permanently | 컨텐츠가 영구적으로 이동함
영구적 리다이렉트함. HTTP 메서드를 변경하지 않음 |
| 302
Found | 다른 URL에서 리소스를 찾음
해당 URL로 이동하지만 기존 HTTP 요청이 유지되거나 변경 |
| 303
See Other | 다른 URL에서 리소스를 찾음
해당 URL로 GET 요청이 자동으로 보내짐 |
| 304
Not Modified | 마지막 요청 이후 페이지가 수정되지 않으며, 페이지를 표시하지 않음
브라우저에 캐시된 정보를 사용함 |
| 307
Temporary Redirect | 현재 서버가 다른 서버에 요청하고 있으나 클라이언트는 향후 요청 시 원래 위치를 계속 사용해야함. 302와 비슷하지만 HTTP 메서드의 변경을 불허한다. |
| 308
Permanent Redirect | 컨텐츠가 영구적으로 이동함
301과 비슷하지만 HTTP 메서드의 변경을 불허한다. |

**300번대**: 리소스가 다른 곳으로 이동함. 추가적인 작업 필요

| 400
Bad Request | 잘못된 요청. 주로 문법오류 등 |
| --- | --- |
| 401
Unauthorized | 인증되지 않은 사용자 |
| 403
Forbidden | 인증에는 성공했으나 인가에는 실패함. 권한이 부족 |
| 404
Not Found | 요청한 페이지 또는 자원이 존재하지 않음 |
| 405
Method Not Allowed | HTTP 주소가 있으나 잘못된 메서드 |
| 406
Not Acceptable | 정상요청이나 방화벽 등으로 정보를 제공하지 못함 |
| 407
Proxy Authentication Required | 프록시 인증 필요 |
| 408
Request Timeout | 요청 시간 초과 |
| 409
Conflict | 클라이언트의 요청과 서버가 충돌함 |
| 410
Gone | 리소스 영구삭제
404와 비슷하지만 410은 리소스가 영구히 삭제됨을 의미 |
| 411
Length Required | 요청메시지에 Content-Length 요구 |
| 412
Precondition Failed | 클라이언트가 서버 측으로 조건부 요청을 보냈는데 조건이 맞지 않는 경우 |
| 413
Payload Too Large | 요청 본문이 서버의 한계보다 긴 경우. nginx 환경에서 크기가 큰 첨부파일을 업로드하면 413 에러가 발생한다. |
| 414
URI Too Long | URI가 매우 김 |
| 415
Unsupported Media Type | 클라이언트의 미디어타입과 서버의 타입이 다르다 |
| 416
Range Not Satisfiable | 요청 헤더의 Range 범위가 잘못됨 |
| 417
Expectation Failed | 요청 헤더의 Expect 값이 잘못됨 |
| 421
Misdirected Request | 의도하지 않은 요청을 받아 서버가 처리할 수 없음 |
| 422
Unprocessable Entity | 정상적인 요청이나 요청된 지시를 따를 수 없음 |
| 423
Locked | 요청의 대상이 되는 폴더 또는 파일이 잠김 |
| 424
Failed Dependency | 이전 요청이 실패하여 현재 요청도 실패함 |
| 426
Upgrade Required | 클라이언트와 서버의 HTTP 프로토콜이 일치하지 않아 요청을 처리할 수 없으나, 업그레이드 시 요청을 처리할 수 있는 경우 |
| 428
Precondition Required | 조건부 요청이 요구됨 |
| 429
Too Many Requests | 클라이언트가 너무 자주 요청을 보낸 경우 |
| 431
Request Header Fields Too Lagrge | 헤더 필드가 너무 큼 |
| 451
Unavailable For Legal Reasons | 법적인 이유로 접근이 불허됨 |

**400번대**: 잘못된 요청

| 500
Internal Server Error | 서버 내부 오류 |
| --- | --- |
| 501
Not Implemented | 서버가 해당요청에 대한 기능을 지원하지 않음 |
| 502
Bad GateWay | 게이트웨이에 문제가 생겨 서버에서 잘못된 응답을 받음 |
| 503
Service Unavailable | 일시적 서비스 이용불가 |
| 504
Gateway Timeout | 게이트웨이 시간초과 |
| 505
HTTP Version Not Supported | 서버에서 지원하지 않는 HTTP 버전 |
| 이외에 506 ~ 511 응답코드 존재 |  |

**500번대**: 서버 에러

---

## RESTful API의 단점과 대안

REST 방식을 사용하면 상황을 구분하지 않고 같은 HTTP 요청이라면 같은 값을 반환한다. 따라서 필요한 값보다 더 많이 데이터를 반환하거나(오버페치), 필요한 값보다 더 적은 데이터를 반환해 여러 번 HTTP 요청을 보내야하는 경우가 발생한다.(언더페치)

1. **RESTful API 수정**

    ```java
    GET /posts   //모든 게시글에 대한 정보를 받음
    GET /posts?fields = content, writer    //게시글 내용과 작성자의 정보만 받음
    ```

   오버페치: 쿼리파라미터를 이용하여 원하는 값만 필터링해 받는다.

    ```java
    GET /posts
    GET /users/{id}
    //모든 게시글의 정보를 가져온 후 유저정보도 가져오도록 요청을 두 번 보내야함
    
    GET /posts?include = user  //게시글 내용과 작성자 정보를 받음
    ```

   언더페치치: 쿼리파라미터를 통해 엔드포인트를 수정해 값을 추가로 반환받도록한다.


1. **graphQL**

graphQL은 쿼리언어로, 클라이언트가 필요한 데이터를 요청사항에 명시할 수 있다. 따라서 위의 경우처럼 복잡하게 요청을 보내야하지는 않고 빠르게 원하는 데이터를 가져올 수 있다.

```java
type Query {
    getBookById(id: ID!): Book
    // 추가
}

type Book {
    id: ID!
    title: String
    author: String
}
```

graphQL 스키마

```java
@Controller
public class BookController {

  private static Map<String, Book> books = new HashMap<>();

  static {
    books.put("1", new Book("1", "Harry Potter and the Philosopher's Stone", "J.K. Rowling"));
    books.put("2", new Book("2", "The Hobbit", "J.R.R. Tolkien"));
  }

  @QueryMapping
  public Book getBookById(@Argument String id) {
    return books.get(id);
  }
}
```

컨트롤러

@QueryMapping 어노테이션을 통해서 쿼리를 지정할 수 있다. 스키마에서 원하는 정보를 볼 수 있고, 요청은 스키마에 맞게 보내야한다. 따라서 REST API와 다르게 직접 어느 정보를 가져올지 정할 수 있지만, 스키마를 직접 짜야하고 API Body 형식도 이에 맞춰야하는 다소 번거로운 점이 있다.

```java
{
  getBookById(id: "1") {
    id
    title
    author
  }
}
```

반환값
=======
# spring-cgv-23rd
CEOS 23기 백엔드 스터디 - CGV 클론 코딩 프로젝트
>>>>>>> d73856fc6ece59c682c38972ad80203e96592d2a
