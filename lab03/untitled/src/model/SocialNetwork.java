package model;

import java.util.*;

public class SocialNetwork {
    private List<Profile> profiles = new ArrayList<>();

    public void addProfile(Profile profile) {
        profiles.add(profile);
    }

    public boolean removeProfile(Profile profile) {
        return profiles.remove(profile);
    }

    public List<Profile> getProfiles() {
        return Collections.unmodifiableList(profiles);
    }

    public List<Profile> getSortedProfiles() {
        List<Profile> sorted = new ArrayList<>(profiles);
        sorted.sort(Comparator.comparing(Profile::getName));
        return sorted;
    }

    public List<Profile> getProfilesByImportance() {
        List<Profile> sorted = new ArrayList<>(profiles);
        sorted.sort(Comparator.comparingInt(Profile::getImportance).reversed());
        return sorted;
    }

    public List<Profile> findArticulationPoints() {
        Map<Profile, Set<Profile>> adjacency = buildAdjacencyList();
        Map<Profile, Integer> disc = new HashMap<>();
        Map<Profile, Integer> low = new HashMap<>();
        Map<Profile, Profile> parent = new HashMap<>();
        Set<Profile> visited = new HashSet<>();
        Set<Profile> articulationPoints = new HashSet<>();
        int[] timer = {0};

        for (Profile p : profiles) {
            if (!visited.contains(p)) {
                dfs(p, adjacency, disc, low, parent, visited, articulationPoints, timer);
            }
        }

        return new ArrayList<>(articulationPoints);
    }

    public List<Set<Profile>> findBiconnectedComponents() {
        Map<Profile, Set<Profile>> adjacency = buildAdjacencyList();
        Map<Profile, Integer> disc = new HashMap<>();
        Map<Profile, Integer> low = new HashMap<>();
        Map<Profile, Profile> parent = new HashMap<>();
        Set<Profile> visited = new HashSet<>();
        Deque<Profile[]> edgeStack = new ArrayDeque<>();
        List<Set<Profile>> components = new ArrayList<>();
        int[] timer = {0};

        for (Profile p : profiles) {
            if (!visited.contains(p)) {
                dfsComponents(p, adjacency, disc, low, parent, visited, edgeStack, components, timer);
            }
        }

        return components;
    }

    private Map<Profile, Set<Profile>> buildAdjacencyList() {
        Map<Profile, Set<Profile>> adjacency = new LinkedHashMap<>();
        for (Profile p : profiles) {
            adjacency.put(p, new LinkedHashSet<>());
        }
        for (Profile p : profiles) {
            if (p instanceof Person person) {
                for (Profile neighbor : person.getRelationships().keySet()) {
                    adjacency.get(p).add(neighbor);
                    adjacency.get(neighbor).add(p);
                }
            }
        }
        return adjacency;
    }

    private void dfs(Profile u,
                     Map<Profile, Set<Profile>> adjacency,
                     Map<Profile, Integer> disc,
                     Map<Profile, Integer> low,
                     Map<Profile, Profile> parent,
                     Set<Profile> visited,
                     Set<Profile> articulationPoints,
                     int[] timer) {
        visited.add(u);
        disc.put(u, timer[0]);
        low.put(u, timer[0]);
        timer[0]++;
        int children = 0;

        for (Profile v : adjacency.get(u)) {
            if (!visited.contains(v)) {
                children++;
                parent.put(v, u);
                dfs(v, adjacency, disc, low, parent, visited, articulationPoints, timer);
                low.put(u, Math.min(low.get(u), low.get(v)));

                if (parent.get(u) == null && children > 1) {
                    articulationPoints.add(u);
                }
                if (parent.get(u) != null && low.get(v) >= disc.get(u)) {
                    articulationPoints.add(u);
                }
            } else if (!v.equals(parent.get(u))) {
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }
    }

    private void dfsComponents(Profile u,
                               Map<Profile, Set<Profile>> adjacency,
                               Map<Profile, Integer> disc,
                               Map<Profile, Integer> low,
                               Map<Profile, Profile> parent,
                               Set<Profile> visited,
                               Deque<Profile[]> edgeStack,
                               List<Set<Profile>> components,
                               int[] timer) {
        visited.add(u);
        disc.put(u, timer[0]);
        low.put(u, timer[0]);
        timer[0]++;
        int children = 0;

        for (Profile v : adjacency.get(u)) {
            if (!visited.contains(v)) {
                children++;
                parent.put(v, u);
                edgeStack.push(new Profile[]{u, v});
                dfsComponents(v, adjacency, disc, low, parent, visited, edgeStack, components, timer);
                low.put(u, Math.min(low.get(u), low.get(v)));

                boolean isArticulation =
                        (parent.get(u) == null && children > 1) ||
                                (parent.get(u) != null && low.get(v) >= disc.get(u));

                if (isArticulation) {
                    Set<Profile> component = new LinkedHashSet<>();
                    while (true) {
                        Profile[] edge = edgeStack.pop();
                        component.add(edge[0]);
                        component.add(edge[1]);
                        if (edge[0].equals(u) && edge[1].equals(v)) break;
                    }
                    components.add(component);
                }
            } else if (!v.equals(parent.get(u)) && disc.get(v) < disc.get(u)) {
                edgeStack.push(new Profile[]{u, v});
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }

        if (parent.get(u) == null && !edgeStack.isEmpty()) {
            Set<Profile> component = new LinkedHashSet<>();
            while (!edgeStack.isEmpty()) {
                Profile[] edge = edgeStack.pop();
                component.add(edge[0]);
                component.add(edge[1]);
            }
            components.add(component);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Social Network ===\n");

        for (Profile p : getProfilesByImportance()) {
            sb.append(String.format("%n[Importance: %d] %s (ID: %s)",
                    p.getImportance(), p.getName(), p.getProfileId()));

            if (p instanceof Person person) {
                sb.append(String.format(" | Born: %s | Email: %s",
                        person.getBirthDate(), person.getEmail()));

                if (person instanceof Programmer programmer) {
                    sb.append(String.format(" | Language: %s", programmer.getPrimaryLanguage()));
                } else if (person instanceof Designer designer) {
                    sb.append(String.format(" | Portfolio: %s", designer.getPortfolioUrl()));
                }

                if (!person.getRelationships().isEmpty()) {
                    sb.append("\n  Relationships:");
                    for (var entry : person.getRelationships().entrySet()) {
                        sb.append("\n    -> ").append(entry.getValue().getDescription());
                    }
                }
            } else if (p instanceof Company company) {
                sb.append(String.format(" | Industry: %s", company.getIndustry()));
            }

            sb.append("\n");
        }

        return sb.toString();
    }
}