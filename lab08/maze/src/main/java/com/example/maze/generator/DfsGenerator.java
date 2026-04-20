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

    public DfsGenerator() {
        this(new Random());
    }

    public DfsGenerator(Random random) {
        this.random = random;
    }

    @Override
    public void generate(Maze maze) {
        maze.resetAllWalls();
        int rows = maze.getRows();
        int cols = maze.getCols();

        boolean[][] visited = new boolean[rows][cols];
        Deque<Cell> stack = new ArrayDeque<>();

        Cell start = maze.getCell(0, 0);
        visited[0][0] = true;
        stack.push(start);

        List<Cell> unvisitedNeighbours = new ArrayList<>(4);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            unvisitedNeighbours.clear();

            for (int[] d : DIRECTIONS) {
                int nr = current.getRow() + d[0];
                int nc = current.getCol() + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
                    unvisitedNeighbours.add(maze.getCell(nr, nc));
                }
            }

            if (unvisitedNeighbours.isEmpty()) {
                stack.pop();
            } else {
                Cell next = unvisitedNeighbours.get(random.nextInt(unvisitedNeighbours.size()));
                maze.removeWallBetween(current, next);
                visited[next.getRow()][next.getCol()] = true;
                stack.push(next);
            }
        }
    }
}
