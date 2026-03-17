package cgv_23rd.ceos.domain.food;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Food {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private Integer price;
    private String foodImageUrl;

    @OneToMany(mappedBy = "food")
    private List<TheaterFood> theaterFoods = new ArrayList<>();

    @OneToMany(mappedBy = "food")
    private List<FoodOrderItem> foodOrderItems = new ArrayList<>();
}
