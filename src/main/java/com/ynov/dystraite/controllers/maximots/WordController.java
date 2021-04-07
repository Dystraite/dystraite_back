package com.ynov.dystraite.controllers.maximots;

import com.ynov.dystraite.entities.maximots.Word;
import com.ynov.dystraite.services.maximots.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "maximots/word")
public class WordController {

    @Autowired
    WordService wordService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Word getById(@PathVariable long id) {
        return wordService.getById(id);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Word> getAll() {
        return wordService.getAll();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Word create(@RequestBody Word word) {
        return wordService.create(word);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Word delete(@PathVariable long id) {
        return wordService.delete(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Word update(@PathVariable long id, @RequestBody Word word) {
        return wordService.update(id, word);
    }

}
