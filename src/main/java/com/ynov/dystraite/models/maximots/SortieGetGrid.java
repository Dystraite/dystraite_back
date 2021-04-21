package com.ynov.dystraite.models.maximots;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class SortieGetGrid {
    private ArrayList<Character> grid;
    private List<String> wordsHash;
    private long gridId;
    private String gridLabel;
}
