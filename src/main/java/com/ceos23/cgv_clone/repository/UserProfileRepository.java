package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
