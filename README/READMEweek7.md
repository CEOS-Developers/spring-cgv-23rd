# CGV 아키텍처 구조도
![alt text](image-2.png)

# 부하테스트 결과분석
테스트는 K6을 가지고 영화예약 → 결제 → 취소를 반복하는 작업을 수행했다.

![alt text](image-3.png)
Overview
## Request Rate
Reqeust Rate가 꾸준히 증가하고 있고 테스트가 끝나갈 때 쯤 내려온다.
Rate가 갑자기 훅 꺼지는 구간이 있는데, 이는 DB 커넥션 풀 고갈 또는 GC 등이 원인으로 추측된다.

## Request Duration p(95)
Request Rate가 꾸준히 증가할 때는 최대 700㎲ 정도지만, Request Rate가 감소할 때 최대 7초까지 증가한다. 이는 DB 커넥션풀 고갈 때문에 DB에 접근할 수 없어 대기해야함이 원인이라고 추측된다. 커넥션풀이 다시 생기고 RequestRate가 증가하자 다시 감소하는 경향을 보인다.

## Request Failed
꾸준히 증가하는 모습을 보이는데, 이는 영화예약 과정에서 이미 선택된 좌석으로 인한 400 에러가 다수의 비중을 차지한다.

![alt text](image-7.png)
이 지표를 보면 2시 8분 45초 경을 제외하면 완만하게 증가하다가 감소하는 것을 볼 수 있는데, 이는 2시 8분 45초 경의 DB 에러를 제외하고 예약된 좌석수들이 늘어남에 따라 이미 선택된 좌석임 때문에 발생한 400에러가 대다수를 차지하고, 테스트가 끝나감에따라 다시 줄어드는 것을 볼 수 있다.

## 다른 지표
![alt text](image-4.png)
![alt text](image-5.png)
![alt text](image-6.png)
다수의 지표에서 위에서 중간에 Request가 끊긴 지점에서 대기시간이 크게 늘었음을 보인다.

## 해결(?) 
![alt text](image-8.png)
![alt text](image-9.png)
서버를 재시작하니 Request Duration이 2초 아래로 줄어들었다. 아마도 2시 8분 경의 오류는 GC 때문으로 추측된다.
2시 26분 경 Duration이 확 튀는 구간은 VU 수가 증가하면서 생긴 현상으로, 이후 안정화되었다.


# 모니터링 실습
![alt text](image-1.png)
5분 간 일어나고 있는 에러의 양, 현재 트래픽의 수, 에러로그와 모든 로그를 한 눈에 볼 수 있도록 커스텀하였다.

