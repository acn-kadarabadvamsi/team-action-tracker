package com.teamactiontracker.backend.repository;

import com.teamactiontracker.backend.model.ActionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionHistoryRepository extends JpaRepository<ActionHistory, Long> {
}