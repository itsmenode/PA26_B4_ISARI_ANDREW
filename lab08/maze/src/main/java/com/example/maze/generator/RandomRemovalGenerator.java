package com.example.maze.generator;

import com.example.maze.model.Cell;
import com.example.maze.model.Maze;

import java.util.Random;

/**
 * Stochastic generator: walks the grid in row-major order and tosses a
 * biased coin for each east/south wall. NOT a perfect maze in general
 * (it can leave isolated regions or create cycles); kept for comparison
 * with the DFS generator.
 */
public class RandomRemovalGenerator implements MazeGenerator {

    private final double removalProbability;
    private final Random random;

    private Maze maze;
    private int row;
    private int col;
    private boolean done;
    private Cell active;

    public RandomRemovalGenerator(double removalProbability) {
        this(removalProbability, new Random());
    }

    public RandomRemovalGenerator(double removalProbability, Random random) {
        if (removalProbability < 0.0 || removalProbability > 1.0) {
            throw new IllegalArgumentException("removalProbability must be in [0, 1]");
        }
        this.removalProbability = removalProbability;
        this.random = random;
    }

    @Override
    public void init(Maze maze) {
        this.maze = maze;
        maze.resetAllWalls();
        row = 0;
        col = 0;
        done = (maze.getRows() == 0 || maze.getCols() == 0);
        active = done ? null : maze.getCell(0, 0);
    }

    @Override
    public void step() {
        if (done) return;

        Cell current = maze.getCell(row, col);
        active = current;

        if (col + 1 < maze.getCols() && random.nextDouble() < removalProbability) {
            maze.removeWallBetween(current, maze.getCell(row, col + 1));
        }
        if (row + 1 < maze.getRows() && random.nextDouble() < removalProbability) {
            maze.removeWallBetween(current, maze.getCell(row + 1, col));
        }

        col++;
        if (col >= maze.getCols()) {
            col = 0;
            row++;
            if (row >= maze.getRows()) {
                done = true;
                active = null;
            }
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public Cell getActiveCell() {
        return active;
    }
}
