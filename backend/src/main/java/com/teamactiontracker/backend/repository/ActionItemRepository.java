package com.teamactiontracker.backend.repository;

import com.teamactiontracker.backend.model.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

}