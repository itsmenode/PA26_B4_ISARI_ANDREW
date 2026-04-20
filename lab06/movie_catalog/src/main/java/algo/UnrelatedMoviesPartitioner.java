package algo;

import model.Actor;
import model.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Partitions movies into lists that contain only *unrelated* movies,
 * where two movies are "related" if they share at least one actor.
 *
 * Algorithm:
 *   1. Build a conflict graph: vertex = movie, edge = shared actor.
 *   2. Greedy graph coloring with Welsh-Powell (highest-degree-first)
 *      gives an approximate minimum number of lists (colors).
 *   3. Rebalance lists by moving movies from the largest list to the
 *      smallest compatible list until every pair of lists differs in
 *      size by at most 1 (or no legal move remains).
 *
 * Graph coloring is NP-hard; the greedy heuristic is fast and gives a
 * good answer on small / medium datasets.
 */
public class UnrelatedMoviesPartitioner {

    public List<List<Movie>> partition(List<Movie> movies) {
        int n = movies.size();
        if (n == 0) {
            return new ArrayList<>();
        }

        List<Set<Integer>> actorIds = new ArrayList<>(n);
        for (Movie m : movies) {
            actorIds.add(m.getActors().stream()
                    .map(Actor::getId)
                    .collect(Collectors.toSet()));
        }

        List<Set<Integer>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new HashSet<>());
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (!Collections.disjoint(actorIds.get(i), actorIds.get(j))) {
                    adj.get(i).add(j);
                    adj.get(j).add(i);
                }
            }
        }

        List<Integer> order = new ArrayList<>(n);
        for (int i = 0; i < n; i++) order.add(i);
        order.sort((a, b) -> adj.get(b).size() - adj.get(a).size());

        int[] color = new int[n];
        for (int i = 0; i < n; i++) color[i] = -1;
        int numColors = 0;
        for (int v : order) {
            Set<Integer> used = new HashSet<>();
            for (int u : adj.get(v)) {
                if (color[u] != -1) used.add(color[u]);
            }
            int c = 0;
            while (used.contains(c)) c++;
            color[v] = c;
            if (c + 1 > numColors) numColors = c + 1;
        }

        List<List<Integer>> bins = new ArrayList<>(numColors);
        for (int i = 0; i < numColors; i++) bins.add(new ArrayList<>());
        for (int i = 0; i < n; i++) bins.get(color[i]).add(i);

        balance(bins, adj);

        List<List<Movie>> result = new ArrayList<>(bins.size());
        for (List<Integer> bin : bins) {
            List<Movie> list = new ArrayList<>(bin.size());
            for (int i : bin) list.add(movies.get(i));
            result.add(list);
        }
        return result;
    }

    private void balance(List<List<Integer>> bins, List<Set<Integer>> adj) {
        boolean progress = true;
        while (progress) {
            progress = false;
            int biggest = indexOfLargest(bins);
            int smallest = indexOfSmallest(bins);
            if (bins.get(biggest).size() - bins.get(smallest).size() <= 1) {
                return;
            }
            List<Integer> from = bins.get(biggest);
            List<Integer> to = bins.get(smallest);

            for (int v : new ArrayList<>(from)) {
                boolean conflict = false;
                for (int u : to) {
                    if (adj.get(v).contains(u)) {
                        conflict = true;
                        break;
                    }
                }
                if (!conflict) {
                    from.remove(Integer.valueOf(v));
                    to.add(v);
                    progress = true;
                    break;
                }
            }
        }
    }

    private int indexOfLargest(List<List<Integer>> bins) {
        int idx = 0;
        for (int i = 1; i < bins.size(); i++) {
            if (bins.get(i).size() > bins.get(idx).size()) idx = i;
        }
        return idx;
    }

    private int indexOfSmallest(List<List<Integer>> bins) {
        int idx = 0;
        for (int i = 1; i < bins.size(); i++) {
            if (bins.get(i).size() < bins.get(idx).size()) idx = i;
        }
        return idx;
    }
}
