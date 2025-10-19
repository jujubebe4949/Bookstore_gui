// path: src/Bookstore_gui/db/DbUserRepository.java
package Bookstore_gui.db;

import Bookstore_gui.repo.UserRepository;
import Bookstore_gui.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Derby impl with password auth (salt+hash). Name-based login ready. */
public class DbUserRepository implements UserRepository {

    public DbUserRepository() {
        try { DbManager.initSchema(); } catch (Exception ignore) {}
    }
 // register / authenticate (email) / authenticateByName (name)
   
    @Override
    public String register(String name, String email, String password) {
        final String em = norm(email).toLowerCase();
        final String nm = norm(name);

        if (em.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("email/password required");
        }
        
        if (findByEmail(em) != null) {
            throw new RuntimeException("Email already registered");
        }
       
        if (findIdByNameLower(nm.toLowerCase()) != null) {
            throw new RuntimeException("Name already taken");
        }

        final String id   = UUID.randomUUID().toString();
        final String salt = PasswordUtil.newSaltHex();
        final String hash = PasswordUtil.hashHex(password, salt);

        final String sql =
            "INSERT INTO Users (id, name, email, password_salt, password_hash) VALUES (?,?,?,?,?)";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, nm.isBlank() ? "User" : nm);
            ps.setString(3, em);
            ps.setString(4, salt);
            ps.setString(5, hash);
            ps.executeUpdate();
            return id;
        } catch (SQLException e) {
            
            if ("23505".equals(e.getSQLState())) {
                throw new RuntimeException("Email already registered");
            }
            throw new RuntimeException("register failed", e);
        }
    }

    @Override
    public String authenticate(String email, String password) {
        final String em = norm(email).toLowerCase();
        final String sql = "SELECT id, password_salt, password_hash FROM Users WHERE email = ?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, em);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                final String id   = rs.getString(1);
                final String salt = rs.getString(2);
                final String hash = rs.getString(3);
                if (salt == null || hash == null) return null; // legacy user guard
                return PasswordUtil.verify(password, salt, hash) ? id : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("authenticate failed", e);
        }
    }

    @Override
    public String authenticateByName(String name, String password) {
        final String nmLower = norm(name).toLowerCase();
        final String sql = "SELECT id, password_salt, password_hash FROM Users WHERE LOWER(name) = ?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nmLower);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                final String id   = rs.getString(1);
                final String salt = rs.getString(2);
                final String hash = rs.getString(3);
                if (salt == null || hash == null) return null; 
                return PasswordUtil.verify(password, salt, hash) ? id : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("authenticateByName failed", e);
        }
    }
// legacy path kept for compatibility
    
    @Override
    public String findOrCreate(String name, String email) {
        final String em = norm(email).toLowerCase();
        final String existing = findByEmail(em);
        return (existing != null) ? existing : create(norm(name), em);
    }

    @Override
    public String findByEmail(String email) {
        final String sql = "SELECT id FROM Users WHERE email = ?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, norm(email).toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            // include chained Derby exceptions for XJ040 root cause
            StringBuilder msg = new StringBuilder("findByEmail failed");
            SQLException next = e.getNextException();
            if (next != null && next.getMessage() != null) {
                msg.append(" (").append(next.getMessage()).append(")");
            }
            throw new RuntimeException(msg.toString(), e);
        }
    }

    @Override
    public String create(String name, String email) {
        final String id = UUID.randomUUID().toString();
        final String sql = "INSERT INTO Users (id, name, email) VALUES (?,?,?)";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, norm(name));
            ps.setString(3, norm(email).toLowerCase());
            ps.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("create user failed", e);
        }
    }

     // queries used elsewhere

    public List<String> findAllUsers() {
        final List<String> list = new ArrayList<>();
        final String sql = "SELECT name || ' <' || email || '>' FROM Users ORDER BY name";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAllUsers failed", e);
        }
    }

    @Override
    public boolean updateUserName(String userId, String newName) {
        final String nm = norm(newName);
        if (nm.isBlank()) return false;

        final String checkSql = "SELECT id FROM Users WHERE LOWER(name) = ? AND id <> ?";
        try (Connection c = DbManager.connect();
             PreparedStatement chk = c.prepareStatement(checkSql)) {
            chk.setString(1, nm.toLowerCase());
            chk.setString(2, userId);
            try (ResultSet rs = chk.executeQuery()) {
                if (rs.next()) {
                   
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("updateUserName check failed", e);
        }

        final String sql = "UPDATE Users SET name = ? WHERE id = ?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nm);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updateUserName failed: " + userId, e);
        }
    }

    private String findIdByNameLower(String nameLower) {
        final String sql = "SELECT id FROM Users WHERE LOWER(name) = ?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nameLower);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findIdByNameLower failed", e);
        }
    }
}