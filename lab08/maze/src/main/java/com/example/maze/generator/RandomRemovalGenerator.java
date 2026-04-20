package com.example.maze.generator;

import com.example.maze.model.Cell;
import com.example.maze.model.Maze;

import java.util.Random;


public class RandomRemovalGenerator implements MazeGenerator {

    private final double removalProbability;
    private final Random random;

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
    public void generate(Maze maze) {
        maze.resetAllWalls();
        int rows = maze.getRows();
        int cols = maze.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell current = maze.getCell(r, c);
                if (c + 1 < cols && random.nextDouble() < removalProbability) {
                    maze.removeWallBetween(current, maze.getCell(r, c + 1));
                }
                if (r + 1 < rows && random.nextDouble() < removalProbability) {
                    maze.removeWallBetween(current, maze.getCell(r + 1, c));
                }
            }
        }
    }
}
