package com.ceos23.cgv_clone.theater.controller;

import com.ceos23.cgv_clone.global.response.ApiResponse;
import com.ceos23.cgv_clone.global.response.SuccessCode;
import com.ceos23.cgv_clone.theater.dto.response.ScheduleResponse;
import com.ceos23.cgv_clone.theater.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ApiResponse<List<ScheduleResponse>> getSchedules(
            @RequestParam Long movieId,
            @RequestParam Long theaterId
    ) {
        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, scheduleService.getSchedule(movieId, theaterId));
    }
}
