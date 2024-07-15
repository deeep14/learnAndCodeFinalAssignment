package org.learnAndCode;

public class User {
    private final String username;
    private final int roleId;

    public User(String username, int roleId) {
        this.username = username;
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public int getRoleId() {
        return roleId;
    }
}