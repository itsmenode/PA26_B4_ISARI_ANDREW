package model;

import java.time.LocalDate;

public class Person extends Network implements Profile, Comparable<Person> {
    private String name;
    private LocalDate birthDate;
    private String email;

    public Person(String profileId, String name, LocalDate birthDate, String email) {
        super(profileId);
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
    }

    @Override
    public String getName() { return name; }

    public LocalDate getBirthDate() { return birthDate; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    @Override
    public int compareTo(Person other) {
        return this.name.compareTo(other.name);
    }
}