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
    @Column(name = "label")
    private String label;

    @NonNull
    @Column(name = "words")
    @ElementCollection
    private List<String> words;

    @NonNull
    @Column(name = "difficulty")
    private int difficulty;

    @OneToMany(mappedBy = "grid")
    @Column(name = "user_grid_id")
    private List<UserGrid> userGrids;
}
