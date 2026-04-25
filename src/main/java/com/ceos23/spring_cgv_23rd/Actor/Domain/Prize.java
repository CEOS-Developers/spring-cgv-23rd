package com.ceos23.spring_cgv_23rd.Actor.Domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "actor_id")
    private Actor actor;

    private String prizeName;

    public void addActor(Actor ac){
        actor = ac;
        ac.getPrizes().add(this);
    }
}
