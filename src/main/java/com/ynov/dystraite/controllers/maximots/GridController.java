package com.ynov.dystraite.controllers.maximots;

import com.ynov.dystraite.entities.maximots.Grid;
import com.ynov.dystraite.models.maximots.SortieGetGrid;
import com.ynov.dystraite.services.maximots.GridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "maximots/gameplay")
public class GridController {

    @Autowired
    GridService gridService;

    @RequestMapping(value = "/getGrid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SortieGetGrid> getGrid(@RequestBody int difficulty, Authentication authentication) throws NoSuchAlgorithmException {
        return gridService.createBoard(difficulty, authentication);
    }

    @RequestMapping(value = "/verifyResponse", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SortieGetGrid> verifyResponse(@RequestBody int difficulty, Authentication authentication) throws NoSuchAlgorithmException {
        return gridService.createBoard(difficulty, authentication);
    }

    @RequestMapping(value = "/init", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void init() {

        List<String> words = new ArrayList<>(
                Arrays.asList(
                        "Jupe",
                        "Pantalon",
                        "Chaussures",
                        "Pull",
                        "Short",
                        "Chemise",
                        "Chaussettes",
                        "Gant",
                        "Bonnet",
                        "Manteau"
                )
        );
        Grid grid = new Grid("Vêtements", words, 10);
        gridService.populate(grid);
    }

}
