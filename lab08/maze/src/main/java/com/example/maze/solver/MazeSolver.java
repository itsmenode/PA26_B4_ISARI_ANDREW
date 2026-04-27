package com.example.maze.solver;

import com.example.maze.model.Cell;
import com.example.maze.model.Direction;
import com.example.maze.model.Maze;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MazeSolver {

    private MazeSolver() {}

    public static List<Cell> shortestPath(Maze maze, Cell start, Cell end) {
        if (start == null || end == null) {
            return List.of();
        }
        Map<Cell, Cell> parents = new HashMap<>();
        Deque<Cell> queue = new ArrayDeque<>();
        queue.add(start);
        parents.put(start, null);

        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            if (cur.equals(end)) {
                return reconstruct(parents, end);
            }
            for (Direction d : Direction.values()) {
                if (cur.hasWall(d)) continue;
                Cell next = neighbour(maze, cur, d);
                if (next == null || parents.containsKey(next)) continue;
                parents.put(next, cur);
                queue.add(next);
            }
        }
        return List.of();
    }

    private static Cell neighbour(Maze maze, Cell c, Direction d) {
        int r = c.getRow();
        int col = c.getCol();
        return switch (d) {
            case TOP    -> r > 0                        ? maze.getCell(r - 1, col) : null;
            case BOTTOM -> r < maze.getRows() - 1       ? maze.getCell(r + 1, col) : null;
            case LEFT   -> col > 0                      ? maze.getCell(r, col - 1) : null;
            case RIGHT  -> col < maze.getCols() - 1     ? maze.getCell(r, col + 1) : null;
        };
    }

    private static List<Cell> reconstruct(Map<Cell, Cell> parents, Cell end) {
        java.util.ArrayList<Cell> path = new java.util.ArrayList<>();
        for (Cell c = end; c != null; c = parents.get(c)) {
            path.add(c);
        }
        Collections.reverse(path);
        return path;
    }
}
