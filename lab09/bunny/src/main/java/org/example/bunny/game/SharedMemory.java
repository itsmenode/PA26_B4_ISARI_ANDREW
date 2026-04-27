package org.example.bunny.game;

import org.example.bunny.model.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SharedMemory {

    public record Sighting(Cell cell, long timestampMs, String observer) {}

    private final List<Sighting> log = new ArrayList<>();
    private Sighting last;

    public synchronized void recordSighting(String observer, Cell cell) {
        Sighting s = new Sighting(cell, System.currentTimeMillis(), observer);
        last = s;
        log.add(s);
    }

    public synchronized Optional<Sighting> latestSighting(long maxAgeMs) {
        if (last == null) return Optional.empty();
        if (System.currentTimeMillis() - last.timestampMs() > maxAgeMs) {
            return Optional.empty();
        }
        return Optional.of(last);
    }

    public synchronized int sightingCount() {
        return log.size();
    }
}
