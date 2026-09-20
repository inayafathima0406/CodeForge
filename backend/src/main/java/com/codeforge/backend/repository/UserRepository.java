package com.codeforge.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeforge.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Loads the role in the same query, so no lazy proxy is left behind
    @Query("select u from User u join fetch u.role where u.username = :username")
    Optional<User> findWithRoleByUsername(@Param("username") String username);
}