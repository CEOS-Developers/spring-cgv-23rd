package com.ceos23.spring_cgv_23rd.User.Domain;

import com.ceos23.spring_cgv_23rd.User.DTO.ROLE;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private User(String loginId, String username, String password, boolean men, int age, ROLE role){
        this.loginId = loginId;
        this.username = username;
        this.password = password;
        this.men = men;
        this.age = age;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //사용자 이름(게시판 등)
    private String username;

    //사용자 아이디(로그인용)
    @Column(unique = true, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    private boolean men;

    private int age;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    public static User create(String loginId, String username, String password, boolean men, int age){
        return new User(loginId, username, password, men, age, ROLE.USER);
    }
}
