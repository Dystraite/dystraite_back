package com.ynov.dystraite.models.maximots;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class EntreeVerifyResponse {
    public long gridId;
    public List<String> words;
}
