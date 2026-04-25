package com.ceos23.spring_cgv_23rd.Token.Domain;

import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    private RefreshToken(String token, User user){
        this.user = user;
        this.token = token;
        this.expiredTime = LocalDateTime.now().plusSeconds(JWTType.REFRESH.getValidTime());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private Long version;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String token;

    @Getter
    //이미 토큰이 사용된 경우
    //true로 설정됩니다.
    private boolean used;

    @Getter
    //이미 폐기된 토큰일 경우
    //true로 설정됩니다.
    private boolean revoked;

    private LocalDateTime expiredTime;

    public void use(){
        this.used = true;
    }

    public void revoke(){
        this.revoked = true;
    }

    public boolean isMatchUser(String loginId){
        return loginId.equals(user.getLoginId());
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiredTime);
    }

    public static RefreshToken create(User user, String token){
        return new RefreshToken(token, user);
    }
}
