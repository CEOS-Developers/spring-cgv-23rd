package com.ceos.spring_cgv_23rd.domain.theater.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.theater.domain.Theater;

public record TheaterResult(
	Long id,
	String name,
	String address,
	boolean isOpened
) {
	public static TheaterResult from(Theater theater) {
		return new TheaterResult(
			theater.getId(),
			theater.getName(),
			theater.getAddress(),
			theater.isOpened()
		);
	}
}
