package com.ceos23.spring_cgv_23rd.Theater.Domain;

import com.ceos23.spring_cgv_23rd.Movie.Domain.AudienceData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "theater")
    private List<TheaterMenu> theaterMenus;

    private String name;

    private String region;

    private String address;

    public void addTheaterMenu(TheaterMenu thm){
        theaterMenus.add(thm);
        thm.setTheater(this);
    }
}
