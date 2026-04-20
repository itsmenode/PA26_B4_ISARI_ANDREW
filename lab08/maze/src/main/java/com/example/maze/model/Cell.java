package com.example.maze.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"row", "col"})
public class Cell {

    private final int row;
    private final int col;

    private boolean topWall;
    private boolean rightWall;
    private boolean bottomWall;
    private boolean leftWall;

    public static Cell withAllWalls(int row, int col) {
        return new Cell(row, col, true, true, true, true);
    }
}
