# spring-cgv-23rd
CEOS 23기 백엔드 스터디 - CGV 클론 코딩 프로젝트

--- 
## 1. 서비스 개요
   본 프로젝트는 다음과 같은 핵심 기능을 지원합니다.

* **영화 및 상영 일정**: 영화 정보 관리 및 영화관별 상영 스케줄 제공.

* **예매 시스템**: 사용자별 좌석 선택 및 예매 상태 관리.

* **관심 서비스**: 사용자 중심의 영화 및 영화관 '찜' 기능.

* **스토어(매점)**: 영화관별 상품 재고 관리 및 주문·결제 시스템.

---

## 2. ERD

<img width="1101" height="598" alt="Image" src="https://github.com/user-attachments/assets/d505ca6a-bcad-434c-93e6-bc2cf955fe3c" />

--- 

## 3. 상세 모델링 설명
   데이터 모델은 크게 영화 예매 도메인, 사용자 활동 도메인, 스토어 도메인의 세 파트로 구분됩니다.

---

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
