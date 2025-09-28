package Bookstore_gui.db;

import Bookstore_gui.repo.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DbUserRepository implements UserRepository {

    @Override
    public String findOrCreate(String name, String email) {
        email = email.trim().toLowerCase();
        name  = name.trim();

        // 1) 먼저 email로 조회
        String existing = findByEmail(email);
        if (existing != null) {
            return existing;
        }

        // 2) 없으면 새로 생성
        return create(name, email);
    }

    @Override
    public String findByEmail(String email) {
        String sql = "SELECT id FROM Users WHERE email = ?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByEmail failed", e);
        }
    }

    @Override
    public String create(String name, String email) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO Users(id,name,email) VALUES(?,?,?)";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, name.trim());
            ps.setString(3, email.trim().toLowerCase());
            ps.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("create user failed", e);
        }
    }

    // ---------------------- 보완 메소드 ----------------------

    /** 모든 사용자 조회 (read #2) */
    public List<String> findAllUsers() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name || ' <' || email || '>' FROM Users ORDER BY name";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAllUsers failed", e);
        }
    }

    /** 사용자 이름 변경 (write #2) */
    public boolean updateUserName(String userId, String newName) {
        String sql = "UPDATE Users SET name=? WHERE id=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newName.trim());
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updateUserName failed: " + userId, e);
        }
    }
}