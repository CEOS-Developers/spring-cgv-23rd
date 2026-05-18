# Cache

## 📌 캐시 도입 위치

| 도메인     | 캐시 키             | TTL | 데이터 특성   |
|:--------|:-----------------|:----|:---------|
| Movie   | `movie:chart`    | 10분 | 전체 영화 차트 |
| Movie   | `movie:running`  | 10분 | 현재 상영작   |
| Movie   | `movie:upcoming` | 10분 | 상영 예정작   |
| Movie   | `movie:detail`   | 10분 | 영화 상세    |
| Movie   | `movie:credits`  | 6시간 | 영화 출연진   |
| Movie   | `movie:medias`   | 6시간 | 영화 미디어   |
| Theater | `theater:list`   | 6시간 | 영화관 목록   |
| Theater | `theater:detail` | 6시간 | 영화관 상세   |

## 📌 캐시 전략

### ✅ Look-Aside + Write Around

| 구분      | 동작                                               |
|:--------|:-------------------------------------------------|
| `Read`  | Application → Redis 조회 → miss 시 DB 조회 후 Redis 적재 |
| `Write` | DB만 갱신 (캐시는 TTL 만료로 자연 무효화)                      | 

**선정 근거**

- 캐시 장애 시에도 DB로 fallback 가능
- 영화/극장 admin이 없는 현재 구조에서 명시적 `@CacheEvict`보다 TTL 정책이 단순
- Write Through/Back은 거의 안 읽힐 데이터까지 캐시하므로 과한 정책

## ❓ 트러블슈팅

### Jackson3

Spring Boot 4부터는 공식 문서에서 Jackson3의 사용을 권장함에 따라, Jackson3 기반으로 직렬화/역직렬화를 구현했는데 자료가 많이 없어서 애를 먹었다.

`List<Result>` 직렬화/역직렬화 실패

처음 GenericJacksonJsonRedisSerializer + default typing 조합으로 시도했으나, List<MovieResult> 같은 최상위 컬렉션의 polymorphism 처리에서
write/read 형식 불일치가 발생했다.

```bash
Write: [{"@class":"MovieResult",...}, ...]             ← 외곽 wrapper 없음
Read:  ["typeId", [...]] 형식 기대                       ← 불일치 -> 에러
```

`DefaultTyping`을 `NON_FINAL`, `NON_FINAL_AND_RECORDS` 등으로 바꿔봤으나 모두 같은 패턴으로 실패.
`GenericJacksonJsonRedisSerializer`의 내부 `TypeResolverBuilder`가 표준 Jackson 동작과 미묘하게 다른 게 원인으로 보였다.

이는 명시적으로 타입을 지정해서 해결했다.

`JacksonJsonRedisSerializer<T>` + 캐시별 `JavaType` 명시 방식으로 전환

```bash
// CacheConfigurationFactory 
public RedisCacheConfiguration create(Duration ttl, JavaType valueType) {
    return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl)
            .disableCachingNullValues()
            .serializeKeysWith(...)
            .serializeValuesWith(...
                .fromSerializer(new JacksonJsonRedisSerializer<>(mapper, valueType)));
}


// MovieCacheCustomizer 
JavaType movieListType = typeFactory.constructCollectionType(List.class, MovieResult.class);

builder
  .withCacheConfiguration("movie:chart", configFactory.create(Duration.ofMinutes(10), movieListType))

```

결과 :

```bash
[{"id":1,"title":"인터스텔라",...}, {"id":2,...}]
```

<details>
  <summary>참고 자료</summary>

- [Jackson 3 Migration Guide](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md)
- [Spring Blog — Jackson 3 Support](https://spring.io/blog/2025/10/07/introducing-jackson-3-support-in-spring)
- [Spring Data Redis Jackson Serializer 문서](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/serializer/Jackson2JsonRedisSerializer.html)

</details>

### TTL 정책

`movie:chart`, `movie:running`, `movie:upcoming`, `movie:detail`을 모두 10분으로 통일했다.

이유: `MovieResult`와 `MovieDetailResult`에 `reservationRank`(예매율 순위) 등 통계 필드가 포함되어 있다.  
만약 각 캐시의 TTL이 다르면 영화 차트(`moive:chart`)의 순위와 영화 상세 정보(`move:detail`)의 순위가 다를 수 있다.

원래는 순위(통계) 데이터만 짧은 TTL로 분리하는 게 이상적이지만, 현 시점에서는 다음 이유로 보류:

- MovieStatistic을 별도 캐시로 분리하려면 도메인 모델, Result DTO, Service, Controller, Mapper, Response 등 전반적인 리팩토링 필요
- 현재 단계에서는 오버엔지니어링으로 판단
- 동일 TTL 통일만으로도 일관성 보장 가능

# Logging

## 📌 MDC

| MDC 키     | 설정 위치                   | 범위         |
|:----------|:------------------------|:-----------|
| traceId   | `RequestLoggingFilter`  | HTTP 요청 전체 |
| paymentId | `PaymentCommandService` | 결제 처리 흐름   |

-> 모든 로그에 traceId가 자동 부착되고, 결제 영역에서는 paymentId까지 추가된다.

```bash
{traceId=abc12345} Request received. method=POST, uri=/api/v1/payments/order_123/instant
{traceId=abc12345, paymentId=order_123} Payment started. orderName=노트북외 1건, amount=15000
{traceId=abc12345, paymentId=order_123} Calling PG pay API. amount=15000
{traceId=abc12345, paymentId=order_123} Payment marked PAID in DB. pgProvider=KAKAO_PAY
{traceId=abc12345, paymentId=order_123} Payment completed
{traceId=abc12345} Request completed. status=200, durationMs=234
```