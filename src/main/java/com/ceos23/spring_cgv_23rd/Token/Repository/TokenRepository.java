package com.ceos23.spring_cgv_23rd.Token.Repository;

import com.ceos23.spring_cgv_23rd.Token.Domain.RefreshToken;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Query("""
        select rt from RefreshToken rt
        where rt.user = :user
        and rt.used = false
    """)
    List<RefreshToken> findUnexpiredTokenByUser(@Param("user") User user);

    Iterable<RefreshToken> findAllByUser(User user);

    boolean existsByToken(String token);
}
