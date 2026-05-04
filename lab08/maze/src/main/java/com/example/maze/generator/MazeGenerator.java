package com.example.maze.generator;

import com.example.maze.model.Cell;
import com.example.maze.model.Maze;

/**
 * Step-based maze generator. The driver is expected to call
 * {@link #init(Maze)} once, then repeatedly call {@link #step()} until
 * {@link #isDone()} returns true. This decoupling lets the GUI animate
 * generation by interleaving steps with frame redraws.
 */
public interface MazeGenerator {

    void init(Maze maze);

    void step();

    boolean isDone();

    /** The cell currently being expanded, or {@code null} if none. */
    Cell getActiveCell();

    /** Convenience method for non-animated callers. */
    default void generateAll(Maze maze) {
        init(maze);
        while (!isDone()) {
            step();
        }
    }
}
