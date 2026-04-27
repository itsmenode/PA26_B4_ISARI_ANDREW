package org.example.bunny.model;

import lombok.Getter;

@Getter
public class Maze {

    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    public Maze(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Maze dimensions must be positive");
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public Cell neighbour(Cell cell, Direction d) {
        int nr = cell.getRow() + d.getDRow();
        int nc = cell.getCol() + d.getDCol();
        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
            return null;
        }
        return grid[nr][nc];
    }

    public void removeWallBetween(Cell a, Cell b) {
        Direction d = directionBetween(a, b);
        a.setWall(d, false);
        b.setWall(d.opposite(), false);
    }

    public void resetAllWalls() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c].resetWalls();
            }
        }
    }

    private Direction directionBetween(Cell a, Cell b) {
        int dr = b.getRow() - a.getRow();
        int dc = b.getCol() - a.getCol();
        if (dr == -1 && dc == 0) return Direction.TOP;
        if (dr == 1 && dc == 0) return Direction.BOTTOM;
        if (dr == 0 && dc == -1) return Direction.LEFT;
        if (dr == 0 && dc == 1) return Direction.RIGHT;
        throw new IllegalArgumentException("Cells " + a + " and " + b + " are not adjacent");
    }
}
