package com.example.maze.generator;

import com.example.maze.model.Cell;
import com.example.maze.model.Maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class DfsGenerator implements MazeGenerator {

    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    private final Random random;

    private Maze maze;
    private boolean[][] visited;
    private Deque<Cell> stack;
    private final List<Cell> scratch = new ArrayList<>(4);

    public DfsGenerator() {
        this(new Random());
    }

    public DfsGenerator(Random random) {
        this.random = random;
    }

    @Override
    public void init(Maze maze) {
        this.maze = maze;
        maze.resetAllWalls();
        visited = new boolean[maze.getRows()][maze.getCols()];
        stack = new ArrayDeque<>();
        Cell start = maze.getCell(0, 0);
        visited[0][0] = true;
        stack.push(start);
    }

    @Override
    public void step() {
        if (isDone()) return;

        Cell current = stack.peek();
        scratch.clear();
        for (int[] d : DIRECTIONS) {
            int nr = current.getRow() + d[0];
            int nc = current.getCol() + d[1];
            if (nr >= 0 && nr < maze.getRows()
                    && nc >= 0 && nc < maze.getCols()
                    && !visited[nr][nc]) {
                scratch.add(maze.getCell(nr, nc));
            }
        }

        if (scratch.isEmpty()) {
            stack.pop();
        } else {
            Cell next = scratch.get(random.nextInt(scratch.size()));
            maze.removeWallBetween(current, next);
            visited[next.getRow()][next.getCol()] = true;
            stack.push(next);
        }
    }

    @Override
    public boolean isDone() {
        return stack == null || stack.isEmpty();
    }

    @Override
    public Cell getActiveCell() {
        return (stack == null || stack.isEmpty()) ? null : stack.peek();
    }
}
