package com.example.maze.solver;

import com.example.maze.model.Cell;
import com.example.maze.model.Direction;
import com.example.maze.model.Maze;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Proves whether the graph induced by the maze (cells = vertices,
 * removed walls = edges) is a spanning tree of the grid.
 *
 * <p>A maze with N cells is a "perfect maze" iff:
 * <ul>
 *   <li>it is <b>connected</b> &mdash; every cell is reachable from any
 *       other (no isolated areas), and</li>
 *   <li>it is <b>acyclic</b> &mdash; given connectivity, this is
 *       equivalent to having exactly {@code N - 1} edges.</li>
 * </ul>
 * Together these two facts force a unique simple path between every
 * pair of cells, which is the standard tree property.
 */
public final class MazeValidator {

    public record Result(
            boolean perfect,
            boolean connected,
            boolean acyclic,
            int totalCells,
            int reachableCells,
            int edges,
            int expectedEdges
    ) {
        public String describe() {
            StringBuilder sb = new StringBuilder();
            sb.append(perfect ? "Perfect maze." : "NOT a perfect maze.")
              .append(System.lineSeparator())
              .append("  cells           = ").append(totalCells).append(System.lineSeparator())
              .append("  reachable (BFS) = ").append(reachableCells)
              .append("  -> connected: ").append(connected ? "yes" : "NO").append(System.lineSeparator())
              .append("  removed walls   = ").append(edges)
              .append("  (tree needs ").append(expectedEdges).append(")")
              .append("  -> acyclic: ").append(acyclic ? "yes" : "NO");
            return sb.toString();
        }
    }

    private MazeValidator() {}

    public static Result validate(Maze maze) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        int n = rows * cols;
        int edges = countOpenings(maze);
        int reachable = bfsReachable(maze);

        boolean connected = (reachable == n);
        // For a connected graph on N vertices: tree  iff  |E| = N - 1.
        // For a graph with N - 1 edges: tree  iff  connected.
        // We test both connectivity and the edge count, which together
        // imply the spanning-tree property.
        boolean acyclic = connected && (edges == n - 1);
        boolean perfect = connected && acyclic;

        return new Result(perfect, connected, acyclic, n, reachable, edges, n - 1);
    }

    private static int countOpenings(Maze maze) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        int edges = 0;
        // Each undirected edge counted once: only east + south of (r, c).
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = maze.getCell(r, c);
                if (c + 1 < cols && !cell.hasWall(Direction.RIGHT)) edges++;
                if (r + 1 < rows && !cell.hasWall(Direction.BOTTOM)) edges++;
            }
        }
        return edges;
    }

    private static int bfsReachable(Maze maze) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        if (rows == 0 || cols == 0) return 0;

        boolean[][] seen = new boolean[rows][cols];
        Deque<Cell> queue = new ArrayDeque<>();
        Cell start = maze.getCell(0, 0);
        seen[0][0] = true;
        queue.add(start);
        int count = 0;

        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            count++;
            int r = cur.getRow();
            int c = cur.getCol();
            tryEnqueue(maze, queue, seen, cur, Direction.TOP,    r - 1, c);
            tryEnqueue(maze, queue, seen, cur, Direction.BOTTOM, r + 1, c);
            tryEnqueue(maze, queue, seen, cur, Direction.LEFT,   r,     c - 1);
            tryEnqueue(maze, queue, seen, cur, Direction.RIGHT,  r,     c + 1);
        }
        return count;
    }

    private static void tryEnqueue(Maze maze, Deque<Cell> queue, boolean[][] seen,
                                   Cell from, Direction dir, int nr, int nc) {
        if (nr < 0 || nr >= maze.getRows() || nc < 0 || nc >= maze.getCols()) return;
        if (from.hasWall(dir)) return;
        if (seen[nr][nc]) return;
        seen[nr][nc] = true;
        queue.add(maze.getCell(nr, nc));
    }
}
