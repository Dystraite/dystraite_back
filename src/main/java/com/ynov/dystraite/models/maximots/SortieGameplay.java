package com.ynov.dystraite.models.maximots;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class SortieGameplay {
    private ArrayList<Character> grid;
    private List<String> words;
}
