package com.ynov.dystraite.entities.maximots;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Grid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NonNull
    @Column(name = "libelle")
    private String libelle;

    @NonNull
    @Column(name = "words")
    @ElementCollection
    private List<String> words;

    @NonNull
    @Column(name = "difficulty")
    private int difficulty;
}
