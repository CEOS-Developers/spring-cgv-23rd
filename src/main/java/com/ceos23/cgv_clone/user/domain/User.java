package com.ceos23.cgv_clone.user.domain;

import com.ceos23.cgv_clone.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 100, unique = true)
    private String email;

    private LocalDate birthdate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @Builder
    public User(String nickname, String email, LocalDate birthdate) {
        this.nickname = nickname;
        this.email = email;
        this.birthdate = birthdate;
    }
}
