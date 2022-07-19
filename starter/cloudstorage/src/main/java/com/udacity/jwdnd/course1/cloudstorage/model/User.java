package com.udacity.jwdnd.course1.cloudstorage.model;

public class User {
    private Integer userid;
    private String username;
    private String salt;
    private String password;
    private String firstname;
    private String lastname;

    public User(Integer userId, String username, String salt, String password, String firstName, String lastName) {
        this.userid = userId;
        this.username = username;
        this.salt = salt;
        this.password = password;
        this.firstname = firstName;
        this.lastname = lastName;
    }

    public Integer getUserId() {
        return userid;
    }

    public void setUserId(Integer userId) {
        this.userid = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstName) {
        this.firstname = firstName;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastName) {
        this.lastname = lastName;
    }
}
