package com.example.maze.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"row", "col"})
public class Cell implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int row;
    private final int col;

    private boolean topWall;
    private boolean rightWall;
    private boolean bottomWall;
    private boolean leftWall;

    public static Cell withAllWalls(int row, int col) {
        return new Cell(row, col, true, true, true, true);
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
}
