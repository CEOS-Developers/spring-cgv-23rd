![스크린샷 2026-03-21 121321.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202026-03-21%20121321.png)
https://www.erdcloud.com/d/vXew5ExzkDE48pxDi

# 구현기능
- 영화관 조회
- 영화 조회
- 영화 예매
- 영화 취소

## 도메인별 구분
### Actor
영화에 들어갈 배우에 대한 정보에 관한 도메인
- Actor: 배우에 대한 정보
- ActorInfo: 다대다 관계를 가지고있는 배우-영화 관계에서 중간테이블 역할
- Prize: 배우 수상경력

### Media
사진 또는 영상에 대한 도메인
- Media: 사진 또는 영상. 추후 MoviePhoto, MovieVideo, ActorPhoto 등 도메인 별로 Photo, Video 테이블로 나눌 계획이 있음

### Movie
영화에 대한 도메인
- AudienceData: 영화예매자들의 나이대별 비율, 성별비율 등 영화예매자들의 메타데이터를 저장하는 테이블
- Comment: 영화에 대한 감상평을 저장하는 테이블
- Movie: 영화를 저장하는 테이블

### Reservation
예약에 대한 도메인
- Reservation: 예약정보저장
- ReservationSeat: 예약한 좌석정보 저장

### Screen
영화관 및 상영정보에 대한 도메인
- Screen: 영화관에 종속된 상영관에 대한 테이블
- Screening: 상영관마다의 상영정보를 담은 테이블

### Theater
영화관에 대한 도메인
- Food: 전 영화관이 공유하는 음식에 대한 테이블
- TheaterMenu: 영화관별 음식. 각 영화관마다 다른 정보 (품절정보 등)을 담은 테이블
- Theater: 영화관

## 구현기능
### 영화관 조회
- 영화관 전체 조회: GET /api/theater
- 특정 영화관 조회: GET /api/theater/{theaterName}

### 영화 조회
- 영화 전체 조회: GET /api/movie
- 특정 영화 조회: GET /api/movie/{movieName}

### 예약
- 영화 예약하기: POST /api/reservation
  - body:
    - userId: 유저정보
    - screeningId: 예약할 상영에 대한 정보(상영관, 영화이름 등..)
    - reservationDate: 관람일
    - totalPrice: 합계가격
    - seatInfos: 예매할 좌석에 대한 정보, seatName(좌석이름), seatInfo(좌석구분)을 저장할 수 있으며, seatInfo에는 ADULT("일반"), CHILD("어린이"), SENIOR("경로")가 있음

### 예약취소
- 예약 취소하기: DELETE /api/reservation/{reservationId}


---

# 개선점
- 사진과 영상에 대한 테이블을 모두 Media 테이블 한 개가 관리함. 추후에 ActorPhoto, MovieVideo, MovieVideo 등 도메인별 사진 및 영상 전용 테이블로 분리할 필요가 있어보임
- 로그인 기능 구현 후 User 테이블 프로퍼티 수정 가능성이 있어보임
- 
