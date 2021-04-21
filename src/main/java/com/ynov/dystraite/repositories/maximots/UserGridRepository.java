package com.ynov.dystraite.repositories.maximots;

import com.ynov.dystraite.entities.maximots.Grid;
import com.ynov.dystraite.entities.maximots.UserGrid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserGridRepository extends JpaRepository<UserGrid, Long> {
    Optional<UserGrid> findByUserIdAndGridId(long user_id, long grid_id);

    Set<UserGrid> findByUserId(long user_id);
}
