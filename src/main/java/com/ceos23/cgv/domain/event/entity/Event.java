package com.ceos23.cgv.domain.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    public static Event create(String title, String content, LocalDateTime startDate, LocalDateTime endDate) {
        return Event.builder()
                .title(title)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
