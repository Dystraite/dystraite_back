package com.ynov.dystraite.repositories.maximots;

import com.ynov.dystraite.entities.maximots.Category;
import com.ynov.dystraite.entities.maximots.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

    List<Theme> findByCategoriesIn(List<Category> categories);
}
