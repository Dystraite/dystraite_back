package com.ynov.dystraite.models.maximots;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class SortieVerifyResponse {
    private SortieGetGrid sortieGetGrid;
    private boolean finish;
}
