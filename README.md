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
