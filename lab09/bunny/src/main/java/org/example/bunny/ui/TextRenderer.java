package org.example.bunny.ui;

import lombok.RequiredArgsConstructor;
import org.example.bunny.entity.Entity;
import org.example.bunny.game.Board;
import org.example.bunny.game.GameState;
import org.example.bunny.game.SharedMemory;
import org.example.bunny.game.SpeedController;
import org.example.bunny.model.Cell;
import org.example.bunny.model.Direction;
import org.example.bunny.model.Maze;

import java.util.Map;

@RequiredArgsConstructor
public class TextRenderer {

    private static final String CLEAR_SCREEN = "[H[2J";

    private final Board board;
    private final GameState state;
    private final SharedMemory memory;
    private final SpeedController speed;
    private final Cell exit;
    private final boolean useAnsiClear;

    public void render() { render(0L); }

    public void render(long elapsedMs) {
        Map<Cell, Entity> snapshot = board.snapshotOccupants();
        StringBuilder sb = new StringBuilder();
        if (useAnsiClear) sb.append(CLEAR_SCREEN);
        appendHeader(sb, elapsedMs);
        appendMaze(sb, snapshot);
        appendSpeeds(sb);
        appendLegend(sb);
        System.out.print(sb);
        System.out.flush();
    }

    private void appendHeader(StringBuilder sb, long elapsedMs) {
        sb.append("Bunny vs Robots\n");
        sb.append("Status: ").append(state.getOutcome().getMessage());
        if (state.isOver() && state.getWinnerName() != null) {
            sb.append("  (").append(state.getWinnerName()).append(')');
        }
        sb.append('\n');
        sb.append("Elapsed: ").append(formatElapsed(elapsedMs)).append('\n');
        sb.append("Sightings logged: ").append(memory.sightingCount()).append('\n');
        memory.latestSighting(Long.MAX_VALUE).ifPresent(s ->
                sb.append("Last sighting: ").append(s.cell())
                  .append(" by ").append(s.observer()).append('\n'));
        sb.append('\n');
    }

    private void appendMaze(StringBuilder sb, Map<Cell, Entity> snapshot) {
        Maze maze = board.getMaze();
        int rows = maze.getRows();
        int cols = maze.getCols();

        for (int c = 0; c < cols; c++) {
            sb.append('+').append(maze.getCell(0, c).hasWall(Direction.TOP) ? "---" : "   ");
        }
        sb.append("+\n");

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = maze.getCell(r, c);
                sb.append(cell.hasWall(Direction.LEFT) ? '|' : ' ');
                sb.append(' ').append(glyph(cell, snapshot)).append(' ');
            }
            sb.append("|\n");

            for (int c = 0; c < cols; c++) {
                Cell cell = maze.getCell(r, c);
                sb.append('+').append(cell.hasWall(Direction.BOTTOM) ? "---" : "   ");
            }
            sb.append("+\n");
        }
    }

    private char glyph(Cell cell, Map<Cell, Entity> snapshot) {
        Entity e = snapshot.get(cell);
        if (e != null) return e.displayChar();
        if (cell.equals(exit)) return 'E';
        return ' ';
    }

    private void appendSpeeds(StringBuilder sb) {
        if (speed == null) return;
        sb.append("\nSpeeds:\n");
        for (String name : speed.names()) {
            sb.append("  ").append(name)
              .append(" delay=").append(speed.getDelay(name)).append("ms");
            if (speed.isPaused(name)) sb.append(" [PAUSED]");
            sb.append('\n');
        }
    }

    private void appendLegend(StringBuilder sb) {
        sb.append("\nLegend: B=bunny  0..9=robots  E=exit\n");
    }

    private static String formatElapsed(long ms) {
        long s = ms / 1000;
        long mm = s / 60;
        long ss = s % 60;
        long mss = ms % 1000;
        return String.format("%02d:%02d.%03d", mm, ss, mss);
    }
}
