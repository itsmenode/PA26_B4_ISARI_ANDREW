package models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Article extends BibliographicReference {
    private String journal;
    private String volume;
    private String doi;

    public Article(String id, String title, String location, int year, String author) {
        super(id, title, location, year, author);
    }

    public Article(String id, String title, String location, int year, String author, String journal, String volume, String doi) {
        super(id, title, location, year, author);
        this.journal = journal;
        this.volume = volume;
        this.doi = doi;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.deleteCharAt(sb.length() - 1);
        if (journal != null) sb.append(String.format(", \"journal\":\"%s\"", journal));
        if (volume != null) sb.append(String.format(", \"volume\":\"%s\"", volume));
        if (doi != null) sb.append(String.format(", \"doi\":\"%s\"", doi));
        sb.append("}");
        return sb.toString();
    }
}