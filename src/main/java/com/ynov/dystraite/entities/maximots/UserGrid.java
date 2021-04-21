package com.ynov.dystraite.entities.maximots;

import com.ynov.dystraite.entities.Users;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"user_id", "grid_id"})})
public class UserGrid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "grid_id")
    private Grid grid;

    @Column(name = "finish")
    private boolean finish;

    @Column(name = "found_words")
    private String foundWords;
}
