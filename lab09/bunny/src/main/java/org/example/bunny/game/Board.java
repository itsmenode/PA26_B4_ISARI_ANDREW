package org.example.bunny.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.bunny.entity.Entity;
import org.example.bunny.model.Cell;
import org.example.bunny.model.Direction;
import org.example.bunny.model.Maze;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Tracks which entity occupies each cell. All occupancy reads and writes go
 * through synchronized methods, so two entity threads cannot race onto the
 * same cell. A single intrinsic lock keeps the protocol simple and
 * deadlock-free since no nested locks are ever taken.
 */
@RequiredArgsConstructor
public class Board {

    @Getter private final Maze maze;
    private final Map<Cell, Entity> occupants = new HashMap<>();

    public synchronized boolean place(Entity e, Cell cell) {
        if (occupants.containsKey(cell)) return false;
        occupants.put(cell, e);
        e.setPosition(cell);
        return true;
    }

    public synchronized Optional<Cell> firstFreeCell(List<Cell> candidates) {
        for (Cell c : candidates) {
            if (!occupants.containsKey(c)) return Optional.of(c);
        }
        return Optional.empty();
    }

    public synchronized MoveResult tryMove(Entity e, Direction d) {
        Cell from = e.getPosition();
        if (from == null || occupants.get(from) != e) {
            return MoveResult.NOT_PLACED;
        }
        if (from.hasWall(d)) {
            return MoveResult.BLOCKED_BY_WALL;
        }
        Cell to = maze.neighbour(from, d);
        if (to == null) {
            return MoveResult.BLOCKED_BY_WALL;
        }
        Entity occupant = occupants.get(to);
        if (occupant != null) {
            if (e.isRobot() && occupant.isBunny()) {
                occupants.remove(from);
                occupants.put(to, e);
                e.setPosition(to);
                occupant.markCaptured();
                return MoveResult.CAPTURED_BUNNY;
            }
            return MoveResult.BLOCKED_BY_OCCUPANT;
        }
        occupants.remove(from);
        occupants.put(to, e);
        e.setPosition(to);
        return MoveResult.MOVED;
    }

    public synchronized Entity entityAt(Cell c) {
        return occupants.get(c);
    }

    public synchronized Map<Cell, Entity> snapshotOccupants() {
        return new HashMap<>(occupants);
    }
}
