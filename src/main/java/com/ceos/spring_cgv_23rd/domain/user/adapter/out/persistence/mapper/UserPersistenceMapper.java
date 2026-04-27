package com.ceos.spring_cgv_23rd.domain.user.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.user.adapter.out.persistence.entity.UserEntity;
import com.ceos.spring_cgv_23rd.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    //  Entity → Domain
    public User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .birth(entity.getBirth())
                .nickname(entity.getNickname())
                .role(entity.getRole())
                .profileImageUrl(entity.getProfileImageUrl())
                .gender(entity.getGender())
                .build();
    }


    // Domain → Entity
    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .password(domain.getPassword())
                .name(domain.getName())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .birth(domain.getBirth())
                .nickname(domain.getNickname())
                .role(domain.getRole())
                .profileImageUrl(domain.getProfileImageUrl())
                .gender(domain.getGender())
                .build();
    }
}
