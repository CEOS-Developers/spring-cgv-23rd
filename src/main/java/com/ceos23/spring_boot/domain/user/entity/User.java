package com.ceos23.spring_boot.domain.user.entity;

import com.ceos23.spring_boot.global.common.BaseSoftDeleteEntity;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_USER_EMAIL", columnNames = {"email"})
})
public class User extends BaseSoftDeleteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Builder
    public User(String name, String email, String password, Role role, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }
}
