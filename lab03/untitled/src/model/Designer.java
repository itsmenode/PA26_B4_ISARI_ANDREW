package model;

import java.time.LocalDate;

public class Designer extends Person{
    public String portfolioUrl;

    public Designer(String profileId, String name, LocalDate birthDate, String email, String portfolioUrl) {
        super(profileId, name, birthDate, email);
        this.portfolioUrl = portfolioUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }
}
