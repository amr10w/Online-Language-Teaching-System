package Main;
public abstract class   User {
    private String username;
    private String email;
    private String password;
    private String ID;


    public User(String username, String email, String password, String ID) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
