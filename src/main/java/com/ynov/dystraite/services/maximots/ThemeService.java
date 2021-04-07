package com.ynov.dystraite.services.maximots;

import com.ynov.dystraite.entities.maximots.Category;
import com.ynov.dystraite.entities.maximots.Theme;
import com.ynov.dystraite.entities.maximots.Word;
import com.ynov.dystraite.repositories.maximots.ThemeRepository;
import com.ynov.dystraite.repositories.maximots.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ThemeService {
    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    WordRepository wordRepository;

    public Theme getById(long id) {
        Optional<Theme> themeOpt = themeRepository.findById(id);
        return themeOpt.orElse(null);
    }

    public List<Theme> getAll() {
        return themeRepository.findAll();
    }

    public Theme delete(long id) {
        Optional<Theme> themeOpt = themeRepository.findById(id);
        if(themeOpt.isPresent()) {
            themeRepository.deleteById(id);
            return themeOpt.get();
        }else {
            return null;
        }
    }

    public Theme update(long id, Theme newTheme) {
        Optional<Theme> themeOpt = themeRepository.findById(id);
        if(themeOpt.isPresent()) {

            //libelle
            if (newTheme.getLibelle() != null && newTheme.getLibelle().trim().length() > 0){
                themeOpt.get().setLibelle(newTheme.getLibelle());
            }

            return themeRepository.save(themeOpt.get());
        }
        return null;
    }

    public Theme create(Theme newTheme) {
        if (newTheme.getLibelle() != null && newTheme.getLibelle().trim().length() > 0) {
            return themeRepository.save(newTheme);
        }
        return null;
    }

    public Theme addWords(long themeId, long[] wordsId){
        Optional<Theme> themeOpt = themeRepository.findById(themeId);
        if(themeOpt.isPresent()) {

            for (long wordId : wordsId ) {
                Optional<Word> wordOpt = wordRepository.findById(wordId);
                wordOpt.ifPresent( (w) -> {
                    if (!themeOpt.get().getWords().contains(w)){
                        themeOpt.get().getWords().add(w);
                    }
                });
            }

            try {
                return themeRepository.save(themeOpt.get());
            } catch (DataIntegrityViolationException e) {
                log.info(e.getLocalizedMessage());
            }

        }
        return null;
    }

    public Theme removeWords(long themeId, long[] wordsId){
        Optional<Theme> themeOpt = themeRepository.findById(themeId);
        if(themeOpt.isPresent()) {

            for (long wordId : wordsId ) {
                Optional<Word> wordOpt = wordRepository.findById(wordId);
                wordOpt.ifPresent( (w) -> {
                    themeOpt.get().getWords().remove(w);
                });
            }

            try {
                return themeRepository.save(themeOpt.get());
            } catch (DataIntegrityViolationException e) {
                log.info(e.getLocalizedMessage());
            }

        }
        return null;
    }

    public List<Theme> getByCategories(List<Category> categories){
        return this.themeRepository.findByCategoriesIn(categories);
    }
}
