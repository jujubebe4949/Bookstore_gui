// path: src/Bookstore_gui/db/DbUserRepository.java
package Bookstore_gui.db;

import Bookstore_gui.repo.UserRepository;
import Bookstore_gui.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Derby impl with password auth (salt+hash). Keeps legacy paths. */
public class DbUserRepository implements UserRepository {

    public DbUserRepository() {
        try { DbManager.initSchema(); } catch (Exception ignore) {}
    }

    // ---------- new (password-based) ----------

    @Override
    public String register(String name, String email, String password) {
        String em = norm(email).toLowerCase();
        String nm = norm(name);
        if (em.isBlank() || password == null || password.isBlank())
            throw new IllegalArgumentException("email/password required");

        if (findByEmail(em) != null) throw new RuntimeException("Email already registered");

        String id   = UUID.randomUUID().toString();
        String salt = PasswordUtil.newSaltHex();
        String hash = PasswordUtil.hashHex(password, salt);

        String sql = "INSERT INTO Users (id,name,email,password_salt,password_hash) VALUES (?,?,?,?,?)";
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
            if ("23505".equals(e.getSQLState())) throw new RuntimeException("Email already registered");
            throw new RuntimeException("register failed", e);
        }
    }

    @Override
    public String authenticate(String email, String password) {
        String em = norm(email).toLowerCase();
        String sql = "SELECT id, password_salt, password_hash FROM Users WHERE email=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, em);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String id   = rs.getString(1);
                String salt = rs.getString(2);
                String hash = rs.getString(3);
                if (salt == null || hash == null) return null; // legacy users
                return PasswordUtil.verify(password, salt, hash) ? id : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("authenticate failed", e);
        }
    }

    // ---------- legacy (passwordless) ----------

    @Override
    public String findOrCreate(String name, String email) {
        String em = norm(email).toLowerCase();
        String existing = findByEmail(em);
        return (existing != null) ? existing : create(norm(name), em);
    }

    // path: src/Bookstore_gui/db/DbUserRepository.java  (예외 메시지 개선 부분만)
@Override
public String findByEmail(String email) {
    String sql = "SELECT id FROM Users WHERE email = ?";
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
        if (next != null && next.getMessage() != null) msg.append(" (").append(next.getMessage()).append(")");
        throw new RuntimeException(msg.toString(), e);
    }
}

    @Override
    public String create(String name, String email) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO Users(id,name,email) VALUES(?,?,?)";
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

    // ---------- extras used elsewhere ----------

    public List<String> findAllUsers() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name || ' <' || email || '>' FROM Users ORDER BY name";
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
        String sql = "UPDATE Users SET name=? WHERE id=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, norm(newName));
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updateUserName failed: " + userId, e);
        }
    }
    @Override
    public String authenticateByName(String name, String password) {
        String nm = norm(name).toLowerCase();
        String sql = "SELECT id, password_salt, password_hash FROM Users WHERE LOWER(name)=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nm);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String id   = rs.getString(1);
                String salt = rs.getString(2);
                String hash = rs.getString(3);
                if (salt == null || hash == null) return null; // legacy 계정 방지
                return PasswordUtil.verify(password, salt, hash) ? id : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("authenticateByName failed", e);
        }
    }
}