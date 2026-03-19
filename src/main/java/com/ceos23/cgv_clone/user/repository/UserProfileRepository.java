package com.ceos23.cgv_clone.user.repository;

import com.ceos23.cgv_clone.user.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
