package cgv_23rd.ceos.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final PaymentProperties paymentProperties;

    @Bean
    public RestTemplate paymentRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(paymentProperties.server().url())
                .additionalInterceptors((request, body, execution) -> {
                    // 모든 결제 요청에 API Secret 헤더를 자동으로 추가
                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + paymentProperties.apiSecret());
                    return execution.execute(request, body);
                })
                .build();
    }
}
