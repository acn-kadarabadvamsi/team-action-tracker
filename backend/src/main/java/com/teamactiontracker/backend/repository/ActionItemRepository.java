
package com.teamactiontracker.backend.repository;

import com.teamactiontracker.backend.model.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionItemRepository
        extends JpaRepository<ActionItem, Long> {

    List<ActionItem> findByAssignee_UserId(Long userId);
}
