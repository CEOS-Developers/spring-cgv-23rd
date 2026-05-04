package com.ceos23.spring_cgv_23rd;

import com.ceos23.spring_cgv_23rd.Food.Domain.MenuType;
import com.ceos23.spring_cgv_23rd.Food.Repository.FoodRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.*;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.CartRepository;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.FoodOrderRepository;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchAllResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationRequestDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationSeatInfo;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.RemainingSeatsDTO;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservationResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.SeatInfo;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningWrapperDTO;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.*;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Region;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterMenuRepository;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.User.DTO.LoginRequestDTO;
import com.ceos23.spring_cgv_23rd.User.DTO.SignupRequestDTO;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.DTO.ErrDTO;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.hamcrest.Matchers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationTests {
	@Autowired
	private MockMvc mockmvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	MovieRepository movieRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ScreeningRepository screeningRepository;

	@Autowired
	TheaterRepository theaterRepository;

	@Autowired
	private TheaterMenuRepository theaterMenuRepository;

	@Autowired
	private TestHelper testHelper;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private FoodOrderRepository foodOrderRepository;

	void signup() throws Exception {
		if (userRepository.existsByLoginId("ceos1234")) {
			return;
		}

		SignupRequestDTO srd1 = new SignupRequestDTO(
				"세오스", "ceos1234", "ceos1234**", true, 21
		);

		SignupRequestDTO srd2 = new SignupRequestDTO(
				"홍익대", "hongik1234", "hongik1234**", true, 29
		);

		SignupRequestDTO srd3 = new SignupRequestDTO(
				"김자바", "java1234", "java1234**", false, 25
		);

		SignupRequestDTO srd4 = new SignupRequestDTO(
				"이신촌", "sinchon1234", "sinchon1234**", false, 25
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

		mockmvc.perform(post("/api/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(srd4)))
				.andExpect(status().isOk())
				.andReturn();
	}

	Cookie login() throws Exception {
		LoginRequestDTO req = new LoginRequestDTO("ceos1234", "ceos1234**");

		String accessToken = Objects.requireNonNull(mockmvc.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getCookie("accessToken")).getValue();

		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setPath("/");
		return cookie;
	}

	Cookie login2() throws Exception {
		LoginRequestDTO req = new LoginRequestDTO("hongik1234", "hongik1234**");

		String accessToken = Objects.requireNonNull(mockmvc.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getCookie("accessToken")).getValue();

		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setPath("/");
		return cookie;
	}

	Cookie login3() throws Exception {
		LoginRequestDTO req = new LoginRequestDTO("java1234", "java1234**");

		String accessToken = Objects.requireNonNull(mockmvc.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getCookie("accessToken")).getValue();

		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setPath("/");
		return cookie;
	}

	Cookie login4() throws Exception {
		LoginRequestDTO req = new LoginRequestDTO("sinchon1234", "sinchon1234**");

		String accessToken = Objects.requireNonNull(mockmvc.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse().getCookie("accessToken")).getValue();

		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setPath("/");
		return cookie;
	}


	void normalSetting() throws Exception {
		testHelper.normalSetting();
		signup();
	}

	@Test
	@Transactional
	@DisplayName("장바구니에 음식 추가하기")
	void addItemToCart() throws Exception {
		normalSetting();
		Theater theater = testHelper.findAllTheater().get(0);
		List<TheaterMenu> tms = testHelper.findAllTheaterMenuByTheater(theater);
		Cookie authCookie = login();

		FoodMenuAndQuantityDTO fdq1 = new FoodMenuAndQuantityDTO(tms.get(0).getId(), 2);
		FoodMenuAndQuantityDTO fdq2 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);

		FoodOrderRequestDTO for1 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq1, fdq2));
		String res = mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		CartResponseDTO cartResponseDTO = objectMapper.readValue(res, CartResponseDTO.class);

		Map<Long, Integer> map = cartResponseDTO.items().stream()
				.collect(Collectors.toMap(
						item -> item.menu().menuId(),
						CartItemWrapperDTO::quantity
				));

		assertThat(map).hasSize(2);
		assertThat(map.get(tms.get(0).getId())).isEqualTo(2);
		assertThat(map.get(tms.get(1).getId())).isEqualTo(1);
	}

	@Test
	@Transactional
	@DisplayName("장바구니 결제하기")
	void payCart() throws Exception {
		normalSetting();
		Cookie authCookie = login();
		Theater theater = testHelper.findAllTheater().get(0);
		List<TheaterMenu> tms = testHelper.findAllTheaterMenuByTheater(theater);

		FoodMenuAndQuantityDTO fdq1 = new FoodMenuAndQuantityDTO(tms.get(0).getId(), 2);
		FoodMenuAndQuantityDTO fdq2 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);

		FoodOrderRequestDTO for1 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq1, fdq2));
		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		FoodMenuAndQuantityDTO fdq3 = new FoodMenuAndQuantityDTO(tms.get(2).getId(), 1);
		FoodMenuAndQuantityDTO fdq4 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);

		FoodOrderRequestDTO for2 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq3, fdq4));

		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for2)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie))
				.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("장바구니 담기 및 결제 후 재고 확인")
	void accessTheaterMenu() throws Exception {
		normalSetting();
		Theater theater = testHelper.findAllTheater().get(0);
		Cookie authCookie = login();
		List<TheaterMenu> tms = testHelper.findAllTheaterMenuByTheater(theater);

		//기본설정된 재고 조사
		mockmvc.perform(get("/api/menus?theaterId=" + theater.getId()
						+ "&menuType=" + MenuType.COMBO))
				.andExpect(status().isOk());

		//장바구니에 수량 추가하기
		FoodMenuAndQuantityDTO fdq1 = new FoodMenuAndQuantityDTO(tms.get(0).getId(), 2);
		FoodMenuAndQuantityDTO fdq2 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);

		FoodOrderRequestDTO for1 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq1, fdq2));
		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isOk());

		//assert -> Menu에는 수량이 그대로
		String res = mockmvc.perform(get("/api/menus?theaterId=" + theater.getId()
						+ "&menuType=" + MenuType.COMBO))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		MenuFindingResponseDTO menuFindingResponseDTO = objectMapper.readValue(res, MenuFindingResponseDTO.class);

		Map<Long, Integer> map = menuFindingResponseDTO.menus().stream()
				.collect(Collectors.toMap(
						MenuWrapperDTO::id,
						MenuWrapperDTO::sold
				));

		assertThat(map.get(tms.get(0).getId())).isEqualTo(3);
		assertThat(map.get(tms.get(1).getId())).isEqualTo(3);


		//결제(결제 시 수량차감됨)
		mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//assert -> 수량감소
		String res2 = mockmvc.perform(get("/api/menus?theaterId=" + theater.getId()
						+ "&menuType=" + MenuType.COMBO))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		MenuFindingResponseDTO menuFindingResponseDTO2 = objectMapper.readValue(res2, MenuFindingResponseDTO.class);

		Map<Long, Integer> map2 = menuFindingResponseDTO2.menus().stream()
				.collect(Collectors.toMap(
						MenuWrapperDTO::id,
						MenuWrapperDTO::sold
				));

		assertThat(map2.get(tms.get(0).getId())).isEqualTo(1);
		assertThat(map2.get(tms.get(1).getId())).isEqualTo(2);
	}

	@Test
	@DisplayName("장바구니 담기 및 결제 후 장바구니 확인")
	void findSoldInOrderingFood() throws Exception {
		normalSetting();
		Theater theater = testHelper.findAllTheater().get(0);
		Cookie authCookie = login();
		List<TheaterMenu> tms = testHelper.findAllTheaterMenuByTheater(theater);

		//장바구니 및 이전주문기록 확인하기
		String res = mockmvc.perform(get("/api/foods")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		String res2 = mockmvc.perform(get("/api/foods/history")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//assert -> 장바구니가 비어있음
		CartResponseDTO cartResponseDTOBefore = objectMapper.readValue(res, CartResponseDTO.class);
		assertThat(cartResponseDTOBefore.items().isEmpty());

		//assert -> 이전주문기록이 비어있음
		List<OrderResponseDTO> list = objectMapper.readValue(res2, new TypeReference<List<OrderResponseDTO>>() {
		});
		assertThat(list).isEmpty();

		System.out.println("1차 assert 통과 >>> ");
		//장바구니에 물건 추가하기
		FoodMenuAndQuantityDTO fdq1 = new FoodMenuAndQuantityDTO(tms.get(0).getId(), 2);
		FoodMenuAndQuantityDTO fdq2 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);

		FoodOrderRequestDTO for1 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq1, fdq2));
		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isOk());

		//물건 추가 후 장바구니 및 이전주문기록 확인하기
		String resPending = mockmvc.perform(get("/api/foods")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		String res3 = mockmvc.perform(get("/api/foods/history")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//assert -> 장바구니에 물건이 추가됨
		CartResponseDTO cartResponseDTOPending = objectMapper.readValue(resPending, CartResponseDTO.class);
		Map<Long, Integer> mapPending = cartResponseDTOPending.items().stream()
				.collect(Collectors.toMap(
						c -> c.menu().menuId(),
						CartItemWrapperDTO::quantity
				));

		assertThat(mapPending.get(tms.get(0).getId())).isEqualTo(2);
		assertThat(mapPending.get(tms.get(1).getId())).isEqualTo(1);

		//assert -> 이전주문기록이 비어있음
		List<OrderResponseDTO> list2 = objectMapper.readValue(res3, new TypeReference<List<OrderResponseDTO>>() {
		});
		assertThat(list2).isEmpty();

		//결제(결제 시 장바구니를 비우고 이전주문기록에 추가함)
		mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		System.out.println("cart >>> " + cartRepository.findAll().get(0).getId() + cartRepository.findAll().get(0).getStatus());
		System.out.println("order >>> " + foodOrderRepository.findAll());

		//결제 후 장바구니 및 이전주문기록 확인
		String res4 = mockmvc.perform(get("/api/foods")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		String res5 = mockmvc.perform(get("/api/foods/history")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//assert -> 장바구니가 비어있음
		CartResponseDTO cartResponseDTOEnding = objectMapper.readValue(res4, CartResponseDTO.class);
		System.out.println("!!! " + res4);
		assertThat(cartResponseDTOEnding.items()).isEmpty();

		//assert -> 이전 장바구니 기록이 이전주문기록에 추가됨
		List<OrderResponseDTO> list3 = objectMapper.readValue(res5, new TypeReference<List<OrderResponseDTO>>() {
		});
		assertThat(list3.size() == 1);

		Map<Long, Integer> orderEnding = list3.get(0).items().stream()
				.collect(Collectors.toMap(
						o -> o.menu().menuId(),
						OrderItemWrapperDTO::quantity
				));

		assertThat(orderEnding.get(tms.get(0).getId())).isEqualTo(2);
		assertThat(orderEnding.get(tms.get(1).getId())).isEqualTo(1);

		//추가결제
		FoodMenuAndQuantityDTO fdq3 = new FoodMenuAndQuantityDTO(tms.get(2).getId(), 1);
		FoodMenuAndQuantityDTO fdq4 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);

		FoodOrderRequestDTO for2 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq3, fdq4));

		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for2)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie))
				.andExpect(status().isOk());

		//이전 장바구니 확인하기
		String res6 = mockmvc.perform(get("/api/foods/history")
						.cookie(authCookie))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//assert -> 이전주문기록에 최근 결제정보가 추가됨
		List<OrderResponseDTO> list4 = objectMapper.readValue(res6, new TypeReference<List<OrderResponseDTO>>() {
		});
		assertThat(list3.size() == 2);

		Map<Long, Integer> orderEnding2 = list4.get(0).items().stream()
				.collect(Collectors.toMap(
						o -> o.menu().menuId(),
						OrderItemWrapperDTO::quantity
				));

		Map<Long, Integer> orderEnding3 = list4.get(1).items().stream()
				.collect(Collectors.toMap(
						o -> o.menu().menuId(),
						OrderItemWrapperDTO::quantity
				));

		assertThat(orderEnding2.get(tms.get(0).getId())).isEqualTo(2);
		assertThat(orderEnding2.get(tms.get(1).getId())).isEqualTo(1);

		assertThat(orderEnding3.get(tms.get(2).getId())).isEqualTo(1);
		assertThat(orderEnding3.get(tms.get(1).getId())).isEqualTo(1);

		testHelper.clear();
	}

	//재고부족, 빈 장바구니, 이미 결제된 장바구니, 잘못된 menuId
	@Test
	@Transactional
	@DisplayName("재고 부족 테스트")
	void soldOutFood() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);
		Cookie authCookie = login();
		List<TheaterMenu> tms = theaterMenuRepository.findByTheater(theater);

		//주문DTO(수량보다 많이)
		FoodMenuAndQuantityDTO fdq1 = new FoodMenuAndQuantityDTO(tms.get(0).getId(), 5);
		FoodMenuAndQuantityDTO fdq2 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);


		FoodOrderRequestDTO for1 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq1, fdq2));
		String res = mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrDTO errDTO = objectMapper.readValue(res, ErrDTO.class);
		assertThat(errDTO.errCode()).isEqualTo("O001");
	}

	@Test
	@Transactional
	@DisplayName("음식 주문 테스트")
	void invalidAmountOrdering() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);
		Cookie authCookie = login();
		List<TheaterMenu> tms = theaterMenuRepository.findByTheater(theater);

		//주문DTO(수량보다 많이)
		FoodMenuAndQuantityDTO fdq1 = new FoodMenuAndQuantityDTO(tms.get(0).getId(), -2);
		FoodMenuAndQuantityDTO fdq2 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);


		FoodOrderRequestDTO for1 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq1, fdq2));
		String res = mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrDTO errDTO = objectMapper.readValue(res, ErrDTO.class);
		assertThat(errDTO.errCode()).isEqualTo("O002");
	}

	@Test
	@Transactional
	@DisplayName("빈 장바구니 주문하기")
	void payTwice() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);
		Cookie authCookie = login();
		List<TheaterMenu> tms = theaterMenuRepository.findByTheater(theater);

		//결제요청
		FoodMenuAndQuantityDTO fdq3 = new FoodMenuAndQuantityDTO(tms.get(2).getId(), 1);
		FoodMenuAndQuantityDTO fdq4 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);

		FoodOrderRequestDTO for2 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq3, fdq4));

		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for2)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie))
				.andExpect(status().isOk());

		//결제 후 빈 장바구니로 재시도
		String res = mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrDTO errDTO = objectMapper.readValue(res, ErrDTO.class);
		assertThat(errDTO.errCode()).isEqualTo("O003");
	}

	@Test
	@Transactional
	@DisplayName("장바구니에 담아둔 후 수량감소")
	void raceBuy() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);
		Cookie authCookie = login();    //세오스
		Cookie authCookie2 = login2();  //홍익대
		List<TheaterMenu> tms = theaterMenuRepository.findByTheater(theater);

		//세오스가 먼저 음식을 장바구니에 추가
		FoodMenuAndQuantityDTO fdq1 = new FoodMenuAndQuantityDTO(tms.get(2).getId(), 1);
		FoodMenuAndQuantityDTO fdq2 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 1);

		FoodOrderRequestDTO for1 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq1, fdq2));

		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isOk());

		//홍익대가 음식을 장바구니에 추가하고 주문. 재고 0
		FoodMenuAndQuantityDTO fdq3 = new FoodMenuAndQuantityDTO(tms.get(2).getId(), 3);
		FoodMenuAndQuantityDTO fdq4 = new FoodMenuAndQuantityDTO(tms.get(1).getId(), 3);

		FoodOrderRequestDTO for2 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq3, fdq4));

		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie2)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for2)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie2))
				.andExpect(status().isOk());

		//세오스가 결제에 실패함
		String res = mockmvc.perform(post("/api/foods/pay")
						.cookie(authCookie))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrDTO errDTO = objectMapper.readValue(res, ErrDTO.class);
		assertThat(errDTO.errCode()).isEqualTo("O001");
	}

	private Runnable createOrderingTask(Cookie authCookie,
										List<Integer> results,
										CountDownLatch readyLatch,
										CountDownLatch startLatch,
										CountDownLatch doneLatch) {
		return () -> {
			System.out.println("시작 >>> ");
			try {
				readyLatch.countDown();
				System.out.println("countdown >>> ");
				startLatch.await();
				System.out.println("await >>> ");

				int status = mockmvc.perform(post("/api/foods/pay")
								.cookie(authCookie))
						.andReturn()
						.getResponse()
						.getStatus();

				results.add(status);
				System.out.println("status >>> " + status);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				doneLatch.countDown();
			}
		};
	}

	@Test
	@DisplayName("RaceCondition 테스트")
	void orderingOnRaceCondition() throws Exception {
		testHelper.normalSetting();
		signup();
		Theater theater = testHelper.findAllTheater().get(0);
		List<TheaterMenu> tms = testHelper.findAllTheaterMenuByTheater(theater);
		Cookie authCookie1 = login();
		Cookie authCookie2 = login2();
		Cookie authCookie3 = login3();
		Cookie authCookie4 = login4();

		CountDownLatch readyLatch = new CountDownLatch(4);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(4);
		List<Integer> results = Collections.synchronizedList(new ArrayList<>());

		FoodMenuAndQuantityDTO fdq1 = new FoodMenuAndQuantityDTO(tms.get(0).getId(), 1);
		FoodOrderRequestDTO for1 = new FoodOrderRequestDTO(theater.getId(), List.of(fdq1));

		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie2)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie3)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isOk());

		mockmvc.perform(post("/api/foods/items")
						.cookie(authCookie4)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(for1)))
				.andExpect(status().isOk());

		new Thread(createOrderingTask(authCookie1, results, readyLatch, startLatch, doneLatch)).start();
		new Thread(createOrderingTask(authCookie2, results, readyLatch, startLatch, doneLatch)).start();
		new Thread(createOrderingTask(authCookie3, results, readyLatch, startLatch, doneLatch)).start();
		new Thread(createOrderingTask(authCookie4, results, readyLatch, startLatch, doneLatch)).start();

		readyLatch.await();
		startLatch.countDown();
		doneLatch.await();

		long success = results.stream().filter(s -> s == 200).count();

		assertThat(success).isGreaterThan(0);
		testHelper.clear();
	}

	@Test
	@Transactional
	@DisplayName("검색어로 영화관 조회")
	void searchAllTheaterWithQuery() throws Exception {
		normalSetting();

		String res = mockmvc.perform(get("/api/theater?query=명동"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		TheaterSearchResponseDTO resDTO = objectMapper.readValue(res, TheaterSearchResponseDTO.class);
		assertThat(resDTO.theater().size()).isGreaterThan(0);
	}

	@Test
	@Transactional
	@DisplayName("영화관 지역별조회")
	void searchTheaterWithRegion() throws Exception {
		String res = mockmvc.perform(get("/api/theater?region=SEOUL"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		TheaterSearchResponseDTO resDTO = objectMapper.readValue(res, TheaterSearchResponseDTO.class);
		resDTO.theater().forEach(r -> {
			assertThat(r.region()).isEqualTo(Region.SEOUL);
		});

		assertThat(resDTO.theater().size()).isEqualTo(theaterRepository.findAllByRegion(Region.SEOUL).size());
	}

	@Test
	@Transactional
	@DisplayName("영화관 전체조회")
	void searchAllTheater() throws Exception {
		normalSetting();

		String res = mockmvc.perform(get("/api/theater"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		TheaterSearchResponseDTO dto = objectMapper.readValue(res, TheaterSearchResponseDTO.class);
		assertThat(dto.theater().size()).isEqualTo(4);
	}

	@Test
	@Transactional
	@DisplayName("영화관 찜")
	void IlikeThisTheater() throws Exception {
		normalSetting();
		Cookie authCookie = login();
		Theater theater = theaterRepository.findAll().get(0);

		//아무것도 없을 때 찜한 영화 조회
		String res = mockmvc.perform(get("/api/theater/likes")
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		CheckLikedTheaterResponseDTO resDTO = objectMapper.readValue(res, CheckLikedTheaterResponseDTO.class);
		assertThat(resDTO.res().size()).isEqualTo(0);

		//영화 찜하기
		mockmvc.perform(post("/api/theater/likes"
						+ "?theaterId=" + theater.getId())
						.cookie(authCookie))
				.andExpect(status().isOk());

		System.out.println("난 찜했어 >>> ");

		//찜하기 후 재조회
		String res2 = mockmvc.perform(get("/api/theater/likes")
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		CheckLikedTheaterResponseDTO resDTO2 = objectMapper.readValue(res2, CheckLikedTheaterResponseDTO.class);
		assertThat(resDTO2.res().size()).isEqualTo(1);

		//취소
		mockmvc.perform(post("/api/theater/likes"
						+ "?theaterId=" + theater.getId())
						.cookie(authCookie))
				.andExpect(status().isOk());

		//재조회
		String res3 = mockmvc.perform(get("/api/theater/likes")
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		CheckLikedTheaterResponseDTO resDTO3 = objectMapper.readValue(res3, CheckLikedTheaterResponseDTO.class);
		assertThat(resDTO3.res().size()).isEqualTo(0);
	}

	@Test
	@Transactional
	@DisplayName("영화검색하기")
	void searchMovieWithTheater() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);

		String res = mockmvc.perform(get("/api/movie?searchQuery=" + "어벤져스"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		MovieSearchResponseDTO dto = objectMapper.readValue(res, MovieSearchResponseDTO.class);
		assertThat(dto.movie().size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("영화 전체검색")
	void searchAllMovieWithTheater() throws Exception {
		normalSetting();

		String res = mockmvc.perform(get("/api/movie"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		MovieSearchAllResponseDTO dto = objectMapper.readValue(res, MovieSearchAllResponseDTO.class);
		assertThat(dto.searchedMovies().size()).isGreaterThan(0);
	}

	@Test
	@Transactional
	@DisplayName("영화 상영 정보 확인")
	void findScreening() throws Exception {
		normalSetting();
		Theater theater = theaterRepository.findAll().get(0);
		Movie movie = movieRepository.findAll().get(0);

		String res = mockmvc.perform(get("/api/screen?theaterId=" + theater.getId()
						+ "&movieId=" + movie.getId()
						+ "&date=" + LocalDate.of(2026, 4, 28)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ScreeningSearchResponseDTO dto = objectMapper.readValue(res, ScreeningSearchResponseDTO.class);
		assertThat(dto.movieId()).isEqualTo(movie.getId());
		assertThat(dto.screen().size()).isGreaterThan(0);
	}

	@Test
	@Transactional
	@DisplayName("영화 예매")
	void reserveMovie() throws Exception {
		normalSetting();
		Screening screening = screeningRepository.findAll().get(0);
		System.out.println("screenings >>> " + screening.getId());
		Theater theater = screening.getScreen().getTheater();
		Movie movie = screening.getMovie();

		Cookie authCookie = login();

		//좌석조회
		String res = mockmvc.perform(get("/api/reservation?screeningId=" + screening.getId())
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		RemainingSeatsDTO dto = objectMapper.readValue(res, RemainingSeatsDTO.class);

		assertThat(dto.leavingSeatAmount()).isEqualTo(dto.totalSeatAmount());

		//예매
		ReservationRequestDTO r = ReservationRequestDTO.create(
				screening.getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		String resSeats = mockmvc.perform(post("/api/reservation/seats")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ReservationResponseDTO resDTO = objectMapper.readValue(resSeats, ReservationResponseDTO.class);
		long reservationId = resDTO.id();

		//좌석조회
		String res2 = mockmvc.perform(get("/api/screen?theaterId=" + theater.getId()
						+ "&movieId=" + movie.getId()
						+ "&date=" + LocalDate.of(2026, 5, 28)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ScreeningSearchResponseDTO dto2 = objectMapper.readValue(res2, ScreeningSearchResponseDTO.class);

		ScreeningWrapperDTO result2 = dto2.screen().stream()
				.flatMap(s -> s.screening().stream())
				.filter(ss -> ss.id() == (screening.getId()))
				.findFirst().get();

		assertThat(result2.leavingSeatAmount()).isEqualTo(result2.totalSeatAmount() - 2);
	}

	@Test
	@DisplayName("영화 결제")
	void payMovie() throws Exception {
		normalSetting();
		Screening screening = screeningRepository.findAll().get(0);
		Theater theater = screening.getScreen().getTheater();
		Movie movie = screening.getMovie();

		Cookie authCookie = login();

		ReservationRequestDTO r = ReservationRequestDTO.create(
				screening.getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		String resSeats = mockmvc.perform(post("/api/reservation/seats")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ReservationResponseDTO resDTO = objectMapper.readValue(resSeats, ReservationResponseDTO.class);
		long reservationId = resDTO.id();

		mockmvc.perform(post("/api/reservation?reservationId=" + reservationId)
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
	}

	@Test
	@Transactional
	@DisplayName("두 개의 계정이 같은 예약을 예매하고 결제하려함")
	void doublePay() throws Exception {
		normalSetting();
		Screening screening = screeningRepository.findAll().get(0);
		Theater theater = screening.getScreen().getTheater();
		Movie movie = screening.getMovie();

		Cookie authCookie = login();
		Cookie authCookie2 = login2();

		ReservationRequestDTO r = ReservationRequestDTO.create(
				screening.getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		String resSeats = mockmvc.perform(post("/api/reservation/seats")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ReservationResponseDTO resDTO = objectMapper.readValue(resSeats, ReservationResponseDTO.class);
		long reservationId = resDTO.id();

		String res = mockmvc.perform(post("/api/reservation?reservationId=" + reservationId)
						.cookie(authCookie2))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrDTO errDTO = objectMapper.readValue(res, ErrDTO.class);
		assertThat(errDTO.errCode()).isEqualTo("R004");

	}

	@Test
	@Transactional
	@DisplayName("영화 취소")
	void cancelReservation() throws Exception {
		normalSetting();
		Screening screening = screeningRepository.findAll().get(0);
		Theater theater = screening.getScreen().getTheater();
		Movie movie = screening.getMovie();

		Cookie authCookie = login();

		//결제
		ReservationRequestDTO r = ReservationRequestDTO.create(
				screening.getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		String resSeats = mockmvc.perform(post("/api/reservation/seats")
						.cookie(authCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(r)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ReservationResponseDTO resDTO = objectMapper.readValue(resSeats, ReservationResponseDTO.class);
		long reservationId = resDTO.id();

		mockmvc.perform(post("/api/reservation?reservationId=" + reservationId)
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		//좌석조회
		String res = mockmvc.perform(get("/api/reservation?screeningId=" + screening.getId())
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		RemainingSeatsDTO dto = objectMapper.readValue(res, RemainingSeatsDTO.class);

		assertThat(dto.leavingSeatAmount()).isEqualTo(dto.totalSeatAmount() - 2);

		//취소
		mockmvc.perform(delete("/api/reservation/" + screening.getId())
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		//좌석조회
		String res2 = mockmvc.perform(get("/api/reservation?screeningId=" + screening.getId())
						.cookie(authCookie))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		RemainingSeatsDTO dto2 = objectMapper.readValue(res2, RemainingSeatsDTO.class);

		assertThat(dto2.leavingSeatAmount()).isEqualTo(dto.totalSeatAmount());
	}

	private Runnable reservingTask(Cookie authCookie,
								   ReservationRequestDTO r,
								   List<Integer> results,
								   CountDownLatch readyLatch,
								   CountDownLatch startLatch,
								   CountDownLatch doneLatch) {
		return () -> {
			System.out.println("시작 >>> ");
			try {
				readyLatch.countDown();
				System.out.println("countdown >>> ");
				startLatch.await();
				System.out.println("await >>> ");

				int status = mockmvc.perform(post("/api/reservation/seats")
								.cookie(authCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(r)))
						.andReturn()
						.getResponse()
						.getStatus();

				results.add(status);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				doneLatch.countDown();
			}
		};
	}

	@Test
	@DisplayName("raceCondition")
	void reservingInRaceCondition() throws Exception {
		normalSetting();
		Screening screening = testHelper.getScreening();

		Cookie authCookie1 = login();
		Cookie authCookie2 = login2();
		Cookie authCookie3 = login3();
		Cookie authCookie4 = login4();

		CountDownLatch readyLatch = new CountDownLatch(4);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(4);
		List<Integer> results = Collections.synchronizedList(new ArrayList<>());

		ReservationRequestDTO r = ReservationRequestDTO.create(
				screening.getId(),
				Arrays.asList(ReservationSeatInfo.create("C16", SeatInfo.ADULT),
						ReservationSeatInfo.create("C17", SeatInfo.CHILD))
		);

		new Thread(reservingTask(authCookie1, r, results, readyLatch, startLatch, doneLatch)).start();
		new Thread(reservingTask(authCookie2, r, results, readyLatch, startLatch, doneLatch)).start();
		new Thread(reservingTask(authCookie3, r, results, readyLatch, startLatch, doneLatch)).start();
		new Thread(reservingTask(authCookie4, r, results, readyLatch, startLatch, doneLatch)).start();

		readyLatch.await();
		startLatch.countDown();
		doneLatch.await();

		long success = results.stream().filter(s -> s == 200).count();

		assertThat(success).isGreaterThan(0);
		testHelper.clear();
	}
}