package org.example.bunny;

import org.example.bunny.entity.Bunny;
import org.example.bunny.entity.Entity;
import org.example.bunny.entity.Robot;
import org.example.bunny.game.Board;
import org.example.bunny.game.GameState;
import org.example.bunny.game.SharedMemory;
import org.example.bunny.generator.DfsGenerator;
import org.example.bunny.generator.MazeGenerator;
import org.example.bunny.model.Cell;
import org.example.bunny.model.Maze;
import org.example.bunny.ui.TextRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BunnyApp {

    public static void main(String[] args) throws InterruptedException {
        Settings cfg = Settings.parse(args);
        System.out.println("Starting bunny simulation: " + cfg);

        Random random = cfg.seed == null ? new Random() : new Random(cfg.seed);

        Maze maze = new Maze(cfg.rows, cfg.cols);
        MazeGenerator generator = new DfsGenerator(random);
        generator.generate(maze);

        Cell exit = maze.getCell(cfg.rows - 1, cfg.cols - 1);

        Board board = new Board(maze);
        SharedMemory memory = new SharedMemory();
        GameState state = new GameState();

        List<Cell> available = listAvailableCells(maze, exit);
        Collections.shuffle(available, random);

        Bunny bunny = new Bunny(
                "bunny", board, state, cfg.bunnyDelayMs, exit,
                cfg.bunnySmartProbability, new Random(random.nextLong()));
        if (!board.place(bunny, available.remove(0))) {
            throw new IllegalStateException("Failed to place bunny");
        }

        List<Robot> robots = new ArrayList<>();
        for (int i = 0; i < cfg.robots; i++) {
            Robot.Strategy strategy =
                    (i % 2 == 0) ? Robot.Strategy.HUNTER : Robot.Strategy.RANDOM;
            char display = (char) ('0' + i);
            Robot robot = Robot.builder()
                    .name("robot-" + i + "(" + strategy + ")")
                    .board(board)
                    .gameState(state)
                    .stepDelayMs(cfg.robotDelayMs)
                    .random(new Random(random.nextLong()))
                    .strategy(strategy)
                    .memory(memory)
                    .visionRange(cfg.visionRange)
                    .sightingTtlMs(cfg.sightingTtlMs)
                    .display(display)
                    .build();
            if (!board.place(robot, available.remove(0))) {
                throw new IllegalStateException("Failed to place " + robot.getName());
            }
            robots.add(robot);
        }

        TextRenderer renderer = new TextRenderer(board, state, memory, exit, cfg.useAnsiClear);

        List<Thread> threads = new ArrayList<>();
        threads.add(startThread(bunny, "bunny"));
        for (Robot r : robots) {
            threads.add(startThread(r, r.getName()));
        }

        long startMs = System.currentTimeMillis();
        while (!state.isOver()
                && (cfg.timeoutMs <= 0 || System.currentTimeMillis() - startMs < cfg.timeoutMs)) {
            renderer.render();
            Thread.sleep(cfg.renderIntervalMs);
        }

        if (!state.isOver()) {
            state.tryEnd(GameState.Outcome.ONGOING, "timeout", null);
            System.out.println("Timed out before either side won.");
        }

        for (Thread t : threads) t.interrupt();
        for (Thread t : threads) t.join();

        renderer.render();
        printOutcome(state, bunny, robots);
    }

    private static Thread startThread(Entity entity, String name) {
        Thread t = new Thread(entity, name);
        t.setDaemon(true);
        t.start();
        return t;
    }

    private static List<Cell> listAvailableCells(Maze maze, Cell exit) {
        List<Cell> cells = new ArrayList<>(maze.getRows() * maze.getCols());
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);
                if (!cell.equals(exit)) cells.add(cell);
            }
        }
        return cells;
    }

    private static void printOutcome(GameState state, Bunny bunny, List<Robot> robots) {
        System.out.println();
        System.out.println("=== Final outcome ===");
        System.out.println(state.getOutcome().getMessage()
                + (state.getWinnerName() == null ? "" : " - " + state.getWinnerName()));
        System.out.println("Bunny final position: " + bunny.getPosition());
        for (Robot r : robots) {
            System.out.println(r.getName() + " final position: " + r.getPosition());
        }
    }

    private record Settings(int rows,
                            int cols,
                            int robots,
                            long bunnyDelayMs,
                            long robotDelayMs,
                            long renderIntervalMs,
                            long timeoutMs,
                            int visionRange,
                            long sightingTtlMs,
                            double bunnySmartProbability,
                            Long seed,
                            boolean useAnsiClear) {

        static Settings parse(String[] args) {
            int rows = 10;
            int cols = 15;
            int robots = 3;
            long bunnyDelayMs = 350;
            long robotDelayMs = 250;
            long renderIntervalMs = 200;
            long timeoutMs = 60_000;
            int visionRange = 3;
            long sightingTtlMs = 4_000;
            double smart = 0.35;
            Long seed = null;
            boolean ansi = true;

            for (String arg : args) {
                if (!arg.startsWith("--")) continue;
                String[] kv = arg.substring(2).split("=", 2);
                if (kv.length != 2) continue;
                switch (kv[0]) {
                    case "rows" -> rows = Integer.parseInt(kv[1]);
                    case "cols" -> cols = Integer.parseInt(kv[1]);
                    case "robots" -> robots = Integer.parseInt(kv[1]);
                    case "bunny-delay" -> bunnyDelayMs = Long.parseLong(kv[1]);
                    case "robot-delay" -> robotDelayMs = Long.parseLong(kv[1]);
                    case "render" -> renderIntervalMs = Long.parseLong(kv[1]);
                    case "timeout" -> timeoutMs = Long.parseLong(kv[1]);
                    case "vision" -> visionRange = Integer.parseInt(kv[1]);
                    case "ttl" -> sightingTtlMs = Long.parseLong(kv[1]);
                    case "smart" -> smart = Double.parseDouble(kv[1]);
                    case "seed" -> seed = Long.parseLong(kv[1]);
                    case "ansi" -> ansi = Boolean.parseBoolean(kv[1]);
                    default -> { }
                }
            }

            int totalCells = rows * cols;
            if (robots + 1 >= totalCells) {
                throw new IllegalArgumentException(
                        "Not enough cells (" + totalCells + ") to host bunny + " + robots + " robots and an exit.");
            }

            return new Settings(rows, cols, robots,
                    bunnyDelayMs, robotDelayMs, renderIntervalMs, timeoutMs,
                    visionRange, sightingTtlMs, smart, seed, ansi);
        }
    }
}
