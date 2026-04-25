package com.ceos23.spring_cgv_23rd.Actor.Domain;

import com.ceos23.spring_cgv_23rd.Media.Domain.Media;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private LocalDateTime birth;

    private String country;

    @OneToMany(mappedBy = "actor")
    private List<ActorInfo> actorInfos = new ArrayList<>();

    @OneToMany(mappedBy = "actor")
    private List<Prize> prizes = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "actor_photos")
    private List<Media> photos = new ArrayList<>();

    public void addActorInfos(ActorInfo ai){
        actorInfos.add(ai);
        ai.setActor(this);
    }

    public void addPrizes(Prize prize){
        prizes.add(prize);
        prize.setActor(this);
    }
}
