package algorithm;

import dao.MovieListDAO;
import db.Database;
import model.MovieList;

import java.sql.*;
import java.util.*;

public class MoviePartitioner {

    public static class PartitionStats {
        public int totalMovies;
        public int numLists;
        public int minSize;
        public int maxSize;

        @Override
        public String toString() {
            return "movies=" + totalMovies
                    + " lists=" + numLists
                    + " min=" + minSize
                    + " max=" + maxSize
                    + " (max-min=" + (maxSize - minSize) + ")";
        }
    }

    public static class Result {
        public final List<MovieList> lists;
        public final PartitionStats stats;

        public Result(List<MovieList> lists, PartitionStats stats) {
            this.lists = lists;
            this.stats = stats;
        }
    }

    public Result partition() throws SQLException {
        List<Integer> movieIds = loadMovieIds();
        if (movieIds.isEmpty()) {
            System.out.println("No movies to partition.");
            return new Result(Collections.emptyList(), new PartitionStats());
        }

        Map<Integer, Set<Integer>> adj = buildAdjacency();
        Map<Integer, Integer> color = colorGreedy(movieIds, adj);
        List<List<Integer>> groups = groupByColor(color);

        balance(groups, adj);

        MovieListDAO listDAO = new MovieListDAO();
        listDAO.deleteAll();

        List<MovieList> saved = new ArrayList<>();
        int n = 1;
        for (List<Integer> group : groups) {
            if (group.isEmpty()) continue;
            MovieList list = listDAO.create("Unrelated Set " + n++);
            listDAO.addMovies(list.getId(), group);

            saved.add(listDAO.findById(list.getId()));
        }

        return new Result(saved, computeStats(groups));
    }

    private List<Integer> loadMovieIds() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM movies ORDER BY id")) {
            while (rs.next()) ids.add(rs.getInt(1));
        }
        return ids;
    }

    private Map<Integer, Set<Integer>> buildAdjacency() throws SQLException {
        String sql =
                "SELECT DISTINCT ma1.movie_id AS m1, ma2.movie_id AS m2 "
                        + "FROM movie_actors ma1 "
                        + "JOIN movie_actors ma2 ON ma1.actor_id = ma2.actor_id "
                        + "WHERE ma1.movie_id < ma2.movie_id";

        Map<Integer, Set<Integer>> adj = new HashMap<>();
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int m1 = rs.getInt("m1");
                int m2 = rs.getInt("m2");
                adj.computeIfAbsent(m1, k -> new HashSet<>()).add(m2);
                adj.computeIfAbsent(m2, k -> new HashSet<>()).add(m1);
            }
        }
        return adj;
    }

    private Map<Integer, Integer> colorGreedy(List<Integer> movieIds,
                                              Map<Integer, Set<Integer>> adj) {

        List<Integer> ordered = new ArrayList<>(movieIds);
        ordered.sort((a, b) -> Integer.compare(
                adj.getOrDefault(b, Collections.emptySet()).size(),
                adj.getOrDefault(a, Collections.emptySet()).size()));

        Map<Integer, Integer> color = new HashMap<>();
        for (int v : ordered) {
            Set<Integer> forbidden = new HashSet<>();
            for (int nb : adj.getOrDefault(v, Collections.emptySet())) {
                Integer c = color.get(nb);
                if (c != null) forbidden.add(c);
            }
            int c = 0;
            while (forbidden.contains(c)) c++;
            color.put(v, c);
        }
        System.out.println("Greedy coloring used " + distinctColors(color) + " colors for "
                + movieIds.size() + " movies.");
        return color;
    }

    private int distinctColors(Map<Integer, Integer> color) {
        return new HashSet<>(color.values()).size();
    }

    private List<List<Integer>> groupByColor(Map<Integer, Integer> color) {
        int k = distinctColors(color);
        List<List<Integer>> groups = new ArrayList<>();
        for (int i = 0; i < k; i++) groups.add(new ArrayList<>());

        Map<Integer, Integer> slotOf = new HashMap<>();
        for (int c : color.values()) slotOf.putIfAbsent(c, slotOf.size());
        for (Map.Entry<Integer, Integer> e : color.entrySet()) {
            groups.get(slotOf.get(e.getValue())).add(e.getKey());
        }
        return groups;
    }

    private void balance(List<List<Integer>> groups, Map<Integer, Set<Integer>> adj) {
        if (groups.size() <= 1) return;

        final int totalMovies = groups.stream().mapToInt(List::size).sum();
        final int maxIterations = totalMovies * groups.size();

        for (int iter = 0; iter < maxIterations; iter++) {
            int maxIdx = indexOfMax(groups);
            int minIdx = indexOfMin(groups);

            if (groups.get(maxIdx).size() - groups.get(minIdx).size() <= 1) {
                return;
            }

            boolean moved = tryMove(groups, adj, maxIdx, minIdx);
            if (!moved) {

                moved = tryMoveToAny(groups, adj, maxIdx);
            }
            if (!moved) {

                System.out.println("Balance: no further valid moves at iter " + iter
                        + ", stopping with imbalance.");
                return;
            }
        }
    }

    private boolean tryMove(List<List<Integer>> groups, Map<Integer, Set<Integer>> adj,
                            int fromIdx, int toIdx) {
        List<Integer> from = groups.get(fromIdx);
        List<Integer> to = groups.get(toIdx);
        Set<Integer> toSet = new HashSet<>(to);

        for (int i = 0; i < from.size(); i++) {
            int movie = from.get(i);
            if (!conflictsWith(movie, toSet, adj)) {
                from.remove(i);
                to.add(movie);
                return true;
            }
        }
        return false;
    }

    private boolean tryMoveToAny(List<List<Integer>> groups, Map<Integer, Set<Integer>> adj,
                                 int fromIdx) {
        int maxSize = groups.get(fromIdx).size();

        List<Integer> targetOrder = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            if (i != fromIdx && groups.get(i).size() < maxSize - 1) targetOrder.add(i);
        }
        targetOrder.sort(Comparator.comparingInt(i -> groups.get(i).size()));
        for (int t : targetOrder) {
            if (tryMove(groups, adj, fromIdx, t)) return true;
        }
        return false;
    }

    private boolean conflictsWith(int movie, Set<Integer> group,
                                  Map<Integer, Set<Integer>> adj) {
        Set<Integer> neighbors = adj.getOrDefault(movie, Collections.emptySet());
        for (int nb : neighbors) {
            if (group.contains(nb)) return true;
        }
        return false;
    }

    private int indexOfMax(List<List<Integer>> groups) {
        int idx = 0;
        for (int i = 1; i < groups.size(); i++) {
            if (groups.get(i).size() > groups.get(idx).size()) idx = i;
        }
        return idx;
    }

    private int indexOfMin(List<List<Integer>> groups) {
        int idx = 0;
        for (int i = 1; i < groups.size(); i++) {
            if (groups.get(i).size() < groups.get(idx).size()) idx = i;
        }
        return idx;
    }

    private PartitionStats computeStats(List<List<Integer>> groups) {
        PartitionStats s = new PartitionStats();
        List<List<Integer>> nonEmpty = groups.stream().filter(g -> !g.isEmpty()).toList();
        s.numLists = nonEmpty.size();
        s.totalMovies = nonEmpty.stream().mapToInt(List::size).sum();
        s.minSize = nonEmpty.stream().mapToInt(List::size).min().orElse(0);
        s.maxSize = nonEmpty.stream().mapToInt(List::size).max().orElse(0);
        return s;
    }
}