import lombok.Getter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class City {

    private final String name;
    private final LinkedList<Street> streets;
    private final Set<Intersection> intersections;

    public City(String name, List<Street> streets) {
        this.name = name;
        this.streets = new LinkedList<>(streets);
        this.intersections = Stream.concat(
                streets.stream().map(Street::getStart),
                streets.stream().map(Street::getEnd)
        ).collect(Collectors.toCollection(HashSet::new));
    }

    public Map<Intersection, Long> getIntersectionDegrees() {
        return Stream.concat(
                streets.stream().map(Street::getStart),
                streets.stream().map(Street::getEnd)
        ).collect(Collectors.groupingBy(i -> i, Collectors.counting()));
    }

    public List<Street> getStreetsLongerThanWithMinDegree(double minLength, int minDegree) {
        Map<Intersection, Long> degrees = getIntersectionDegrees();
        return streets.stream()
                .filter(s -> s.getLength() > minLength)
                .filter(s -> degrees.getOrDefault(s.getStart(), 0L) >= minDegree
                        || degrees.getOrDefault(s.getEnd(),   0L) >= minDegree)
                .sorted()
                .collect(Collectors.toList());
    }

    public LinkedList<Street> getStreetsSortedByLength() {
        LinkedList<Street> sorted = new LinkedList<>(streets);
        sorted.sort((s1, s2) -> Double.compare(s1.getLength(), s2.getLength()));
        return sorted;
    }

    public double getTotalStreetLength() {
        return streets.stream()
                .mapToDouble(Street::getLength)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("City(%s) -- %d intersections, %d streets, %.1f m total",
                name, intersections.size(), streets.size(), getTotalStreetLength());
    }
}