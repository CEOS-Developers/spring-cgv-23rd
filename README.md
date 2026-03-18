# spring-cgv-23rd
CEOS 23기 백엔드 스터디 - CGV 클론 코딩 프로젝트

## EntityManager와 DB Connection

- **EntityManager**
    - 스프링 컨테이너에 등록되는 것은 **Proxy EntityManager**
        - Proxy EM이 **싱글톤**으로 등록 되는 것
        - 빈들이 DI 받는 **EM은 Proxy EM**!!
            - 결국 하나의 Proxy EM이 모든 빈들에 공유
    - 진짜 **EntityManager**는 트랜잭션마다 **EntityManagerFactory**에 의해 생성됨
        - 클라이언트 요청이 오면 **Proxy EM**이 진짜 **EM**에게 요청 넘김
    - **결론**
        - 스프링이 싱글톤 EM 하나만을 사용하는 것 X
            - 그렇게 되면 여러 사용자의 동시 요청을 병렬적으로 수행 불가..
        - 내부적으로 **트랜잭션마다 진짜 EM이 계속 새로 생성되**며 독립적인 영속성 컨텍스트를 유지
            - **EM**마다 **영속성 컨텍스트** 관리하므로
                - 이로 인해 영속성 컨텍스트 내 데이터가 섞이지 않아, **여러 사용자의 동시 요청도 병렬 수행 가능!!**

- **EntityManager 생성 및 DB 연결** 과정
    1. **서버 시작**
        - 스프링 부트 실행
        - `application.yml`에 적어둔 DB 접속 정보를 바탕으로 `EntityManagerFactory`가 딱 하나 생성
        - **커넥션 풀에 미리 여러 커넥션들이 생성**
            - 스프링 부트에서 DB Connection 풀로 사용하는 **HikariCP** 라이브러리에 의해!!
    2. **사용자 요청 발생**
        - HTTP 요청을 처리하기 위한 **트랜잭션** 시작
    3. 빈들에게 주입된 `Proxy EntityManager`가 호출되고, 해당 요청에 할당된 진짜 `EntityManager`에게 요청 넘기기 위해 `EntityManagerFactory` 호출
    4. `EntityManager` 생성
        - `EntityManagerFactory`가 해당 요청을 처리할 `EntityManager` 생성
            - 즉 HTTP 요청마다 새로운 `EntityManager`가 생성됨!!
        - **아직 DB 커넥션을 가져오지 않음**
            - **지연된 커넥션 획득 전략** (JPA 최적화 전략)
                - 메모리(영속성 컨텍스트)에서 할 수 있는 일은 최대한 커넥션 없이 처리
    5. **비지니스 로직** 수행
        - `EntityManager`가 영속성 컨텍스트를 통해 로직 수행
            - **DB Connection** 사용 안하는 작업
                - `em.persist(member)` 호출
                    - 쓰기 지연 SQL 저장소에 쿼리 저장
    6. **DB 통신**
        - DB에 쿼리를 날려야 하는 시점에 **커넥션 풀**에서 DB Connection 획득
            - 트랜잭션 커밋 직전
            - `em.flush()`
            - JPQL 실행 전
    7. **쿼리 전송**
        - 영속성 컨텍스트에 쌓아둔 SQL을 DB로 모두 전송
    8. **요청 종료**
        - 트랜잭션이 **Commit**되고, 해당 `EntityManager`는 메모리에서 사라지고, **DB Connection**을 풀에 반납

### 컬렉션 페치 조인

![](images/table.png)

- **컬렉션 페치조인에서 영속성 컨텍스트 동작 흐름**
    - **팀 테이블**에 1개 저장된 `팀 A` 조회 시, 연관된 `회원1`과 `회원2`를 **페치조인**하는 경우
        
        ```jsx
        public interface TeamRepository extends JPARepository<Team, Long> {
        		@EntityGraph(attributePaths = { "members" })
        		List<Team> findByName(String name);
        }
        ```
        
    1. **반환된 team 결과 리스트**
        - DB에서 1줄이 아니라, **2줄을 받음**
            - 일대다 관계이므로, **데이터 뻥튀기**
        - 결과 리스트(`List<Team>`)의 크기는 **2**
    2. **리스트에 객체 저장**
        - 첫 번째 줄을 읽어 `Team A` 객체 생성
        - 두 번째 줄을 읽을 때, 동일 Id 확인 (1차 캐시에 이미 저장)
            - 이미 생성된 객체의 주소 재활용
        - 결국 **리스트의** 첫 번째 요소와 두 번째 요소 모두 동일 객체 가리킴
    3. **리스트 출력**
        - 동일한 객체가 2번 출력
    
    ```jsx
    List<Team> teams = teamRepository.findByName("TEAM A");
    
    for(Team team : teams) { 
        System.out.println("teamname = " + team.getName()); 
        for (Member member : team.getMembers()) { 
          //페치 조인으로 팀과 회원을 함께 조회해서 지연 로딩 발생 안함 
          System.out.println("username =" + member.getUsername()); 
      } 
    }
    
    teamname = 팀A 
    username = 회원1 
    username = 회원2
    teamname = 팀A 
    username = 회원1 
    username = 회원2 
    ```
    
    - 중복 문제 해결 방법
        - **distinct**
            
            ```jsx
            public interface TeamRepository extends JPARepository<Team, Long> {
            		@EntityGraph(attributePaths = { "members" })
            		List<Team> findDistinctByName(String name);
            } 
            ```
            
            - **애플리케이션 레벨에서 중복 제거**
                - **Hibernate**가 같은 식별자인 Team 엔티티 제거하여, 리스트에 한 번만 저장됨
            - **DB 레벨**에서 중복 제거 안됨
                - SQL의 데이터는 같지 않으므로
        - **Hibernate6**부터 **distinct 명령어** 사용하지 않아도 자동으로 애플리케이션 레벨에서 중복 제거 해줌
            - 알아서 식별자를 보고 중복 제거
            - DB 레벨에서는 여전히 뻥튀기된 데이터가 전달
        - 그럼에도 **distinct** 명시적으로 작성하는 것을 추천!!
            - 자동 중복 제거는 JPA가 아닌 Hibernate의 기능이므로 다른 구현체로 바꾸면 중복 제거 안됨..
            - 중복 없이 가져온다는 것을 명시적으로 표현 가능

- **컬렉션 페치조인의 한계**
    - **둘 이상의 컬렉션 페치조인 불가**
        - 컬렉션마다 **카르테시안 곱** 발생
            - 팀 1개 * 멤버 10명 * 주문 10개 = 100개 행..
        - 둘 이상의 컬렉션 중복부터는 **Hibernate**가 중복 제거 불가능
            - `org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags`
            
    - **페이징 불가**
        - **데이터 유실 문제**
            - **일대다 페치조인**하면, 위의 경우처럼 **데이터 뻥튀기 발생**
                - 즉 DB 결과로 `팀A`가 두 줄로 반환
            - 뻥튀기된 데이터에 **페이징 걸면 데이터 유실 가능**
                - `limit 1`을 걸면 DB가 첫 줄만 보내게 됨
                - **DB 레벨**에서는 어디까지가 하나의 팀인지 모름
        - **Hibernate**의 작동 방식
            - 이러한 데이터 유실을 막기 위해 **뻥튀기된 데이터를 모두 갖고 와 메모리에서 페이징** 시도..
                - **OutOfMemory** 에러 발생 가능
                - **`firstResult/maxResults specified with collection fetch; applying in memory!`**

- **Batch Fetch**로 해결!!
    - 우선 **지연로딩으로** 가져오고, 연관된 엔티티를 필요할 때 **in 쿼리로** 여러개 가져오기
        - **페치조인** 사용 안하므로 위 문제들 발생 X
        - 페치조인 사용 안해도 **N+1** 문제 발생 X
    - **application.yml** 설정
        
        ```jsx
        jpa:
        	properties
        		hibernate:
        			default_batch_fetch_size: 100
        ```
        
    - 동작 방식
        
        ```jsx
        // Team만 조회 (페치 조인 X)
        List<Team> teams = teamRepository.findByName("TEAM A");
        
        // 지연 로딩 발생
        for (Team t : teams) {
            t.getMembers(); // in 쿼리 실행
        }
        ```
        
        - `IN` 절을 사용해서 데이터를 한 번에 미리 설정값만큼 가져옴
            - `SELECT * FROM MEMBER WHERE TEAM_ID IN (1, 2, 3, ..., 100)`
        - N+1 문제가 발생하지 않고, `1 + 1` 수준으로 최적화 가능
