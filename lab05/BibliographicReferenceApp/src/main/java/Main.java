import enums.ResourceType;
import models.BibliographicReferences;
import models.CommandOperationExecutor;
import models.RepositoryControl;
import models.commands.AddCommand;
import models.commands.ListCommand;
import models.commands.LoadCommand;
import models.commands.ReportCommand;
import models.commands.ViewCommand;

public class Main {
    public static void main(String[] args) {
        RepositoryControl catalog = new RepositoryControl();
        CommandOperationExecutor executor = new CommandOperationExecutor();

        BibliographicReferences knuth = new BibliographicReferences("knuth67", "The Art of Computer Programming",
                "d:/books/programming/tacp.ps", 1967, "Donald E. Knuth", ResourceType.Book);

        BibliographicReferences jvm = new BibliographicReferences("jvm25", "The Java Virtual Machine Specification",
                "https://docs.oracle.com/javase/specs/jvms/se25/html/index.html", 2025, "Tim Lindholm & others", ResourceType.Article);

        BibliographicReferences jls = new BibliographicReferences("java25", "The Java Language Specification",
                "https://docs.oracle.com/javase/specs/jls/se25/jls25.pdf", 2025, "James Gosling & others", ResourceType.Article);

        executor.executeOpration(new AddCommand(catalog, knuth));
        executor.executeOpration(new AddCommand(catalog, jvm));
        executor.executeOpration(new AddCommand(catalog, jls));

        executor.executeOpration(new ListCommand(catalog));
        executor.executeOpration(new LoadCommand(catalog, jvm));
        executor.executeOpration(new ViewCommand(catalog, jls));
        executor.executeOpration(new ReportCommand(catalog));
    }
}