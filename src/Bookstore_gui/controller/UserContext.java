package Bookstore_gui.controller;

/** Maintains the current logged-in user's session info. */
public class UserContext {

    private String userId;
    private String name;
    private String email;

    /** Set user info (used for login or registration). */
    public void setUser(String userId, String name, String email) {
        this.userId = (userId == null || userId.isBlank()) ? null : userId.trim();
        this.name   = (name == null   || name.isBlank())   ? null : name.trim();
        this.email  = (email == null  || email.isBlank())  ? null : email.trim();
    }

    /** Quick login without email (used in sign-in). */
    public void signIn(String userId, String name) {
        setUser(userId, name, null);
    }

    public String getUserId() { 
        return userId; 
    }
    public String getName()   {
        return name; 
    }
    public String getEmail()  {
        return email; 
    }

    /** True if user is logged in. */
    public boolean isSignedIn() { 
        return userId != null;
    }

    /** Clears user info on logout. */
    public void signOut() {
        userId = null;
        name = null;
        email = null;
    }
}