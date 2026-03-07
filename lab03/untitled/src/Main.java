import enums.jobTitle;
import model.*;
import relationshipService.PersonToCompany;
import relationshipService.PersonToPerson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // --- Persons ---
        Person alice     = new Programmer("p1", "Alice",   LocalDate.of(1995, 3, 12), "alice@mail.com",   "Java");
        Person bob       = new Designer  ("p2", "Bob",     LocalDate.of(1992, 7, 24), "bob@mail.com",     "https://bobdesigns.io");
        Person charlie   = new Person    ("p3", "Charlie", LocalDate.of(1988, 11, 5), "charlie@mail.com");

        // --- Companies ---
        Company acme = new Company("c1", "Acme Corp", "Software");
        Company beta = new Company("c2", "Beta Ltd",  "Design");

        // --- Relationships ---
        PersonToPerson rel1 = new PersonToPerson(alice, bob,     "Colleagues from university");
        PersonToPerson rel2 = new PersonToPerson(bob,   charlie, "Met at a conference");

        PersonToCompany emp1 = new PersonToCompany((Person) alice, acme, jobTitle.Programmer);
        PersonToCompany emp2 = new PersonToCompany(charlie,        acme, jobTitle.Designer);
        PersonToCompany emp3 = new PersonToCompany((Person) bob,   beta, jobTitle.Designer);

        // --- Mixed sorted list ---
        List<Profile> network = new ArrayList<>();
        network.add(alice);
        network.add(bob);
        network.add(charlie);
        network.add(acme);
        network.add(beta);

        network.sort(Comparator.comparing(Profile::getName));

        System.out.println("=== Network (sorted by name) ===");
        for (Profile p : network) {
            System.out.println(p.getName() + " [" + p.getProfileId() + "]");
        }

        System.out.println("\n=== Person-to-Person Relationships ===");
        for (PersonToPerson rel : List.of(rel1, rel2)) {
            System.out.println(rel.getFirstPerson().getName()
                    + " knows " + rel.getSecondPerson().getName()
                    + " -> " + rel.getContextOfRelationship());
        }

        System.out.println("\n=== Person-to-Company Relationships ===");
        for (PersonToCompany emp : List.of(emp1, emp2, emp3)) {
            System.out.println(emp.getPerson().getName()
                    + " works at " + emp.getCompany().getName()
                    + " as " + emp.getPosition());
        }
    }
}