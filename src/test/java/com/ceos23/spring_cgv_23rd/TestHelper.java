package com.ceos23.spring_cgv_23rd;

import com.ceos23.spring_cgv_23rd.Food.Domain.Food;
import com.ceos23.spring_cgv_23rd.Food.Domain.MenuType;
import com.ceos23.spring_cgv_23rd.Food.Repository.FoodRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.CartRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.FoodOrderRepository;
import com.ceos23.spring_cgv_23rd.Movie.Domain.AccessibleAge;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Domain.MovieType;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationSeatRepository;
import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreenRepository;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterMenuRepository;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.Token.Repository.TokenRepository;
import com.ceos23.spring_cgv_23rd.User.DTO.SignupRequestDTO;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TestHelper {

    private final TheaterRepository theaterRepository;
    private final TheaterMenuRepository theaterMenuRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final FoodRepository foodRepository;
    private final TokenRepository tokenRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final CartRepository cartRepository;
    private final ScreenRepository screenRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    public TestHelper(TheaterRepository theaterRepository, TheaterMenuRepository theaterMenuRepository, MovieRepository movieRepository, UserRepository userRepository, ScreeningRepository screeningRepository, FoodRepository foodRepository, TokenRepository tokenRepository, FoodOrderRepository foodOrderRepository, CartRepository cartRepository, ScreenRepository screenRepository, ReservationRepository reservationRepository, ReservationSeatRepository reservationSeatRepository) {
        this.theaterRepository = theaterRepository;
        this.theaterMenuRepository = theaterMenuRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.screeningRepository = screeningRepository;
        this.foodRepository = foodRepository;
        this.tokenRepository = tokenRepository;
        this.foodOrderRepository = foodOrderRepository;
        this.cartRepository = cartRepository;
        this.screenRepository = screenRepository;
        this.reservationRepository = reservationRepository;
        this.reservationSeatRepository = reservationSeatRepository;
    }

    @Transactional
    List<Theater> setTheater() {
        Theater theater1 = Theater.create("CGV 판교", "경기 성남시 분당구 판교역로146번길 20");
        Theater theater2 = Theater.create("CGV 서현점", "경기 성남시 분당구 서현로180번길 19 비전월드");
        Theater theater3 = Theater.create("CGV 명동", "서울특별시 중구 명동길 14 Noon Square 8F");
        Theater theater4 = Theater.create("CGV 대학로", "서울특별시 종로구 대명길 28 대학로 CGV");

        System.out.println("영화관 사전 설정 완료!");
        return theaterRepository.saveAll(Arrays.asList(theater1, theater2, theater3, theater4));
    }

    @Transactional
    List<Movie> setMovie() {
        Movie movie1 = Movie.create("트루먼쇼",
                LocalDate.of(2025, 9, 12),
                "트루먼 쇼는 ~~한 내용입니다.",
                AccessibleAge.TWELVE,
                MovieType.DRAMA,
                15000,
                120);


        Movie movie2 = Movie.create("명량",
                LocalDate.of(2004, 5, 16),
                "신에게는 아직 12척의 배가 있사옵니다.",
                AccessibleAge.FIFTEEN,
                MovieType.ACTION,
                13000,
                110);


        Movie movie3 = Movie.create("어벤져스: 엔드게임",
                LocalDate.of(2023, 6, 5),
                "어벤져스 인피니티 사가의 최종장!",
                AccessibleAge.FIFTEEN,
                MovieType.ACTION,
                12000,
                190);


        Movie movie4 = Movie.create("왕과사는남자",
                LocalDate.of(2026, 1, 11),
                "2년만에 나온 천만영화",
                AccessibleAge.TWELVE,
                MovieType.HISTORY,
                14000,
                80);


        Movie movie5 = Movie.create("영화이름",
                LocalDate.of(1999, 3, 18),
                "영화내용",
                AccessibleAge.NINETEEN,
                MovieType.WAR,
                17000,
                110);

        return movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4, movie5));
    }

    @Transactional
    List<Food> setFood() {
        Food food1 = Food.create(
                "왕사남콤보", 9500, "왕사남 콤보 구매 시 스낵 추가가 1천원!", MenuType.COMBO
        );

        Food food2 = Food.create(
                "주토피아 무빙뱃지 세트", 13900, "8종 랜덤 무빙뱃지!", MenuType.COMBO
        );

        Food food3 = Food.create(
                "팝콘(M)", 5500, "팝콘(M)", MenuType.POPCORN
        );

        Food food4 = Food.create(
                "시그니처 팝콘", 4500, "팝콘 맛집 cgv의 시그니처 팝콘!", MenuType.POPCORN
        );

        Food food5 = Food.create(
                "콜라", 3500, "콜라", MenuType.BEVERAGE
        );

        return foodRepository.saveAll(Arrays.asList(food1, food2, food3, food4, food5));
    }

    List<TheaterMenu> setTheaterMenu(Theater theater, List<Food> foods){
        List<TheaterMenu> res = new ArrayList<>();

        for (Food food : foods){
            res.add(
                    TheaterMenu.create(
                            food, theater, 3
                    )
            );
        }

        return theaterMenuRepository.saveAll(res);
    }

    @Transactional
    void setTheaterToTheaterMenu(List<Theater> theaters){
        List<Food> foods = setFood();

        for (Theater th : theaters){
            setTheaterMenu(th, foods);
        }
    }

    List<Screening> setScreening(List<Screen> scs) {

        List<Movie> movies = movieRepository.findAll();
        List<Screening> scc = new ArrayList<>();

        for (Screen sc : scs){
            for (Movie mv : movies){
                scc.add(Screening.create(
                        sc, mv, LocalDateTime.of(2026,5,28,8,0)
                ));

                scc.add(Screening.create(
                        sc, mv, LocalDateTime.of(2026,6,28,8,0)
                ));


                scc.add(Screening.create(
                        sc, mv, LocalDateTime.of(2026,5,28,13,0)
                ));

                scc.add(Screening.create(
                        sc, mv, LocalDateTime.of(2026,6,28,13,0)
                ));


                scc.add(Screening.create(
                        sc, mv, LocalDateTime.of(2026,5,28,20,0)
                ));

                scc.add(Screening.create(
                        sc, mv, LocalDateTime.of(2026,6,28,20,0)
                ));
            }
        }

        return screeningRepository.saveAll(scc);
    }

    @Transactional
    List<Screen> setScreen(List<Theater> theaters) {
        List<Screen> sss = new ArrayList<>();

        for (Theater theater : theaters){
            sss.add(Screen.create(theater, "1관", CinemaType.NORMAL, 157));
            sss.add(Screen.create(theater, "2관", CinemaType.NORMAL, 163));
            sss.add(Screen.create(theater, "3관", CinemaType.NORMAL, 156));

            sss.add(Screen.create(theater, "12관", CinemaType.IMAX, 133));
            sss.add(Screen.create(theater, "15관", CinemaType.FOUR_D_X, 133));
        }

        sss.add(Screen.create(theaters.get(0), "118관", CinemaType.PRIMIUM, 10));
        sss.add(Screen.create(theaters.get(0), "120관", CinemaType.SCREEN_X, 164));

        return sss;
    }

    @Transactional(readOnly = true)
    public List<Theater> findAllTheater() throws Exception {
        return theaterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TheaterMenu> findAllTheaterMenuByTheater(Theater theater){
        return theaterMenuRepository.findByTheater(theater);
    }

    void clear(){
        tokenRepository.deleteAll();
        foodOrderRepository.deleteAll();
        cartRepository.deleteAll();

        reservationSeatRepository.deleteAll();
        reservationRepository.deleteAll();
        screeningRepository.deleteAll();

        theaterMenuRepository.deleteAll();

        screenRepository.deleteAll();
        movieRepository.deleteAll();

        foodRepository.deleteAll();
        theaterRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Transactional
    void normalSetting() throws Exception {
        setMovie();
        List<Theater> theaters = setTheater();
        setTheaterToTheaterMenu(theaters);
        setScreening(setScreen(theaters));
    }

    @Transactional
    Screening getScreening() throws Exception {
        return screeningRepository.findAll().get(0);
    }
}
