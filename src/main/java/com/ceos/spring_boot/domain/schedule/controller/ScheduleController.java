package com.ceos.spring_boot.domain.schedule.controller;

import com.ceos.spring_boot.domain.schedule.dto.ScheduleRequest;
import com.ceos.spring_boot.domain.schedule.dto.ScheduleResponse;
import com.ceos.spring_boot.domain.schedule.service.ScheduleService;
import com.ceos.spring_boot.global.codes.SuccessCode;
import com.ceos.spring_boot.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Schedule 관련 API", description = "상영 일정 관리를 위한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "스케줄 생성", description = "영화와 상영관 ID, 시작 시간을 입력해 스케줄을 생성합니다.")
    public ResponseEntity<ApiResponse<ScheduleResponse>> create(@RequestBody @Valid ScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.of(scheduleService.createSchedule(request), SuccessCode.INSERT_SUCCESS));
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "스케줄 삭제", description = "해당 스케줄을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(ApiResponse.of(null, SuccessCode.DELETE_SUCCESS));
    }
}