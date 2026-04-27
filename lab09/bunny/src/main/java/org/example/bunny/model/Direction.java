package org.example.bunny.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Direction {
    TOP(-1, 0),
    RIGHT(0, 1),
    BOTTOM(1, 0),
    LEFT(0, -1);

    private final int dRow;
    private final int dCol;

    public Direction opposite() {
        return switch (this) {
            case TOP -> BOTTOM;
            case BOTTOM -> TOP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
