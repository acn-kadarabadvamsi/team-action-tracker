
package com.teamactiontracker.backend.repository;

import com.teamactiontracker.backend.model.ActionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionHistoryRepository
        extends JpaRepository<ActionHistory, Long> {

    List<ActionHistory> findByActionItem_ActionItemIdOrderByUpdatedAtDesc(
            Long actionItemId);
}
