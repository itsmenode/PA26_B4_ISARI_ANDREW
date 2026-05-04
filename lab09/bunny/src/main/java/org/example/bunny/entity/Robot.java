package org.example.bunny.entity;

import lombok.Builder;
import lombok.Getter;
import org.example.bunny.game.Board;
import org.example.bunny.game.GameState;
import org.example.bunny.game.MoveResult;
import org.example.bunny.game.SharedMemory;
import org.example.bunny.game.SpeedController;
import org.example.bunny.model.Cell;
import org.example.bunny.model.Direction;
import org.example.bunny.util.Pathfinder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Robot extends Entity {

    public enum Strategy { RANDOM, EXPLORER, HUNTER }

    @Getter private final Strategy strategy;
    private final SharedMemory memory;
    private final int visionRange;
    private final long sightingTtlMs;
    private final char display;

    private final Set<Cell> visited = new HashSet<>();

    @Builder
    public Robot(String name,
                 Board board,
                 GameState gameState,
                 long stepDelayMs,
                 Random random,
                 Strategy strategy,
                 SharedMemory memory,
                 int visionRange,
                 long sightingTtlMs,
                 char display,
                 SpeedController speed) {
        super(name, board, gameState, stepDelayMs, random, speed);
        this.strategy = strategy;
        this.memory = memory;
        this.visionRange = visionRange;
        this.sightingTtlMs = sightingTtlMs;
        this.display = display;
    }

    @Override
    public char displayChar() { return display; }

    @Override
    public boolean isBunny() { return false; }

    @Override
    public boolean isRobot() { return true; }

    @Override
    protected void step() {
        Cell here = getPosition();
        if (here == null) return;

        visited.add(here);
        scanForBunny(here);

        EnumSet<Direction> excluded = EnumSet.noneOf(Direction.class);
        for (int attempt = 0; attempt < Direction.values().length; attempt++) {
            Direction choice = chooseDirection(here, excluded);
            if (choice == null) return;

            MoveResult result = board.tryMove(this, choice);
            if (result == MoveResult.MOVED) return;
            if (result == MoveResult.CAPTURED_BUNNY) {
                gameState.tryEnd(GameState.Outcome.BUNNY_CAUGHT, name, getPosition());
                return;
            }
            if (result == MoveResult.BLOCKED_BY_OCCUPANT) {
                excluded.add(choice);
                continue;
            }
            return;
        }
    }

    private void scanForBunny(Cell origin) {
        if (visionRange <= 0) return;
        Map<Cell, Integer> dist = new HashMap<>();
        Deque<Cell> queue = new ArrayDeque<>();
        queue.add(origin);
        dist.put(origin, 0);

        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            int d = dist.get(cur);
            Entity occ = board.entityAt(cur);
            if (occ != null && occ.isBunny()) {
                memory.recordSighting(name, cur);
                return;
            }
            if (d == visionRange) continue;
            for (Direction dir : Direction.values()) {
                if (cur.hasWall(dir)) continue;
                Cell n = board.getMaze().neighbour(cur, dir);
                if (n == null || dist.containsKey(n)) continue;
                dist.put(n, d + 1);
                queue.add(n);
            }
        }
    }

    private Direction chooseDirection(Cell here, Set<Direction> excluded) {
        if (strategy == Strategy.HUNTER) {
            Optional<SharedMemory.Sighting> hint = memory.latestSighting(sightingTtlMs);
            if (hint.isPresent()) {
                Optional<Direction> step =
                        Pathfinder.nextStep(board.getMaze(), here, hint.get().cell());
                if (step.isPresent() && !excluded.contains(step.get())) return step.get();
            }
        }

        if (strategy == Strategy.EXPLORER || strategy == Strategy.HUNTER) {
            Direction d = exploreFrom(here, excluded);
            if (d != null) return d;
        }

        return randomOpenDirection(here, excluded);
    }

    private Direction exploreFrom(Cell here, Set<Direction> excluded) {
        List<Direction> unvisitedNeighbours = new ArrayList<>(4);
        for (Direction d : Direction.values()) {
            if (excluded.contains(d)) continue;
            if (here.hasWall(d)) continue;
            Cell n = board.getMaze().neighbour(here, d);
            if (n == null) continue;
            if (!visited.contains(n)) unvisitedNeighbours.add(d);
        }
        if (!unvisitedNeighbours.isEmpty()) {
            return unvisitedNeighbours.get(random.nextInt(unvisitedNeighbours.size()));
        }

        Optional<Cell> frontier = bfsToNearestUnvisited(here);
        if (frontier.isPresent()) {
            Optional<Direction> step = Pathfinder.nextStep(board.getMaze(), here, frontier.get());
            if (step.isPresent() && !excluded.contains(step.get())) return step.get();
            return null;
        }

        visited.clear();
        visited.add(here);
        return null;
    }

    private Optional<Cell> bfsToNearestUnvisited(Cell from) {
        Map<Cell, Integer> dist = new HashMap<>();
        Deque<Cell> queue = new ArrayDeque<>();
        queue.add(from);
        dist.put(from, 0);
        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            if (!visited.contains(cur)) return Optional.of(cur);
            for (Direction d : Direction.values()) {
                if (cur.hasWall(d)) continue;
                Cell n = board.getMaze().neighbour(cur, d);
                if (n == null || dist.containsKey(n)) continue;
                dist.put(n, dist.get(cur) + 1);
                queue.add(n);
            }
        }
        return Optional.empty();
    }

    private Direction randomOpenDirection(Cell here, Set<Direction> excluded) {
        List<Direction> open = new ArrayList<>(4);
        for (Direction d : Direction.values()) {
            if (excluded.contains(d)) continue;
            if (!here.hasWall(d) && board.getMaze().neighbour(here, d) != null) {
                open.add(d);
            }
        }
        if (open.isEmpty()) return null;
        return open.get(random.nextInt(open.size()));
    }
}
