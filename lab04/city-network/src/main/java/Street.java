import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Street implements Comparable<Street> {

    private String name;
    private double length;
    private Intersection start;
    private Intersection end;

    @Override
    public int compareTo(Street other) {
        return Double.compare(this.length, other.length);
    }

    @Override
    public String toString() {
        return String.format("Street(%-18s %6.1f m  [%s <-> %s])",
                "\"" + name + "\"", length, start.getName(), end.getName());
    }
}