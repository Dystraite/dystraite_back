package com.ynov.dystraite.controllers.maximots;

import com.ynov.dystraite.entities.maximots.Category;
import com.ynov.dystraite.entities.maximots.Theme;
import com.ynov.dystraite.entities.maximots.Word;
import com.ynov.dystraite.services.maximots.CategoryService;
import com.ynov.dystraite.services.maximots.ThemeService;
import com.ynov.dystraite.services.maximots.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "maximots/theme")
public class ThemeController {

    @Autowired
    ThemeService themeService;

    @Autowired
    WordService wordService;

    @Autowired
    CategoryService categoryService;

    @RequestMapping(value = "/init", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void init() {

        List<Word> words = new ArrayList<>(
                Arrays.asList(
                        wordService.create(new Word("Jupe")),
                        wordService.create(new Word("Pantalon")),
                        wordService.create(new Word("Chaussures")),
                        wordService.create(new Word("Pull")),
                        wordService.create(new Word("Short")),
                        wordService.create(new Word("Chemise")),
                        wordService.create(new Word("Chaussettes")),
                        wordService.create(new Word("Gant")),
                        wordService.create(new Word("Bonnet")),
                        wordService.create(new Word("Manteau"))
                )
        );
        List<Category> categories = new ArrayList<>(
                Arrays.asList(
                        categoryService.create(new Category("Jupe")),
                        categoryService.create(new Category("Pantalon"))
                )
        );
        Theme t = new Theme("Vêtements", words, categories);
        themeService.create(t);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Theme getById(@PathVariable long id) {
        return themeService.getById(id);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Theme> getAll() {
        return themeService.getAll();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Theme create(@RequestBody Theme theme) {
        return themeService.create(theme);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Theme delete(@PathVariable long id) {
        return themeService.delete(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Theme update(@PathVariable long id, @RequestBody Theme theme) {
        return themeService.update(id, theme);
    }

    @RequestMapping(value = "/{id}/word", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Theme addWords(@PathVariable long id, @RequestBody long[] wordsId) {
        return themeService.addWords(id, wordsId);
    }

    @RequestMapping(value = "/{id}/word", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Theme removeWords(@PathVariable long id, @RequestBody long[] wordsId) {
        return themeService.removeWords(id, wordsId);
    }
}
