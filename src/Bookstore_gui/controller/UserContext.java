package Bookstore_gui.controller;

public class UserContext {
    private String userId;
    private String name;
    private String email;

    public void setUser(String userId, String name, String email) {
        this.userId = userId;
        this.name   = name;
        this.email  = email;
    }

    // ✅ StartFrame 호환용 signIn
    public void signIn(String email, String name) {
        this.userId = email;  // 임시로 email을 id로 사용
        this.name   = name;
        this.email  = email;
    }

    public String getUserId() { return userId; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }
    public boolean isSignedIn() { return userId != null; }
    public void signOut() { userId = name = email = null; }
}