package com.example.maze.model;

import lombok.Getter;

/**
 * Rectangular grid of {@link Cell}s.
 *
 * <p>Maintains the invariant that shared walls between adjacent cells
 * are always in sync: removing the right wall of (r,c) also removes
 * the left wall of (r,c+1), and vice versa.</p>
 */
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
        resetAllWalls();
    }

    /** Restore every wall of every cell. */
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

    /**
     * Remove the wall shared by two orthogonally adjacent cells.
     * Both cells have their corresponding wall flipped off, keeping
     * the representation consistent.
     *
     * @throws IllegalArgumentException if the cells are not orthogonal neighbours
     */
    public void removeWallBetween(Cell a, Cell b) {
        int dr = b.getRow() - a.getRow();
        int dc = b.getCol() - a.getCol();

        if (dr == -1 && dc == 0) {          // b is above a
            a.setTopWall(false);
            b.setBottomWall(false);
        } else if (dr == 1 && dc == 0) {    // b is below a
            a.setBottomWall(false);
            b.setTopWall(false);
        } else if (dr == 0 && dc == -1) {   // b is left of a
            a.setLeftWall(false);
            b.setRightWall(false);
        } else if (dr == 0 && dc == 1) {    // b is right of a
            a.setRightWall(false);
            b.setLeftWall(false);
        } else {
            throw new IllegalArgumentException(
                    "Cells " + a + " and " + b + " are not orthogonally adjacent");
        }
    }
}
