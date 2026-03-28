package com.ceos.spring_boot.domain.user.repository;

import com.ceos.spring_boot.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 가입된 유저가 있는지 확인 (회원가입 중복 체크용)
    boolean existsByEmail(String email);

    // 이메일로 유저 정보 가져오기 (로그인 시 사용)
    Optional<User> findByEmail(String email);

}
