package com.ceos23.spring_cgv_23rd.Media.Domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Enumerated(EnumType.ORDINAL)
    private Type mediaType;

    private String url;

    private LocalDateTime createdAt;
}

enum Type {
    VIDEO, PHOTO
}