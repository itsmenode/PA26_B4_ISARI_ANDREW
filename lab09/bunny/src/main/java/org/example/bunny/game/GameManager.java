package org.example.bunny.game;

import org.example.bunny.ui.TextRenderer;

public class GameManager implements Runnable {

    private final TextRenderer renderer;
    private final GameState state;
    private final long timeoutMs;
    private final long renderIntervalMs;
    private final long startMs;

    public GameManager(TextRenderer renderer,
                       GameState state,
                       long timeoutMs,
                       long renderIntervalMs) {
        this.renderer = renderer;
        this.state = state;
        this.timeoutMs = timeoutMs;
        this.renderIntervalMs = renderIntervalMs;
        this.startMs = System.currentTimeMillis();
    }

    public long getStartMs() { return startMs; }

    @Override
    public void run() {
        try {
            while (!state.isOver()) {
                long elapsed = System.currentTimeMillis() - startMs;
                renderer.render(elapsed);
                if (timeoutMs > 0 && elapsed >= timeoutMs) {
                    state.tryEnd(GameState.Outcome.TIMEOUT, "manager", null);
                    break;
                }
                Thread.sleep(renderIntervalMs);
            }
            renderer.render(System.currentTimeMillis() - startMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
