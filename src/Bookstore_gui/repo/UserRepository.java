// File: src/Bookstore_gui/repo/UserRepository.java
package Bookstore_gui.repo;

/**
 * User repository for Derby-embedded login.
 * - findByEmail: returns user id or null
 * - create: inserts and returns new user id
 * - findOrCreate: default composition of findByEmail + create
 */
public interface UserRepository {

    /** Returns user id if exists, else null. */
    String findByEmail(String email);

    /** Creates a new user and returns user id. */
    String create(String name, String email);

    /** Finds by email; if not found, creates a new user. */
    default String findOrCreate(String name, String email) {
        final String n  = norm(name);
        final String em = norm(email).toLowerCase();
        String id = findByEmail(em);
        return (id != null) ? id : create(n, em);
    }

    /** Minimal input normalization. */
    default String norm(String s) { return s == null ? "" : s.trim(); }
}