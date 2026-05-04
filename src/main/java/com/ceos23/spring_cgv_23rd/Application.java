package com.ceos23.spring_cgv_23rd;

import com.ceos23.spring_cgv_23rd.Actor.Domain.Actor;
import com.ceos23.spring_cgv_23rd.Actor.Domain.ActorInfo;
import com.ceos23.spring_cgv_23rd.Actor.Repository.ActorInterface;
import com.ceos23.spring_cgv_23rd.Movie.Domain.AccessibleAge;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Comment;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Domain.MovieType;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@EnableScheduling
@SpringBootApplication
@EnableFeignClients
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
