package org.example.bunny.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = {"row", "col"})
@ToString(of = {"row", "col"})
public class Cell {

    private final int row;
    private final int col;
    private boolean topWall = true;
    private boolean rightWall = true;
    private boolean bottomWall = true;
    private boolean leftWall = true;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean hasWall(Direction d) {
        return switch (d) {
            case TOP -> topWall;
            case RIGHT -> rightWall;
            case BOTTOM -> bottomWall;
            case LEFT -> leftWall;
        };
    }

    public void setWall(Direction d, boolean present) {
        switch (d) {
            case TOP -> topWall = present;
            case RIGHT -> rightWall = present;
            case BOTTOM -> bottomWall = present;
            case LEFT -> leftWall = present;
        }
    }

    public void resetWalls() {
        topWall = rightWall = bottomWall = leftWall = true;
    }
}
