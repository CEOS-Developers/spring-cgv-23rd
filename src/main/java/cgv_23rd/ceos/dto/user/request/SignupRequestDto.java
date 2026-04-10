package cgv_23rd.ceos.dto.user.request;

import lombok.Getter;

import java.time.LocalDate;

public record SignupRequestDto(String name, String email,
                               LocalDate birth, String phone,
                               String password) {
}
