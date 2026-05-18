package com.ceos.spring_cgv_23rd.domain.movie.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieCredit;
import com.ceos.spring_cgv_23rd.domain.movie.domain.RoleType;

public record MovieCreditResult(
	Long contributorId,
	String name,
	String profileImageUrl,
	RoleType roleType
) {
	public static MovieCreditResult from(MovieCredit credit) {
		return new MovieCreditResult(
			credit.getContributor().getId(),
			credit.getContributor().getName(),
			credit.getContributor().getProfileImageUrl(),
			credit.getRoleType()
		);
	}
}
