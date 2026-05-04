package org.example.bunny.ui;

import org.example.bunny.entity.Robot;
import org.example.bunny.game.GameState;
import org.example.bunny.game.SpeedController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommandConsole implements Runnable {

    private enum Action { PAUSE, RESUME, FASTER, SLOWER }

    private final SpeedController speed;
    private final GameState state;
    private final Map<String, String> aliases = new LinkedHashMap<>();

    public CommandConsole(SpeedController speed,
                          GameState state,
                          String bunnyName,
                          List<Robot> robots) {
        this.speed = speed;
        this.state = state;
        aliases.put("bunny", bunnyName);
        aliases.put("b", bunnyName);
        for (int i = 0; i < robots.size(); i++) {
            String full = robots.get(i).getName();
            aliases.put("robot-" + i, full);
            aliases.put("r" + i, full);
            aliases.put(String.valueOf(i), full);
        }
    }

    @Override
    public void run() {
        printHelp();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (!state.isOver()) {
                String line = reader.readLine();
                if (line == null) return;
                handle(line.trim().toLowerCase(Locale.ROOT));
            }
        } catch (IOException ignored) {
        }
    }

    private void printHelp() {
        System.out.println("commands: help, status, pause [t], resume [t], faster [t], slower [t], quit");
        System.out.println("targets : all (default), bunny|b, robot-N|rN|N");
    }

    private void handle(String line) {
        if (line.isEmpty()) return;
        String[] parts = line.split("\\s+");
        String cmd = parts[0];
        String tgt = parts.length > 1 ? parts[1] : "all";

        switch (cmd) {
            case "help", "?" -> printHelp();
            case "status" -> printStatus();
            case "pause", "stop" -> apply(tgt, Action.PAUSE);
            case "resume", "start" -> apply(tgt, Action.RESUME);
            case "faster", "+" -> apply(tgt, Action.FASTER);
            case "slower", "-" -> apply(tgt, Action.SLOWER);
            case "quit", "end", "exit" -> state.tryEnd(GameState.Outcome.STOPPED, "console", null);
            default -> System.out.println("unknown: " + cmd);
        }
    }

    private void apply(String target, Action a) {
        if ("all".equals(target)) {
            switch (a) {
                case PAUSE -> speed.pauseAll();
                case RESUME -> speed.resumeAll();
                case FASTER -> speed.fasterAll();
                case SLOWER -> speed.slowerAll();
            }
            System.out.println("[" + a + " all]");
            return;
        }
        String name = aliases.get(target);
        if (name == null) {
            System.out.println("unknown target: " + target);
            return;
        }
        switch (a) {
            case PAUSE -> speed.pause(name);
            case RESUME -> speed.resume(name);
            case FASTER -> speed.faster(name);
            case SLOWER -> speed.slower(name);
        }
        System.out.println(a + " " + name);
    }

    private void printStatus() {
        System.out.println("Speeds:");
        for (String n : speed.names()) {
            System.out.println("  " + n + " delay=" + speed.getDelay(n) + "ms"
                    + (speed.isPaused(n) ? " [PAUSED]" : ""));
        }
    }
}
