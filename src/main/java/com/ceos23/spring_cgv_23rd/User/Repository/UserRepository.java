package com.ceos23.spring_cgv_23rd.User.Repository;

import com.ceos23.spring_cgv_23rd.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPassword(String password);

    User findByUsername(String username);
}
