
package com.cs.cs_learning.model;

public class User {

    public enum Qualification {
        BACHELORS,
        MASTERS,
        DIPLOMA,
        NONE
    }

    private final long userId;
    private final String username;
    private final String identityNumber;
    private final String email;
    private final String phoneNumber;
    private final Qualification qualification;

    public User(long userId, String username, String identityNumber,
                String email, String phoneNumber,
                Qualification qualification) {
        this.userId = userId;
        this.username = username;
        this.identityNumber = identityNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.qualification = qualification;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Qualification getQualification() {
        return qualification;
    }
}
