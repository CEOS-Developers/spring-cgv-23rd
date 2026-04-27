package com.ceos23.spring_cgv_23rd.Theater.Domain;

import com.ceos23.spring_cgv_23rd.Movie.Domain.AudienceData;
import jakarta.persistence.*;
import lombok.*;
import org.apache.coyote.BadRequestException;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theater {
    private Theater(String name, Region region, String address, List<TheaterMenu> menus){
        this.name = name; this.region = region; this.address = address; this.theaterMenus = menus;
    }

    private Theater(String name, Region region, String address){
        this.name = name; this.region = region; this.address = address;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    private List<TheaterMenu> theaterMenus = new ArrayList<>();

    private String name;

    @Enumerated(EnumType.STRING)
    private Region region;

    private String address;

    public void addTheaterMenu(TheaterMenu thm){
        theaterMenus.add(thm);
        thm.setTheater(this);
    }

    public static Theater create(String name, String address) throws BadRequestException {
        return new Theater(name, Region.findRegion(address), address);
    }

    public static Theater create(String name, String address, List<TheaterMenu> menus) throws BadRequestException {
        return new Theater(name, Region.findRegion(address), address, menus);
    }
}
