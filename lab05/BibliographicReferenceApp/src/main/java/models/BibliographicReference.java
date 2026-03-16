package models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public abstract class BibliographicReference {
    @EqualsAndHashCode.Include
    private String id;

    private String title;
    private String location;
    private int year;
    private String author;

    public BibliographicReference(String id, String title, String location, int year, String author) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.year = year;
        this.author = author;
    }

    @Override
    public String toString() {
        return String.format("{\"id\":\"%s\", \"title\":\"%s\", \"location\":\"%s\", \"year\":\"%d\", \"author\":\"%s\"}", id, title, location, year, author);
    }
}