import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(includeFieldNames = false)
public class Intersection implements Comparable<Intersection> {

    @EqualsAndHashCode.Include
    private String name;

    @Override
    public int compareTo(Intersection other) {
        return this.name.compareTo(other.name);
    }
}