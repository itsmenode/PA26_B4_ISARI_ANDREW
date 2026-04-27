package com.example.maze.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
public class Maze implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    @Setter private Cell startCell;
    @Setter private Cell endCell;

    public Maze(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Maze dimensions must be positive");
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        resetAllWalls();
    }

    public void resetAllWalls() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = Cell.withAllWalls(r, c);
            }
        }
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Cell (" + row + "," + col + ") out of bounds");
        }
        return grid[row][col];
    }

    public void removeWallBetween(Cell a, Cell b) {
        setSharedWall(a, b, false);
    }

    public void toggleWall(Cell cell, Direction side) {
        Cell neighbour = neighbourOf(cell, side);
        if (neighbour == null) {
            return;
        }
        boolean newValue = !cell.hasWall(side);
        setSharedWall(cell, neighbour, newValue);
    }

    private Cell neighbourOf(Cell cell, Direction side) {
        int r = cell.getRow();
        int c = cell.getCol();
        return switch (side) {
            case TOP    -> r > 0          ? grid[r - 1][c] : null;
            case BOTTOM -> r < rows - 1   ? grid[r + 1][c] : null;
            case LEFT   -> c > 0          ? grid[r][c - 1] : null;
            case RIGHT  -> c < cols - 1   ? grid[r][c + 1] : null;
        };
    }

    private void setSharedWall(Cell a, Cell b, boolean present) {
        int dr = b.getRow() - a.getRow();
        int dc = b.getCol() - a.getCol();

        if (dr == -1 && dc == 0) {
            a.setTopWall(present);
            b.setBottomWall(present);
        } else if (dr == 1 && dc == 0) {
            a.setBottomWall(present);
            b.setTopWall(present);
        } else if (dr == 0 && dc == -1) {
            a.setLeftWall(present);
            b.setRightWall(present);
        } else if (dr == 0 && dc == 1) {
            a.setRightWall(present);
            b.setLeftWall(present);
        } else {
            throw new IllegalArgumentException(
                    "Cells " + a + " and " + b + " are not orthogonally adjacent");
        }
    }
}
