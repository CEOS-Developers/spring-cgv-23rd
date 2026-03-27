package com.ceos23.spring_cgv_23rd;

import com.ceos23.spring_cgv_23rd.Actor.Repository.ActorInterface;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchAllResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.Domain.AccessibleAge;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Domain.MovieType;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationSeatInfo;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationSeat;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreenRepository;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
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
	private MockMvc mockMvc;
    @Autowired
    private ScreenRepository screenRepository;

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

	List<Screening> setScreening(List<Screen> scs) {

		List<Movie> movies = movieRepository.findAll();
		List<Screening> scc = new ArrayList<>();

		for (Screen sc : scs){
			for (Movie mv : movies){
				//public static Screening create(Screen screen, Movie movie, LocalDateTime startTime){
				//        return new Screening(screen, movie, startTime);
				//    }
				scc.add(Screening.create(
					sc, mv, LocalDateTime.of(2026,3,28,8,0)
				));

				scc.add(Screening.create(
						sc, mv, LocalDateTime.of(2026,4,28,8,0)
				));


				scc.add(Screening.create(
					sc, mv, LocalDateTime.of(2026,3,28,13,0)
				));

				scc.add(Screening.create(
						sc, mv, LocalDateTime.of(2026,4,28,13,0)
				));


				scc.add(Screening.create(
					sc, mv, LocalDateTime.of(2026,3,28,20,0)
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

	User setUser() {
		User user = User.builder()
				.username("ceos")
				.password("ceos23rdbckend**")
				.build();

		return userRepository.save(user);
	}

	void normalSetting() throws BadRequestException {
		setMovie();
		setScreening(setScreen(setTheater()));
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
		List<Theater> theaterLists = setTheater();

		MvcResult res = mockmvc.perform(get("/api/theater?query=명동"))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res);
	}

	@Test
	@DisplayName("영화관 지역검색")
	void SearchTheaterWithRegion() throws Exception {
		List<Theater> theaterLists = setTheater();

		MvcResult res = mockmvc.perform(get("/api/theater?region=SEOUL"))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res);
	}

	@Test
	@DisplayName("영화관 전체 상영 중인 영화검색")
	void SearchMovieWithTheater() throws Exception {
		normalSetting();
		List<Theater> theaters = theaterRepository.findAll();

		for (Theater theater : theaters) {
			MvcResult res = mockmvc.perform(get("/api/screen?theaterId=" + theater.getId()
							+ "&date=" + LocalDate.of(2026,3,28)))
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
				+ "&date=" + LocalDate.of(2026, 3, 28)))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
		System.out.println("finish!");
	}

	@Test
	@DisplayName("영화 예매 테스트")
	void reserve() throws Exception {
		normalSetting();
		setUser();
		List<Screening> screenings = screeningRepository.findAll();
		User user = userRepository.findAll().get(0);


		ReservationRequestDTO r = ReservationRequestDTO.create(
				user.getId(),
				screenings.get(0).getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		MvcResult res = mockmvc.perform(post("/api/reservation")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r)))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화 예매 시 중복자리 예매 방지 테스트")
	void reserveWithOccupied() throws Exception {
		normalSetting();
		setUser();
		List<Screening> screenings = screeningRepository.findAll();
		User user = userRepository.findAll().get(0);


		ReservationRequestDTO r = ReservationRequestDTO.create(
				user.getId(),
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
		setUser();
		List<Screening> screenings = screeningRepository.findAll();
		User user = userRepository.findAll().get(0);


		ReservationRequestDTO r = ReservationRequestDTO.create(
				user.getId(),
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
				user.getId(),
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
		setUser();
		List<Screening> screenings = screeningRepository.findAll();
		User user = userRepository.findAll().get(0);


		ReservationRequestDTO r = ReservationRequestDTO.create(
				user.getId(),
				screenings.get(0).getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		ReservationRequestDTO r2 = ReservationRequestDTO.create(
				user.getId(),
				screenings.get(0).getId(),
				Arrays.asList(ReservationSeatInfo.create("D25", SeatInfo.SENIOR),
						ReservationSeatInfo.create("D26", SeatInfo.SENIOR))
		);

		ReservationRequestDTO r3 = ReservationRequestDTO.create(
				user.getId(),
				screenings.get(0).getId(),
				Arrays.asList(ReservationSeatInfo.create("M13", SeatInfo.ADULT),
						ReservationSeatInfo.create("E4", SeatInfo.ADULT),
						ReservationSeatInfo.create("F16", SeatInfo.ADULT))
		);

		mockmvc.perform(post("/api/reservation")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/reservation")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r2)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/reservation")
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
		setUser();
		User user = userRepository.findAll().get(0);
		Theater theater = theaterRepository.findAll().get(0);
		System.out.println("ID >>> " + theater.getId());

		MvcResult res = mockmvc.perform(get("/api/theater?userId=" + user.getId()
						+ "&theaterId=" + theater.getId()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화관 찜 & 취소")
	void bookmarkAndCancelTheater() throws Exception {
		normalSetting();
		setUser();
		User user = userRepository.findAll().get(0);
		Theater theater = theaterRepository.findAll().get(0);

		MvcResult res = mockmvc.perform(get("/api/theater?userId=" + user.getId()
						+ "&theaterId=" + theater.getId()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());

		MvcResult res2 = mockmvc.perform(get("/api/theater?userId=" + user.getId()
						+ "&theaterId=" + theater.getId()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res2.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화관 찜 & 취소 & 조회")
	void bookmarkAndCancelTheaterAndCheck() throws Exception {
		normalSetting();
		setUser();
		User user = userRepository.findAll().get(0);
		Theater theater = theaterRepository.findAll().get(0);

		MvcResult res = mockmvc.perform(get("/api/theater?userId=" + user.getId()
						+ "&theaterId=" + theater.getId()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());

		System.out.println(mockmvc.perform(get("/api/theater?userId=" + user.getId()))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getContentAsString());

		MvcResult res2 = mockmvc.perform(get("/api/theater?userId=" + user.getId()
						+ "&theaterId=" + theater.getId()))
				.andExpect(status().isOk())
				.andReturn();

		MvcResult res3 = mockmvc.perform(get("/api/theater?userId=" + user.getId()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res3.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화 찜")
	void bookmarkMovie() throws Exception {
		normalSetting();
		setUser();
		User user = userRepository.findAll().get(0);
		Movie movie = movieRepository.findAll().get(0);

		MvcResult res = mockmvc.perform(get("/api/movie?userId=" + user.getId()
						+ "&movieId=" + movie.getId()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화 찜 + 취소")
	void bookmarkMovieAndCancel() throws Exception {
		normalSetting();
		setUser();
		User user = userRepository.findAll().get(0);
		Movie movie = movieRepository.findAll().get(0);

		mockmvc.perform(get("/api/movie?userId=" + user.getId()
						+ "&movieId=" + movie.getId()))
				.andExpect(status().isOk())
				.andReturn();

		MvcResult res = mockmvc.perform(get("/api/movie?userId=" + user.getId()
						+ "&movieId=" + movie.getId()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());
	}

	@Test
	@DisplayName("영화 찜 + 취소 + 조회")
	void bookmarkMovieAndCancelAndCheck() throws Exception {
		normalSetting();
		setUser();
		User user = userRepository.findAll().get(0);
		Movie movie = movieRepository.findAll().get(0);

		mockmvc.perform(get("/api/movie?userId=" + user.getId()
						+ "&movieId=" + movie.getId()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(
				mockmvc.perform(get("/api/movie?userId=" + user.getId()))
						.andReturn().getResponse().getContentAsString()
		);

		MvcResult res = mockmvc.perform(get("/api/movie?userId=" + user.getId()
						+ "&movieId=" + movie.getId()))
				.andExpect(status().isOk())
				.andReturn();

		System.out.println(res.getResponse().getContentAsString());

		System.out.println(
				mockmvc.perform(get("/api/movie?userId=" + user.getId()))
						.andReturn().getResponse().getContentAsString()
		);
	}
}

//
//long userId,
//long screeningId,
//int totalPrice,
//List<ReservationSeatInfo> seatInfos


/*
	@Test
	@DisplayName("영화 조회")
	void searchMovieAll() throws Exception {
		Comment cmm = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("재미있어요")
				.build();

		Comment cmm2 = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("인생영화에요")
				.build();

		Comment cmm3 = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("재미없어요")
				.build();

		Comment cmm4 = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("조금 지루해요")
				.build();

		Comment cmm5 = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("후속작이 기대됩니다")
				.build();

		Movie movie1 = Movie.builder()
				.movieName("트루먼쇼")
				.openDate(LocalDateTime.of(1276, 7, 3, 8, 0))
				.reservRate(97.6)
				.eggRate(88.5)
				.prolog("나가자")
				.accessibleAge(AccessibleAge.NINETEEN)
				.movieType(MovieType.DRAMA)
				.build();

		Movie movie2 = Movie.builder()
				.movieName("어벤져스")
				.openDate(LocalDateTime.of(1010, 7, 6, 12, 0))
				.reservRate(99.8)
				.eggRate(95.4)
				.prolog("어벤져스 어셈블")
				.accessibleAge(AccessibleAge.ALL)
				.movieType(MovieType.ACTION)
				.build();

		movie1.addComment(cmm);
		movie1.addComment(cmm3);
		movie2.addComment(cmm2);
		movie2.addComment(cmm4);
		movie2.addComment(cmm5);

		movieRepository.saveAll(Arrays.asList(movie1, movie2));

		MvcResult result = mockmvc.perform(get("/api/movie"))
				.andExpect(status().isOk())
				.andReturn();

		MovieSearchAllResponseDTO body = objectMapper.readValue(result.getResponse().getContentAsString(), MovieSearchAllResponseDTO.class);
		System.out.println(body);
	}

	@Test
	@DisplayName("특정 영화 조회")
	void searchMovieSpecified() throws Exception {
		Comment cmm = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("재미있어요")
				.build();


		Comment cmm2 = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("인생영화에요")
				.build();

		Comment cmm3 = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("재미없어요")
				.build();

		Comment cmm4 = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("조금 지루해요")
				.build();

		Comment cmm5 = Comment.builder()
				.createdAt(LocalDateTime.now())
				.content("후속작이 기대됩니다")
				.build();

		Movie movie1 = Movie.builder()
				.movieName("트루먼쇼")
				.openDate(LocalDateTime.of(1276, 7, 3, 8, 0))
				.reservRate(97.6)
				.eggRate(88.5)
				.prolog("나가자")
				.accessibleAge(AccessibleAge.NINETEEN)
				.movieType(MovieType.DRAMA)
				.build();

		Movie movie2 = Movie.builder()
				.movieName("어벤져스")
				.openDate(LocalDateTime.of(1010, 7, 6, 12, 0))
				.reservRate(99.8)
				.eggRate(95.4)
				.prolog("어벤져스 어셈블")
				.accessibleAge(AccessibleAge.ALL)
				.movieType(MovieType.ACTION)
				.build();

		movie1.addComment(cmm);
		movie1.addComment(cmm3);
		movie2.addComment(cmm2);
		movie2.addComment(cmm4);
		movie2.addComment(cmm5);

		List<Movie> movies = movieRepository.saveAll(Arrays.asList(movie1, movie2));

		org.springframework.test.web.servlet.MvcResult result = mockmvc.perform(get("/api/movie/{searchQuery}", movies.get(0).getMovieName()))
				.andExpect(status().isOk())
				.andReturn();

		MovieSearchResponseDTO body = objectMapper.readValue(result.getResponse().getContentAsString(), MovieSearchResponseDTO.class);
		System.out.println(body);
	}

	@Test
	@DisplayName("영화관 전체조회")
	void searchTheaterWithQuery() throws Exception {
		Theater theater1 = Theater.builder()
				.name("CGV 판교")
				.region("경기")
				.address("경기 성남시 분당구 판교역로146번길 20")
				.build();

		Theater theater2 = Theater.builder()
				.name("CGV 서현점")
				.region("경기")
				.address("경기 성남시 분당구 서현로180번길 19 비전월드")
				.build();

		Theater theater3 = Theater.builder()
				.name("CGV 명동")
				.region("서울")
				.address("서울특별시 중구 명동길 14 Noon Square 8F")
				.build();

		Theater theater4 = Theater.builder()
				.name("CGV 대학로")
				.region("서울")
				.address("서울특별시 종로구 대명길 28 대학로 CGV")
				.build();

		List<Theater> svth = theaterRepository.saveAll(Arrays.asList(theater1, theater2, theater3, theater4));

		MvcResult result = mockMvc.perform(get("/api/theater/{searchQuery}", svth.get(2).getName()))
				.andExpect(status().isOk())
				.andReturn();


	}

	@Test
	@DisplayName("좌석예매하기")
	void reservation() throws Exception {

		boolean isInitialized = !(movieRepository.findAll().isEmpty());

		if (isInitialized){
			return;
		}

		User user = User.builder()
				.username("CEOS")
				.password("1234")
				.build();

		Comment cmm = Comment.builder()
				.user(user)
				.createdAt(LocalDateTime.now())
				.content("재밌어요")
				.build();

		Actor actor = Actor.builder()
				.name("유해진")
				.birth(LocalDateTime.of(1396, 03, 28, 14, 55))
				.country("오스트리아")
				.build();

		Movie movie = Movie.builder()
				.movieName("어벤져스:둠스데이")
				.openDate(LocalDateTime.of(1182, 7, 26, 00, 00))
				.reservRate(65.7)
				.eggRate(4.5)
				.prolog("간단한 줄거리")
				.accessibleAge(AccessibleAge.FIFTEEN)
				.movieType(MovieType.ANIMATION)
				.build();

		ActorInfo ai = ActorInfo.builder()
				.build();

		ReservationSeatInfo res = ReservationSeatInfo.builder()
				.seatName("A3")
				.info(SeatInfo.ADULT)
				.build();

		Screen screen = Screen.builder()
				.screenName("101관")
				.cinemaType("일반")
				.theater(null)
				.build();

		Screening sc = Screening.builder()
				.screen(screen)
				.movie(movie)
				.startTime(LocalTim.now())
				.endDate(LocalDateTime.now())
				.build();

		ai.setActor(actor);
		ai.setMovie(movie);

		cmm.setUser(user);
		cmm.setMovie(movie);

		userRepository.save(user);
		actorInfoRepository.save(actor);
		movieRepository.save(movie);
		Screening scs = screeningRepository.save(sc);

		ReservationSeatInfo seat = ReservationSeatInfo.builder()
				.seatName("A1").info(SeatInfo.ADULT)
				.build();

		ReservationRequestDTO reqDTO = ReservationRequestDTO.builder()
				.userId(userRepository.findByUsername("CEOS").getId())
				.screeningId(scs.getId())
				.seatInfos(Collections.singletonList(seat))
				.reservationDate(LocalDateTime.of(2092, 3, 11, 0, 2))
				.totalPrice(2000000)
				.build();

		mockmvc.perform(post("/api/reservation")
						.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDTO)))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("좌석예매 후 취소")
	void withdraw() throws Exception {

		boolean isInitialized = !(movieRepository.findAll().isEmpty());

		if (isInitialized){
			return;
		}

		User user = User.builder()
				.username("CEOS")
				.password("1234")
				.build();

		Comment cmm = Comment.builder()
				.user(user)
				.createdAt(LocalDateTime.now())
				.content("재밌어요")
				.build();

		Actor actor = Actor.builder()
				.name("유해진")
				.birth(LocalDateTime.of(1396, 03, 28, 14, 55))
				.country("오스트리아")
				.build();

		Movie movie = Movie.builder()
				.movieName("어벤져스:둠스데이")
				.openDate(LocalDateTime.of(1182, 7, 26, 00, 00))
				.reservRate(65.7)
				.eggRate(4.5)
				.prolog("간단한 줄거리")
				.accessibleAge(AccessibleAge.FIFTEEN)
				.movieType(MovieType.ANIMATION)
				.build();

		ActorInfo ai = ActorInfo.builder()
				.build();

		ReservationSeatInfo res = ReservationSeatInfo.builder()
				.seatName("A3")
				.info(SeatInfo.ADULT)
				.build();

		Screen screen = Screen.builder()
				.screenName("101관")
				.cinemaType("일반")
				.build();

		Screening sc = Screening.builder()
				.screen(screen)
				.movie(movie)
				.startTime(LocalDateTime.now())
				.endDate(LocalDateTime.now())
				.build();

		ai.setActor(actor);
		ai.setMovie(movie);

		cmm.setUser(user);
		cmm.setMovie(movie);

		userRepository.save(user);
		actorInfoRepository.save(actor);
		movieRepository.save(movie);
		Screening scs = screeningRepository.save(sc);

		ReservationSeatInfo seat = ReservationSeatInfo.builder()
				.seatName("A1").info(SeatInfo.ADULT)
				.build();

		ReservationRequestDTO reqDTO = ReservationRequestDTO.builder()
				.userId(userRepository.findByUsername("CEOS").getId())
				.screeningId(scs.getId())
				.seatInfos(Collections.singletonList(seat))
				.reservationDate(LocalDateTime.of(2092, 3, 11, 0, 2))
				.totalPrice(2000000)
				.build();

		MvcResult result = mockmvc.perform(post("/api/reservation")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(reqDTO)))
				.andExpect(status().isOk())
				.andReturn();

		ReservationResponseDTO body = objectMapper.readValue(result.getResponse().getContentAsString(), ReservationResponseDTO.class);
		long resId = body.id();

		mockMvc.perform(delete("/api/reservation/{reservationId}", resId))
				.andExpect(status().isOk());

	}
}
*/