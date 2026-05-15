package com.ceos.spring_cgv_23rd.domain.movie.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.movie.domain.MediaType;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieMedia;

public record MovieMediaResult(
	Long id,
	MediaType mediaType,
	String mediaUrl
) {
	public static MovieMediaResult from(MovieMedia media) {
		return new MovieMediaResult(
			media.getId(),
			media.getMediaType(),
			media.getMediaUrl()
		);
	}
}
