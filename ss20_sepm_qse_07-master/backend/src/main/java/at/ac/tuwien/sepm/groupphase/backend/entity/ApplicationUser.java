package at.ac.tuwien.sepm.groupphase.backend.entity;

//TODO: replace this class with a correct ApplicationUser Entity implementation
public class ApplicationUser {

    private String username;
    private String password;
    private Boolean admin;

    public ApplicationUser() {
    }

    public ApplicationUser(String username, String password, Boolean admin) {
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String email) {
        this.username = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
