package org.example.bunny.generator;

import org.example.bunny.model.Cell;
import org.example.bunny.model.Direction;
import org.example.bunny.model.Maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class DfsGenerator implements MazeGenerator {

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

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Direction> options = new ArrayList<>(4);
            for (Direction d : Direction.values()) {
                Cell n = maze.neighbour(current, d);
                if (n != null && !visited[n.getRow()][n.getCol()]) {
                    options.add(d);
                }
            }
            if (options.isEmpty()) {
                stack.pop();
            } else {
                Collections.shuffle(options, random);
                Direction chosen = options.get(0);
                Cell next = maze.neighbour(current, chosen);
                maze.removeWallBetween(current, next);
                visited[next.getRow()][next.getCol()] = true;
                stack.push(next);
            }
        }
    }
}
