// File: src/Bookstore_gui/repo/UserRepository.java
package Bookstore_gui.repo;

/**
 * User repository supporting password-based login and registration.
 * Derby-compatible (Embedded).
 */
public interface UserRepository {

    String findByEmail(String email);
    String create(String name, String email);

    default String findOrCreate(String name, String email) {
        final String n  = norm(name);
        final String em = norm(email).toLowerCase();
        String id = findByEmail(em);
        return (id != null) ? id : create(n, em);
    }

    /** Registers a new user with password and returns the user id. */
    String register(String name, String email, String password);

    /** Authenticates user. Returns user id if valid credentials, else null. */
    String authenticate(String email, String password);

    /** Updates user’s display name. */
    boolean updateUserName(String userId, String newName);
    
    String authenticateByName(String name, String password); 
    
    /** Minimal input normalization. */
    default String norm(String s) { return s == null ? "" : s.trim(); }
}