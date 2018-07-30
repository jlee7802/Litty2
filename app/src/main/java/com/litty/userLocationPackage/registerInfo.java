package com.litty.userLocationPackage;

import java.sql.Date;
import java.time.LocalDate;

public class registerInfo {
    private String email;
    private LocalDate dob;
    private int gender;
    private int race;
    private String password;
    private String ConfirmPassword;

    public registerInfo(String email, LocalDate dob, int gender, int race, String password, String ConfirmPassword) {
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.race = race;
        this.password = password;
        this.ConfirmPassword = ConfirmPassword;
    }

    public String getEmail() { return email; }
    public LocalDate getDob() { return dob; }
    public int getGender() { return gender; }
    public int getRace() { return race; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return ConfirmPassword; }
}
