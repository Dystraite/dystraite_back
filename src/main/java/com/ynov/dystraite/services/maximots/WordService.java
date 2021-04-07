package com.ynov.dystraite.services.maximots;

import com.ynov.dystraite.entities.maximots.Word;
import com.ynov.dystraite.repositories.maximots.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WordService {
    @Autowired
    WordRepository wordRepository;

    public Word getById(long id) {
        Optional<Word> wordOpt = wordRepository.findById(id);
        return wordOpt.orElse(null);
    }

    public List<Word> getAll() {
        return wordRepository.findAll();
    }

    public Word delete(long id) {
        Optional<Word> wordOpt = wordRepository.findById(id);
        if(wordOpt.isPresent()) {
            wordRepository.deleteById(id);
            return wordOpt.get();
        }else {
            return null;
        }
    }

    public Word update(long id, Word newWord) {
        Optional<Word> wordOpt = wordRepository.findById(id);
        if(wordOpt.isPresent()) {
            if (newWord.getWord() != null && newWord.getWord().trim().length() > 0){
                wordOpt.get().setWord(newWord.getWord());
                return wordRepository.save(wordOpt.get());
            }
        }
        return null;
    }

    public Word create(Word newWord) {
        if (newWord.getWord() != null && newWord.getWord().trim().length() > 0) {
            return wordRepository.save(newWord);
        }
        return null;
    }
}
