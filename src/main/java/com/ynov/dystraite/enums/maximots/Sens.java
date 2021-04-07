package com.ynov.dystraite.enums.maximots;

public enum Sens {
    Vertical,
    Horizontal,
    Diagonale_BasGauche_HautDroite,
    Diagonale_HautGauche_BasDroite;

    public static Sens getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
}
