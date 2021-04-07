package com.ynov.dystraite.entities.maximots;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "libelle")
    private String libelle;

    @OneToMany
    private List<Word> words;

    @ManyToMany
    private List<Category> categories;

    public Theme(String libelle){
        this.libelle = libelle;
    }

    public Theme(String libelle, List<Word> words, List<Category> categories){
        this.libelle = libelle;
        this.words = words;
        this.categories = categories;
    }
}
