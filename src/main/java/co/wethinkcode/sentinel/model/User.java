package co.wethinkcode.sentinel.model;

public class User {

    private String username;
    private String password;
    private String role;
    private boolean blocked;

    public User(String username, String password, String role) {
        this(username, password, role, false);
    }

    public User(String username, String password, String role, boolean blocked) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.blocked = blocked;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}