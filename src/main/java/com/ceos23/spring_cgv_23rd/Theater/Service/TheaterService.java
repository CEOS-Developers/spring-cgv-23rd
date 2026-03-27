package com.ceos23.spring_cgv_23rd.Theater.Service;

import com.ceos23.spring_cgv_23rd.Theater.DTO.Response.*;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Region;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Repository.BookmarkedTheaterRepository;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.BookmarkedTheater;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TheaterService {
    TheaterRepository theaterRepository;
    BookmarkedTheaterRepository bookmarkedTheaterRepository;
    UserRepository userRepository;

    public TheaterService(TheaterRepository theaterRepository,
                   BookmarkedTheaterRepository bookmarkedTheaterRepository, UserRepository userRepository){
        this.theaterRepository = theaterRepository;
        this.bookmarkedTheaterRepository = bookmarkedTheaterRepository;
        this.userRepository = userRepository;
    }

    /**
     * 검색어로 극장 조회
     *
     * @param query 검색어
     * @return 검색결과. 극장의 id값과 이름값
     */
    @Transactional
    public ResponseEntity<TheaterSearchResponseDTO> theaterSearchService(String query){
        List<Theater> searchedTheater = theaterRepository.findByNameContaining(query);

        TheaterSearchResponseDTO responseDTO = TheaterSearchResponseDTO.builder()
                .theater(TheaterWrapperDTO.create(searchedTheater))
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 극장 전체조회
     *
     * @return 전체 극장의 id값과 이름값
     */
    @Transactional(readOnly = true)
    public ResponseEntity<TheaterSearchResponseDTO> theaterSearchService(){
        List<Theater> searchedTheaters = theaterRepository.findAll();

        TheaterSearchResponseDTO responseDTO = TheaterSearchResponseDTO.builder()
                .theater(TheaterWrapperDTO.create(searchedTheaters))
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 지역별 극장 조회(경기, 서울 등등...)
     *
     * @param reg 지역명
     *            반드시 SEOUL, GYEONGGI, INCHEON, GANGWON, CHUNGCHEONG, DAEGU, BUSAN_ULSAN, GYEONGSANG, HONAM_JEJU
     *            중 하나여야함.
     * @return 영화관 검색결과. id값과 이름 필드
     */
    @Transactional(readOnly = true)
    public ResponseEntity<TheaterSearchResponseDTO> theaterSearchService(Region reg){
        List<Theater> searchedTheaters = theaterRepository.findByRegion(reg);

        TheaterSearchResponseDTO responseDTO = TheaterSearchResponseDTO.builder()
                .theater(TheaterWrapperDTO.create(searchedTheaters))
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 영화관 찜하기
     * 이미 찜된 경우 취소
     *
     * @param theaterId 영화관 ID
     * @param userId 사용자 ID
     * @return 영화관 검색결과. id값과 이름 필드
     */
    @Transactional()
    public LikedTheaterResponseDTO theaterBookMarkService(long userId, long theaterId){
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 ID의 영화관이 없습니다.")
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 ID의 사용자가 없습니다.")
        );

        Optional<BookmarkedTheater> theaterOptional = bookmarkedTheaterRepository.findByTheaterAndUser(theater, user);

        if (theaterOptional.isPresent()){ //이미 있음, 취소
            bookmarkedTheaterRepository.deleteBookmarkedTheaterById(theaterOptional.get().getId());

            return LikedTheaterResponseDTO.create(
                    RequestType.DELETE,theaterOptional.get().getTheater()
            );
        } else { //없음, 새로이 예약
            BookmarkedTheater bmt = BookmarkedTheater.create(theater, user);

            bookmarkedTheaterRepository.save(bmt);

            return LikedTheaterResponseDTO.create(
                    RequestType.ADD, theater
            );
        }
    }

    /**
     * 찜한 영화관 리스트 보기
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public CheckLikedTheaterResponseDTO checkTheaterBookMark(long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 ID의 사용자가 없습니다.")
        );

        List<BookmarkedTheater> bookmarkedTheaters = bookmarkedTheaterRepository.findByUser(user);

        List<Theater> theaters = bookmarkedTheaters.stream()
                .map(BookmarkedTheater::getTheater)
                .toList();

        return CheckLikedTheaterResponseDTO.create(
                user, TheaterWrapperDTO.create(theaters)
        );
    }
}
