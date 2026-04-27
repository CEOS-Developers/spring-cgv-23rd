package com.ceos23.cgv_clone.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 해당 어노테이션이 적용된 클래스는 테이블로 생성 X
@EntityListeners(AuditingEntityListener.class)
//BaseEntity의 변화를 감지하고 처리하기 위해 @EntityListeners로 AuditingEntityListener 클래스를 등록
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
