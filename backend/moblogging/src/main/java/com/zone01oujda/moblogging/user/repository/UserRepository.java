package com.zone01oujda.moblogging.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.user.enums.Role;

public interface  UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByBannedTrue();
    long countByRole(Role role);
}
