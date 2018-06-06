package com.litty.userLocationPackage;

public class userCredential {
    private String username;
    private String password;

    public userCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
