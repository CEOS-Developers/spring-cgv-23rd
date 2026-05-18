package com.ceos23.spring_boot.cgv.service.screening;

import com.ceos23.spring_boot.cgv.dto.screening.SeatAvailabilityResponse.SeatTemplateSnapshot;
import com.ceos23.spring_boot.cgv.global.cache.CacheNames;
import com.ceos23.spring_boot.cgv.repository.cinema.SeatTemplateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatTemplateCacheService {

    private final SeatTemplateRepository seatTemplateRepository;

    @Cacheable(cacheNames = CacheNames.SEAT_TEMPLATES, key = "#seatLayoutId")
    public List<SeatTemplateSnapshot> getSeatTemplates(Long seatLayoutId) {
        return seatTemplateRepository.findAllBySeatLayoutIdOrderByRowNameAscColNumberAsc(seatLayoutId)
                .stream()
                .map(SeatTemplateSnapshot::from)
                .toList();
    }
}
