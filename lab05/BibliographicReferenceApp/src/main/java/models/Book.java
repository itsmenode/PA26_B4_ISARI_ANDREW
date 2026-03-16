package models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Book extends BibliographicReference {
    private String isbn;
    private String publisher;
    private int edition;

    public Book(String id, String title, String location, int year, String author) {
        super(id, title, location, year, author);
    }

    public Book(String id, String title, String location, int year, String author,
                String isbn, String publisher, int edition) {
        super(id, title, location, year, author);
        this.isbn = isbn;
        this.publisher = publisher;
        this.edition = edition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.deleteCharAt(sb.length() - 1);
        if (isbn != null) sb.append(String.format(", \"isbn\":\"%s\"", isbn));
        if (publisher != null) sb.append(String.format(", \"publisher\":\"%s\"", publisher));
        if (edition > 0) sb.append(String.format(", \"edition\":\"%d\"", edition));
        sb.append("}");
        return sb.toString();
    }
}