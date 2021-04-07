package com.ynov.dystraite.repositories.maximots;

import com.ynov.dystraite.entities.maximots.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByIdIn(List<Long> ids);
}
