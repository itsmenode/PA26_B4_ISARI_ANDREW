package org.example.bunny.entity;

import org.example.bunny.game.Board;
import org.example.bunny.game.GameState;
import org.example.bunny.game.MoveResult;
import org.example.bunny.game.SpeedController;
import org.example.bunny.model.Cell;
import org.example.bunny.model.Direction;
import org.example.bunny.util.Pathfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Bunny extends Entity {

    private final Cell exit;
    private final double smartProbability;

    public Bunny(String name,
                 Board board,
                 GameState gameState,
                 long stepDelayMs,
                 Cell exit,
                 double smartProbability,
                 Random random,
                 SpeedController speed) {
        super(name, board, gameState, stepDelayMs, random, speed);
        this.exit = exit;
        this.smartProbability = smartProbability;
    }

    @Override
    public char displayChar() { return 'B'; }

    @Override
    public boolean isBunny() { return true; }

    @Override
    public boolean isRobot() { return false; }

    @Override
    protected void step() {
        Cell here = getPosition();
        if (here == null) return;

        if (here.equals(exit)) {
            gameState.tryEnd(GameState.Outcome.BUNNY_ESCAPED, name, here);
            return;
        }

        Direction choice = chooseDirection(here);
        if (choice == null) return;

        MoveResult result = board.tryMove(this, choice);
        if (result == MoveResult.MOVED && getPosition().equals(exit)) {
            gameState.tryEnd(GameState.Outcome.BUNNY_ESCAPED, name, getPosition());
        }
    }

    private Direction chooseDirection(Cell here) {
        if (random.nextDouble() < smartProbability) {
            Optional<Direction> guided = Pathfinder.nextStep(board.getMaze(), here, exit);
            if (guided.isPresent()) {
                return guided.get();
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
