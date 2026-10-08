package com.teamactiontracker.backend.repository;

import com.teamactiontracker.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}