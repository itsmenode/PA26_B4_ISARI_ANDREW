package model;

public class Company extends Network implements Profile, Comparable<Company> {
    private String name;
    private String industry;

    public Company(String profileId, String name, String industry) {
        super(profileId);
        this.name = name;
        this.industry = industry;
    }

    @Override
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getIndustry() { return industry; }

    public void setIndustry(String industry) { this.industry = industry; }

    @Override
    public int compareTo(Company other) {
        return this.name.compareTo(other.name);
    }
}