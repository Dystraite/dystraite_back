package com.ynov.dystraite.controllers.maximots;

import com.ynov.dystraite.entities.maximots.Theme;
import com.ynov.dystraite.services.maximots.GameplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "maximots/gameplay")
public class GameplayController {

    @Autowired
    GameplayService gameplayService;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Theme> getBoard(@RequestBody List<Long> categoryIds) {
        return gameplayService.createBoard(categoryIds, 10);
    }

}
