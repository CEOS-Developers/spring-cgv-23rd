package com.ceos23.spring_cgv_23rd.global;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class health {

    private DataSource dataSource;

    /**
     * CD 이후 서버가 정상적으로 동작하는지 파악하기 위한 엔드포인트입니다.
     */
    @GetMapping("/ready")
    public ResponseEntity<?> healthChecking() {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(2)) {
                throw new RuntimeException("DB not valid");
            }
        } catch (Exception e){
            return ResponseEntity.status(500)
                    .body(Map.of("status", "DOWN"));
        }

        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
