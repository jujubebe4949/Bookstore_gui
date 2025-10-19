package Bookstore_gui.controller;

public class UserContext {

    private String userId;  
    private String name;    
    private String email;   

    public void setUser(String userId, String name, String email) {
        this.userId = (userId == null || userId.isBlank()) ? null : userId.trim();
        this.name   = (name == null   || name.isBlank())   ? null : name.trim();
        this.email  = (email == null  || email.isBlank())  ? null : email.trim();
    }

    public void signIn(String userId, String name) {
        setUser(userId, name, null);
    }

    //Getters
    public String getUserId()   { return userId; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }

    
    public boolean isSignedIn() { return userId != null; }

    public void signOut() {
        userId = null;
        name = null;
        email = null;
    }
}