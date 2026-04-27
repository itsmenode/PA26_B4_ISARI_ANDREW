package org.example.bunny.entity;

import lombok.Builder;
import lombok.Getter;
import org.example.bunny.game.Board;
import org.example.bunny.game.GameState;
import org.example.bunny.game.MoveResult;
import org.example.bunny.game.SharedMemory;
import org.example.bunny.model.Cell;
import org.example.bunny.model.Direction;
import org.example.bunny.util.Pathfinder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class Robot extends Entity {

    public enum Strategy { RANDOM, HUNTER }

    @Getter private final Strategy strategy;
    private final SharedMemory memory;
    private final int visionRange;
    private final long sightingTtlMs;
    private final char display;

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
                 char display) {
        super(name, board, gameState, stepDelayMs, random);
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

        scanForBunny(here);

        Direction choice = chooseDirection(here);
        if (choice == null) return;

        MoveResult result = board.tryMove(this, choice);
        if (result == MoveResult.CAPTURED_BUNNY) {
            gameState.tryEnd(GameState.Outcome.BUNNY_CAUGHT, name, getPosition());
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

    private Direction chooseDirection(Cell here) {
        if (strategy == Strategy.HUNTER) {
            Optional<SharedMemory.Sighting> hint = memory.latestSighting(sightingTtlMs);
            if (hint.isPresent()) {
                Optional<Direction> step =
                        Pathfinder.nextStep(board.getMaze(), here, hint.get().cell());
                if (step.isPresent()) {
                    return step.get();
                }
            }
        }
        List<Direction> open = new ArrayList<>(4);
        for (Direction d : Direction.values()) {
            if (!here.hasWall(d) && board.getMaze().neighbour(here, d) != null) {
                open.add(d);
            }
        }
        if (open.isEmpty()) return null;
        return open.get(random.nextInt(open.size()));
    }
}
