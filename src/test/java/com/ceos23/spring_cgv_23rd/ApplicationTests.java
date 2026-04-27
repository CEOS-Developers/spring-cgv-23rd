package com.ceos23.spring_cgv_23rd;

import com.ceos23.spring_cgv_23rd.Actor.Repository.ActorInterface;
import com.ceos23.spring_cgv_23rd.Food.Domain.Food;
import com.ceos23.spring_cgv_23rd.Food.Domain.MenuType;
import com.ceos23.spring_cgv_23rd.Food.Repository.FoodRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodMenuAndQuantityDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodOrderRequestDTO;
import com.ceos23.spring_cgv_23rd.Movie.Domain.AccessibleAge;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Domain.MovieType;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationSeatInfo;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterMenuRepository;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.User.Controller.LoginController;
import com.ceos23.spring_cgv_23rd.User.DTO.LoginRequestDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.SignupRequestDTO;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.User.Service.LoginService;
import jakarta.servlet.http.Cookie;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ApplicationTests {

	@Autowired
	private MockMvc mockmvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	MovieRepository movieRepository;

	@Autowired
	ActorInterface actorInfoRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ScreeningRepository screeningRepository;

	@Autowired
	TheaterRepository theaterRepository;

	@Autowired
    private FoodRepository foodRepository;

    @Autowired
    private TheaterMenuRepository theaterMenuRepository;

	@Autowired
	ApplicationContext context;

	@Test
	void check() {
		System.out.println("CONTEXT >>> " + context.getBean(LoginController.class));
		System.out.println(context.getBean(LoginService.class));
	}


	@Test
	void contextLoads() {
	}

	public void setDataBase() {

	}

	List<Theater> setTheater() throws BadRequestException {
		Theater theater1 = Theater.create("CGV 판교", "경기 성남시 분당구 판교역로146번길 20");
		Theater theater2 = Theater.create("CGV 서현점", "경기 성남시 분당구 서현로180번길 19 비전월드");
		Theater theater3 = Theater.create("CGV 명동", "서울특별시 중구 명동길 14 Noon Square 8F");
		Theater theater4 = Theater.create("CGV 대학로", "서울특별시 종로구 대명길 28 대학로 CGV");

		System.out.println("영화관 사전 설정 완료!");
		return theaterRepository.saveAll(Arrays.asList(theater1, theater2, theater3, theater4));
	}

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
						sc, mv, LocalDateTime.of(2026,4,28,8,0)
				));


				scc.add(Screening.create(
					sc, mv, LocalDateTime.of(2026,5,28,13,0)
				));

				scc.add(Screening.create(
						sc, mv, LocalDateTime.of(2026,4,28,13,0)
				));


				scc.add(Screening.create(
					sc, mv, LocalDateTime.of(2026,5,28,20,0)
				));

				scc.add(Screening.create(
						sc, mv, LocalDateTime.of(2026,4,28,20,0)
				));
			}
		}

		return screeningRepository.saveAll(scc);
	}


	List<Screen> setScreen(List<Theater> theaters) throws BadRequestException {
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

    void signup() throws Exception {
		if (userRepository.existsByLoginId("ceos1234")){
			return;
		}

        SignupRequestDTO srd1 = new SignupRequestDTO(
                "세오스", "ceos1234", "ceos1234**", true, 21
        );

        SignupRequestDTO srd2 = new SignupRequestDTO(
                "홍익대", "hongik1234", "hongik1234**", true, 29
        );

        SignupRequestDTO srd3 = new SignupRequestDTO(
                "김자바", "hongk1234", "java1234**", false, 25
        );

        mockmvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(srd1)))
                .andExpect(status().isOk())
                .andReturn();

        mockmvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(srd2)))
                .andExpect(status().isOk())
                .andReturn();

        mockmvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(srd3)))
                .andExpect(status().isOk())
                .andReturn();
    }

	void normalSetting() throws Exception {
		setMovie();
		List<Theater> theaters = setTheater();
		setTheaterToTheaterMenu(theaters);
		setScreening(setScreen(theaters));
		signup();
	}

    void signup(SignupRequestDTO req) throws Exception{
        mockmvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();
    }

    String login(LoginRequestDTO req) throws Exception {
        return mockmvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn()
				.getResponse().getCookie("accessToken").getValue();
    }

	@Test
	@DisplayName("영화관 전체조회")
	void SearchAllTheater() throws Exception {
		List<Theater> theaterLists = setTheater();

		MvcResult res = mockmvc.perform(get("/api/theater"))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res);
	}

	@Test
	@DisplayName("영화관 검색")
	void SearchTheaterWithQuery() throws Exception {
		setTheater();

		MvcResult res = mockmvc.perform(get("/api/theater?query=명동"))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화관 지역검색")
	void SearchTheaterWithRegion() throws Exception {
		List<Theater> theaterLists = setTheater();

		MvcResult res = mockmvc.perform(get("/api/theater?region=SEOUL"))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화관 전체 상영 중인 영화검색")
	void SearchMovieWithTheater() throws Exception {
		normalSetting();
		List<Theater> theaters = theaterRepository.findAll();

		for (Theater theater : theaters) {
			MvcResult res = mockmvc.perform(get("/api/screen?theaterId=" + theater.getId()
							+ "&date=" + LocalDate.of(2026,4,28)))
					.andExpect(status().isOk())
					.andReturn();

			System.out.println(res.getResponse().getContentAsString());
		}

		System.out.println("finish!");
	}

	@Test
	@DisplayName("영화관 및 영화를 검색어로 검색")
	void SearchMovieWithTheaterAndMovie() throws Exception {
		normalSetting();
		List<Theater> theaters = theaterRepository.findAll();
		List<Movie> movies = movieRepository.findAll();

		MvcResult res = mockmvc.perform(get("/api/screen?theaterId=" + theaters.get(0).getId()
				+ "&movieId=" + movies.get(1).getId()
				+ "&date=" + LocalDate.of(2026, 4, 28)))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
		System.out.println("finish!");
	}

	@Test
	@DisplayName("영화 예매 테스트")
	void reserve() throws Exception {
		normalSetting();
		List<Screening> screenings = screeningRepository.findAll();
        String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");

		ReservationRequestDTO r = ReservationRequestDTO.create(
				screenings.get(0).getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		MvcResult reservationResult = mockmvc.perform(post("/api/reservation")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r))
				)
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(reservationResult.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화 예매 시 중복자리 예매 방지 테스트")
	void reserveWithOccupied() throws Exception {
		normalSetting();
		List<Screening> screenings = screeningRepository.findAll();
		String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");


		ReservationRequestDTO r = ReservationRequestDTO.create(
				screenings.get(3).getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C16", SeatInfo.CHILD))
		);

		MvcResult res = mockmvc.perform(post("/api/reservation")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r)))
				.andExpect(status().isBadRequest())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화 예매 시 이선좌 테스트")
	void reserveAlreadyOccupied() throws Exception {
		normalSetting();
		List<Screening> screenings = screeningRepository.findAll();
		String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");

		ReservationRequestDTO r = ReservationRequestDTO.create(
				screenings.get(3).getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		MvcResult res = mockmvc.perform(post("/api/reservation")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r)))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());

		ReservationRequestDTO r2 = ReservationRequestDTO.create(
				screenings.get(3).getId(),
                List.of(ReservationSeatInfo.create("C16", SeatInfo.ADULT)));

		MvcResult res2 = mockmvc.perform(post("/api/reservation")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r2)))
				.andExpect(status().isBadRequest())
				.andReturn();
	}

	@Test
	@DisplayName("영화 예매 후 좌석변화 감지")
	void canceling() throws Exception {
		normalSetting();
		List<Screening> screenings = screeningRepository.findAll();
		String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");

		ReservationRequestDTO r = ReservationRequestDTO.create(
				screenings.get(3).getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		mockmvc.perform(post("/api/reservation")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r)))
				.andExpect(status().isOk());

		ReservationRequestDTO r2 = ReservationRequestDTO.create(
				screenings.get(0).getId(),
				Arrays.asList(ReservationSeatInfo.create("D25", SeatInfo.SENIOR),
						ReservationSeatInfo.create("D26", SeatInfo.SENIOR))
		);

		ReservationRequestDTO r3 = ReservationRequestDTO.create(
				screenings.get(0).getId(),
				Arrays.asList(ReservationSeatInfo.create("M13", SeatInfo.ADULT),
						ReservationSeatInfo.create("E4", SeatInfo.ADULT),
						ReservationSeatInfo.create("F16", SeatInfo.ADULT))
		);

		mockmvc.perform(post("/api/reservation")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r2)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/reservation")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r3)))
				.andExpect(status().isOk());


		MvcResult res = mockmvc.perform(get("/api/screen?theaterId=" + screenings.get(0).getScreen().getTheater().getId()
						+ "&movieId=" + screenings.get(0).getMovie().getId()
						+ "&date=" + screenings.get(0).getStartTime().toLocalDate()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
		System.out.println("finish!");
	}

	@Test
	@DisplayName("영화관 찜")
	void bookmarkTheater() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);

		String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");

		MvcResult res = mockmvc.perform(get("/api/theater/likes"
						+ "?theaterId=" + theater.getId())
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화관 찜 & 취소")
	void bookmarkAndCancelTheater() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);

		String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");

		MvcResult res = mockmvc.perform(get("/api/theater/likes"
						+ "?theaterId=" + theater.getId())
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());

		MvcResult res2 = mockmvc.perform(get("/api/theater/likes"
						+ "?theaterId=" + theater.getId())
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res2.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화관 찜 & 취소 & 조회")
	void bookmarkAndCancelTheaterAndCheck() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);

		String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");

		MvcResult res = mockmvc.perform(get("/api/theater/likes"
						+ "?theaterId=" + theater.getId())
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());

		System.out.println(mockmvc.perform(get("/api/theater/likes")
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getContentAsString());

		MvcResult res2 = mockmvc.perform(get("/api/theater/likes"
						+ "?theaterId=" + theater.getId())
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn();

		MvcResult res3 = mockmvc.perform(get("/api/theater/likes")
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res3.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("메뉴 조회")
	void food() throws Exception {
		normalSetting();
		List<Theater> theaters = theaterRepository.findAll();
		setTheaterToTheaterMenu(theaters);

		System.out.println(
				mockmvc.perform(get("/api/menus?theaterId=" + theaters.get(0).getId()
						+ "&menuType=" + MenuType.COMBO))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString()
		);

		System.out.println(
				mockmvc.perform(get("/api/menus?theaterId=" + theaters.get(1).getId()
								+ "&menuType=" + MenuType.COMBO))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString()
		);
	}

	@Test
	@DisplayName("음식 예약하기")
	void foodOrder() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);

		String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");

		List<TheaterMenu> theaterMenus = theaterMenuRepository.findByTheater(theater);
		FoodMenuAndQuantityDTO rrd = new FoodMenuAndQuantityDTO(theaterMenus.get(0).getId(), 2);
		FoodMenuAndQuantityDTO rrd2 = new FoodMenuAndQuantityDTO(theaterMenus.get(1).getId(), 1);


		mockmvc.perform(post("/api/foods/items")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(new FoodOrderRequestDTO(theater.getId(), Arrays.asList(rrd, rrd2))))
							.cookie(authCookie))
				.andExpect(status().isOk());

		System.out.println(mockmvc.perform(get("/api/foods")
						    .cookie(authCookie))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString()
		);
	}

	/**
	 * 장바구니에만 추가한 경우에는 재고수량이 줄지 않았다.
	 * 결제까지 진행한 경우에 재고수량이 줄어들었다.
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("음식 구매 후 수량 변화 조사")
	void foodOrderAndCheck() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);

		String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");

		List<TheaterMenu> theaterMenus = theaterMenuRepository.findByTheater(theater);
		FoodMenuAndQuantityDTO rrd = new FoodMenuAndQuantityDTO(theaterMenus.get(0).getId(), 3);
		FoodMenuAndQuantityDTO rrd2 = new FoodMenuAndQuantityDTO(theaterMenus.get(1).getId(), 1);

		System.out.println(
				mockmvc.perform(get("/api/menus?theaterId=" + theater.getId()
								+ "&menuType=" + MenuType.COMBO))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString()
		);

		mockmvc.perform(post("/api/foods/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new FoodOrderRequestDTO(theater.getId(), Arrays.asList(rrd, rrd2))))
						.cookie(authCookie))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/foods/pay")
					.contentType(MediaType.APPLICATION_JSON)
					.cookie(authCookie))
				.andExpect(status().isOk());

		System.out.println(
				mockmvc.perform(get("/api/menus?theaterId=" + theater.getId()
								+ "&menuType=" + MenuType.COMBO))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString()
		);
	}

	@Test
	@DisplayName("음식 매진 테스트")
	void foodSoldOut() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);

		String at = login(new LoginRequestDTO("ceos1234", "ceos1234**"));

		System.out.println("at >>> " + at);
		Cookie authCookie = new Cookie("accessToken", at);
		authCookie.setPath("/");

		List<TheaterMenu> theaterMenus = theaterMenuRepository.findByTheater(theater);
		FoodMenuAndQuantityDTO rrd = new FoodMenuAndQuantityDTO(theaterMenus.get(0).getId(), 2);
		FoodMenuAndQuantityDTO rrd2 = new FoodMenuAndQuantityDTO(theaterMenus.get(1).getId(), 1);

		mockmvc.perform(post("/api/foods/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new FoodOrderRequestDTO(theater.getId(), Arrays.asList(rrd, rrd2))))
						.cookie(authCookie))
				.andExpect(status().isOk());

		System.out.println(mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new FoodOrderRequestDTO(theater.getId(), Arrays.asList(rrd, rrd2)))))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString());
	}
}