package com.ynov.dystraite.repositories.maximots;

import com.ynov.dystraite.entities.maximots.Grid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GridRepository extends JpaRepository<Grid, Long> {
    Grid findFirstByIdNotInOrderByDifficultyAsc(List<Long> gridList);

    Grid findFirstByOrderByDifficultyAsc();
}
