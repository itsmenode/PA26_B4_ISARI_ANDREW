package models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class RepositoryControl {
    @EqualsAndHashCode.Include
    Set<BibliographicReferences> referenceList = new HashSet<>();

    public boolean add(BibliographicReferences newReference){
        if (!referenceList.add(newReference)) throw new RuntimeException("[ERROR] Could not add this reference. Please try again. [ERROR]");
        return true;
    }

    public boolean remove(BibliographicReferences referenceToRemove) {
        if (referenceList.contains(referenceToRemove)) throw new RuntimeException("[ERROR] Reference you want to remove does not exist. [ERROR]");
        if (!referenceList.remove(referenceToRemove)) throw new RuntimeException("[ERROR] Could not remove this reference. Please try again. [ERROR]");
        return true;
    }

    public boolean updateAll(BibliographicReferences targetReference, BibliographicReferences updateReference) {
        if (referenceList.contains(targetReference)) throw new RuntimeException("[ERROR] Target reference does not exist. [ERROR]");
        remove(targetReference);
        add(updateReference);
        return true;
    }

}
