package com.cgv.spring_boot.domain.schedule.exception;

import com.cgv.spring_boot.global.common.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements BaseErrorCode {
    SCHEDULE_NOT_FOUND(404, "해당 영화 스케줄을 찾을 수 없습니다.");

    private final int status;
    private final String message;
}
