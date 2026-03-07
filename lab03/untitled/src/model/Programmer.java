package model;

import java.time.LocalDate;

public class Programmer extends Person {
    private String primaryLanguage;

    public Programmer(String profileId, String name, LocalDate birthDate, String email, String primaryLanguage) {
        super(profileId, name, birthDate, email);
        this.primaryLanguage = primaryLanguage;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }
}
