package model;
public class Person {
    private String identity;
    private String fullName;
    private String dateOfBirth;

    public Person(String identity, String fullName, String dateOfBirth) {
        this.identity = identity;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    public void setInfon(String identity, String fullName, String dateOfBirth) {
        this.identity = identity;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getIdentity() { return identity; }
    public String getFullName() { return fullName; }
    public String getDateOfBirth() { return dateOfBirth; }
}
