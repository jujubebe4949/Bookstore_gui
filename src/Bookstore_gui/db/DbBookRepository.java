// file: src/Bookstore_gui/db/DbBookRepository.java
package Bookstore_gui.db;

import Bookstore_gui.model.BookProduct;
import Bookstore_gui.repo.BookRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbBookRepository implements BookRepository {

    public DbBookRepository() {
        try { DbManager.initSchema(); } catch (Exception ignored) {}
    }

    // ---- 기본 CRUD ----
    public List<BookProduct> findAll() {
        final String sql = "SELECT id,title,description,price,stock,author FROM BookProducts ORDER BY title";
        List<BookProduct> out = new ArrayList<>();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) { throw new RuntimeException("findAll failed", e); }
    }

    public Optional<BookProduct> findById(String id) {
        final String sql = "SELECT id,title,description,price,stock,author FROM BookProducts WHERE id=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("findById failed: " + id, e); }
    }

    public void add(BookProduct b) {
        final String sql = """
            INSERT INTO BookProducts (id,title,description,price,stock,author)
            VALUES (?,?,?,?,?,?)
        """;
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getId());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getDescription());
            ps.setDouble(4, b.getPrice());
            ps.setInt(5, b.getStock());
            ps.setString(6, b.getAuthor());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("add failed: " + b.getId(), e); }
    }

    public void update(BookProduct b) {
        final String sql = """
            UPDATE BookProducts
               SET title=?, description=?, price=?, stock=?, author=?
             WHERE id=?
        """;
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getDescription());
            ps.setDouble(3, b.getPrice());
            ps.setInt(4, b.getStock());
            ps.setString(5, b.getAuthor());
            ps.setString(6, b.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("update failed: " + b.getId(), e); }
    }

    public void delete(String id) {
        final String sql = "DELETE FROM BookProducts WHERE id=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("delete failed: " + id, e); }
    }

    // ---- b) 추가 기능 ----

    /** 제목 부분검색(대소문자 무시). */
    public List<BookProduct> findByTitleLike(String q) {
        final String sql = """
            SELECT id,title,description,price,stock,author
              FROM BookProducts
             WHERE UPPER(title) LIKE UPPER(?)
             ORDER BY title
        """;
        List<BookProduct> out = new ArrayList<>();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + (q == null ? "" : q.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) { throw new RuntimeException("findByTitleLike failed: " + q, e); }
    }
 
    @Override
    public int getStock(String productId) {
        String sql = "SELECT stock FROM BookProducts WHERE id=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getStock failed: " + productId, e);
        }
    }
    
    @Override
    public int updateStockDelta(String productId, int delta) {
        String sql = "UPDATE BookProducts SET stock = stock + ? WHERE id = ?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setString(2, productId);
            return ps.executeUpdate(); // 0 또는 1
        } catch (SQLException e) {
            throw new RuntimeException("updateStockDelta failed: " + productId, e);
        }
    }

    // ---- 호환 래퍼 ----
    public List<BookProduct> searchBooksByTitle(String q) { return findByTitleLike(q); }
     public int changeStockBy(String id, int delta) {
        return updateStockDelta(id, delta);
    }


    // ---- 공용 매퍼 ----
    private static BookProduct map(ResultSet rs) throws SQLException {
        return new BookProduct(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getString("author")
        );
    }
}