package com.litty.userLocationPackage;

public class userCredential {
    private String email;
    private String password;

    public userCredential(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
