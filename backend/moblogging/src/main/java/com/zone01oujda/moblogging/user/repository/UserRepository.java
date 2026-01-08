package com.zone01oujda.moblogging.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zone01oujda.moblogging.entity.User;

public interface  UserRepository extends JpaRepository<User, Long> {
    Optional<User> fidByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
