package org.example.bunny.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.bunny.game.Board;
import org.example.bunny.game.GameState;
import org.example.bunny.model.Cell;

import java.util.Random;

/**
 * Base class for anything that lives in the maze and moves on its own
 * thread. Subclasses implement {@link #step()} which is invoked in a loop
 * until the game ends.
 */
public abstract class Entity implements Runnable {

    @Getter protected final String name;
    protected final Board board;
    protected final GameState gameState;
    protected final Random random;
    protected final long stepDelayMs;

    @Getter @Setter private volatile Cell position;
    @Getter private volatile boolean captured;

    protected Entity(String name, Board board, GameState gameState, long stepDelayMs, Random random) {
        this.name = name;
        this.board = board;
        this.gameState = gameState;
        this.stepDelayMs = stepDelayMs;
        this.random = random;
    }

    public void markCaptured() { this.captured = true; }

    public abstract char displayChar();
    public abstract boolean isBunny();
    public abstract boolean isRobot();

    @Override
    public void run() {
        try {
            while (!gameState.isOver() && !captured && !Thread.currentThread().isInterrupted()) {
                step();
                Thread.sleep(stepDelayMs);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    protected abstract void step();
}
