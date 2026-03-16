import enums.ResourceType;
import models.BibliographicReferences;
import models.RepositoryControl;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    public static void main() {
        RepositoryControl catalog = new RepositoryControl();

        catalog.add(new BibliographicReferences("knuth67", "The Art of Computer Programming",
                "d:/books/programming/tacp.ps", 1967, "Donald E. Knuth", ResourceType.Book));

        catalog.add(new BibliographicReferences("jvm25", "The Java Virtual Machine Specification", "https://docs.oracle.com/javase/specs/jvms/se25/html/index.html", 2025, "Tim Lindholm & others", ResourceType.Article));

        catalog.add(new BibliographicReferences("java25", "The Java Language Specification", "https://docs.oracle.com/javase/specs/jls/se25/jls25.pdf", 2025, "James Gosling & others", ResourceType.Article));


    }

    public static void open(String location) {
        if (!Desktop.isDesktopSupported()) throw new RuntimeException("[ERROR] Desktop is not supported on this system. [ERROR]");

        Desktop desktop = Desktop.getDesktop();

        try {
            if (location.startsWith("http://") || location.startsWith("https://")) {
                desktop.browse(new URI(location));
            }
            else {
                desktop.open(new File(location));
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("[ERROR] Could not open target location. [ERROR]");
        }

    }
}
