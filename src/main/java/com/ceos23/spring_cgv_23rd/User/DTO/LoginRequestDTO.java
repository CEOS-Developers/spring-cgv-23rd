package com.ceos23.spring_cgv_23rd.User.DTO;

import org.springframework.transaction.annotation.Transactional;

public record LoginRequestDTO(
        String loginId,
        String password
) {


}
