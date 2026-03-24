package com.ceos23.spring_cgv_23rd;

import com.ceos23.spring_cgv_23rd.Actor.Domain.Actor;
import com.ceos23.spring_cgv_23rd.Actor.Domain.ActorInfo;
import com.ceos23.spring_cgv_23rd.Actor.Repository.ActorInterface;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchAllResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.Domain.AccessibleAge;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Comment;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Domain.MovieType;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationSeatInfo;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
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

	@Test
	void contextLoads() {
	}

	public void setDataBase(){

	}

	@Test
	@DisplayName("영화관 전체조회")
	void searchTheaterAll() throws Exception {
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

		theaterRepository.saveAll(Arrays.asList(theater1, theater2, theater3, theater4));

		mockmvc.perform(get("/api/theater"))
				.andExpect(status().isOk());
	}

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
