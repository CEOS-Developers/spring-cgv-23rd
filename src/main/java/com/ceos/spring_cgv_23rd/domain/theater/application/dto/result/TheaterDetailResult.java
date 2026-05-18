package com.ceos.spring_cgv_23rd.domain.theater.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.theater.domain.Theater;

public record TheaterDetailResult(
	Long id,
	String name,
	String address,
	String description,
	boolean isOpened
) {
	public static TheaterDetailResult from(Theater theater) {
		return new TheaterDetailResult(
			theater.getId(),
			theater.getName(),
			theater.getAddress(),
			theater.getDescription(),
			theater.isOpened()
		);
	}
}
