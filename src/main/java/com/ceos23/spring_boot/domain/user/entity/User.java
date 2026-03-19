package com.ceos23.spring_boot.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_USER_LOGIN_ID", columnNames = {"login_id"}),
        @UniqueConstraint(name = "UQ_USER_EMAIL", columnNames = {"email"})
})
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", nullable = false, length = 50)
    private String phoneNumber;

    @Column(nullable = false, length = 50)
    private String email;
}
