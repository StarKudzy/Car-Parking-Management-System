package com.example.demo;

public class Staff {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String role;
    private String password;

    public Staff(int id, String firstName, String lastName, String username, String role, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.role = role;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUsername() {
        return username;
    }
    public String getRole() {
        return role;
    }
    public String getPassword() {
        return password;
    }
}
