package com.example.maze.generator;

import com.example.maze.model.Cell;
import com.example.maze.model.Maze;

public interface MazeGenerator {

    void init(Maze maze);

    void step();

    boolean isDone();

    Cell getActiveCell();

    default void generateAll(Maze maze) {
        init(maze);
        while (!isDone()) {
            step();
        }
    }
}
