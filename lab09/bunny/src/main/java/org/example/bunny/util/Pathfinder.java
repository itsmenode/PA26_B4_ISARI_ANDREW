package org.example.bunny.util;

import org.example.bunny.model.Cell;
import org.example.bunny.model.Direction;
import org.example.bunny.model.Maze;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Pathfinder {

    private Pathfinder() {}

    public static Optional<Direction> nextStep(Maze maze, Cell from, Cell to) {
        if (from == null || to == null || from.equals(to)) {
            return Optional.empty();
        }
        Map<Cell, Cell> parent = new HashMap<>();
        Deque<Cell> queue = new ArrayDeque<>();
        queue.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            if (cur.equals(to)) break;
            for (Direction d : Direction.values()) {
                if (cur.hasWall(d)) continue;
                Cell n = maze.neighbour(cur, d);
                if (n == null || parent.containsKey(n)) continue;
                parent.put(n, cur);
                queue.add(n);
            }
        }
        if (!parent.containsKey(to)) {
            return Optional.empty();
        }
        Cell step = to;
        while (parent.get(step) != null && !parent.get(step).equals(from)) {
            step = parent.get(step);
        }
        return Optional.of(directionBetween(from, step));
    }

    private static Direction directionBetween(Cell a, Cell b) {
        int dr = b.getRow() - a.getRow();
        int dc = b.getCol() - a.getCol();
        if (dr == -1) return Direction.TOP;
        if (dr == 1)  return Direction.BOTTOM;
        if (dc == -1) return Direction.LEFT;
        return Direction.RIGHT;
    }
}
