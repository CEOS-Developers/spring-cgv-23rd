# 리펙토링

1. `CustomUserDetails.java`
    - **리팩토링 내용**: `private final User user;`를 삭제하고, 인증에 필요한 `id`, `role` 필드만 유지
    - **이유:** CustomUserDetails에 User 엔티티를 저장하게 되면, 클라이언트가 요청을 보낼 때마다 인증객체(Authentication)을 만들기 위해 DB에 Select 쿼리를 날리기 때문에 성능 저하 → 필요한 데이터만 추출하여 저장함으로써 보안 계층과 데이터 계층의 결합도를 낮춤
2. `CinemaService.java`
    1. `createCinema`  → 다른 서비스 계층 코드도 똑같이 리팩토링
        - 리팩토리 내용: 엔티티 내부에 **정적 팩토리 메서드**를 만들어 생성 책임을 엔티티로 옮김
        - 이유: `CinemaService`에서 `Cinema.builder()...build()`를 직접 호출 → 서비스 레이어가 엔티티의 구체적인 생성 방식에 깊게 관여
    2. `findEntityById`
        - 리팩토리 내용: `findEntityById`  메서드를 추가하여 중복 코드 최소화
        - 이유: 코드 가독성
3. `Service` 계층 코드
    - **리팩토링 내용**: 사용자를 `findById`를 통한 엔티티 직접 조회 방식에서 `getReferenceById`를 활용한 프록시 객체 참조 방식으로 변경
    - **이유:**
        - 사용자(User): JWT 인증을 성공한 유저의 요청 → 유저 ID의 유효성이 시스템적으로 보장되므로, SELECT 쿼리 없이 프록시 객체만 생성하여 성능을 최적화
        - 영화(Movie): movieId는 클라이언트가 보낸 외부 입력값 → 존재하지 않는 ID일 가능성이 있으므로, 확실한 검증(SELECT) 후 비즈니스 로직을 수행하는 것이 안전
    - 주의할 점:
        - 실제 존재 여부: `getReferenceById`는 DB를 조회하지 않기 때문에, 만약 존재하지 않는 ID를 넣어도 일단 프록시 객체가 만들짐 → 실제 INSERT 시점이나 데이터를 조회할 때 에러 발생 할 수도.. → User는 인증을 한 번 거치기 때문에 안전하다고 가정
    - 비교
   
      <img width="563" height="230" alt="Image" src="https://github.com/user-attachments/assets/843eb4b8-e474-498a-919d-3ed7b30a7653" />