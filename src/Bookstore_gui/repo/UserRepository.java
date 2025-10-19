// File: src/Bookstore_gui/repo/UserRepository.java
package Bookstore_gui.repo;

/**
 * User repository supporting name-based and password-based login and registration.
 * Derby-compatible (Embedded).
 */
public interface UserRepository {

    /** Finds a user ID by email (used for registration compatibility). */
    String findByEmail(String email);

    /** Creates a basic user entry (legacy or internal use). */
    String create(String name, String email);

    /** Finds or creates a user entry (legacy path). */
    default String findOrCreate(String name, String email) {
        final String n  = norm(name);
        final String em = norm(email).toLowerCase();
        String id = findByEmail(em);
        return (id != null) ? id : create(n, em);
    }
    String register(String name, String email, String password);

    String authenticate(String email, String password);

    String authenticateByName(String name, String password);

    boolean updateUserName(String userId, String newName);

    default String norm(String s) {
        return s == null ? "" : s.trim();
    }
}