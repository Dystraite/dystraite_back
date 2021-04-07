package com.ynov.dystraite.repositories.maximots;

import com.ynov.dystraite.entities.maximots.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
}
