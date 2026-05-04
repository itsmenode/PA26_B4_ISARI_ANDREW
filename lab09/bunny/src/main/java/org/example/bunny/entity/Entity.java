package org.example.bunny.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.bunny.game.Board;
import org.example.bunny.game.GameState;
import org.example.bunny.game.SpeedController;
import org.example.bunny.model.Cell;

import java.util.Random;

public abstract class Entity implements Runnable {

    @Getter
    protected final String name;
    protected final Board board;
    protected final GameState gameState;
    protected final Random random;
    protected final long stepDelayMs;
    protected final SpeedController speed;

    @Getter
    @Setter
    private volatile Cell position;
    @Getter
    private volatile boolean captured;

    protected Entity(String name,
                     Board board,
                     GameState gameState,
                     long stepDelayMs,
                     Random random,
                     SpeedController speed) {
        this.name = name;
        this.board = board;
        this.gameState = gameState;
        this.stepDelayMs = stepDelayMs;
        this.random = random;
        this.speed = speed;
        if (speed != null) {
            speed.register(name, stepDelayMs);
        }
    }

    public void markCaptured() { this.captured = true; }

    public abstract char displayChar();
    public abstract boolean isBunny();
    public abstract boolean isRobot();

    @Override
    public void run() {
        try {
            while (!gameState.isOver() && !captured && !Thread.currentThread().isInterrupted()) {
                if (speed != null && speed.isPaused(name)) {
                    Thread.sleep(50);
                    continue;
                }
                step();
                long d = speed != null ? speed.getDelay(name) : stepDelayMs;
                Thread.sleep(d);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    protected abstract void step();
}
