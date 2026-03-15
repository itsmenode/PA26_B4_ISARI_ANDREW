import net.datafaker.Faker;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NameGenerator {

    private static final Faker FAKER = new Faker();
    private static final int MIN_STREET_LENGTH = 100;
    private static final int MAX_STREET_LENGTH = 2000;

    public static City createRandom(int intersectionCount, int streetCount) {
        List<Intersection> intersections = generateIntersections(intersectionCount);
        List<Street> streets = generateStreets(streetCount, intersections);
        return new City(FAKER.address().city(), streets);
    }

    private static List<Intersection> generateIntersections(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Intersection(FAKER.address().streetName()))
                .collect(Collectors.toList());
    }

    private static List<Street> generateStreets(int count, List<Intersection> intersections) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Intersection start = intersections.get(FAKER.number().numberBetween(0, intersections.size()));
                    Intersection end   = intersections.get(FAKER.number().numberBetween(0, intersections.size()));
                    double length      = FAKER.number().randomDouble(1, MIN_STREET_LENGTH, MAX_STREET_LENGTH);
                    return new Street(FAKER.address().streetName(), length, start, end);
                })
                .collect(Collectors.toList());
    }
}