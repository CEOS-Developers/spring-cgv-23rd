## 배포

## 📌CI / CD

### ✅ 구성

| 워크플로우 | 트리거                        | 하는 일                       |
|-------|----------------------------|----------------------------|
| CI    | PR 오픈/업데이트                 | 빌드 & 테스트                   |
| CD    | main 브랜치 push (= PR merge) | 빌드 → DockerHub 푸시 → EC2 배포 |

### 🧠 흐름

**PR 오픈**

- CI 실행
- Checkout
- JDK 21 + Gradle 캐시 설정
- ./gradlew build (컴파일 + 테스트)

**Main Push (PR merge)**

- CI 실행 & 성공
- CD 실행 (workflow_run)
- Checkout (머지된 커밋 SHA 기준)
- ./gradlew bootJar -x test
- Docker Buildx + GHA 캐시로 이미지 빌드
- DockerHub에 push (latest + SHA 태그)
- EC2 SSH 접속
- docker pull (SHA 태그)
- docker compose up -d (컨테이너 교체)
- /actuator/health 헬스체크 (최대 60초 대기)
- 이전 이미지 정리

### 🔒안전장치

- **workflow_run 조건**: CI 성공 + push 이벤트일 때만 배포
- **concurrency 그룹**: `deploy-prod`로 동시 배포 방지
- **헬스체크**: 배포 후 `/actuator/health`를 30회(2초 간격) 확인, 실패 시 배포 실패 처리
- **이미지 태그**: `latest` + git SHA 태깅으로 롤백 가능