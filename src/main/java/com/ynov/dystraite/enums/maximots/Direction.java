package com.ynov.dystraite.enums.maximots;

public enum Direction {
    Normal,
    Inverse;

    public static Direction getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
}
