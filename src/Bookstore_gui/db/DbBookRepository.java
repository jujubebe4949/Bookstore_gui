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

    @Override
    public List<BookProduct> findAll() {
        final String sql = "SELECT id,title,description,price,stock,author FROM BookProducts ORDER BY title";
        List<BookProduct> out = new ArrayList<>();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
    }

    public Optional<BookProduct> findById(String id) {
        final String sql = "SELECT id,title,description,price,stock,author FROM BookProducts WHERE id=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed: " + id, e);
        }
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
        } catch (SQLException e) {
            throw new RuntimeException("add failed: " + b.getId(), e);
        }
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
        } catch (SQLException e) {
            throw new RuntimeException("update failed: " + b.getId(), e);
        }
    }

    public void delete(String id) {
        final String sql = "DELETE FROM BookProducts WHERE id=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete failed: " + id, e);
        }
    }

    // ---- Search & stock (interface methods) ----

    /** Case-insensitive title substring search. */
    @Override
    public List<BookProduct> findByTitleLike(String q) {
        final String sql = """
            SELECT id,title,description,price,stock,author
              FROM BookProducts
             WHERE LOWER(title) LIKE LOWER(?)
             ORDER BY title
        """;
        List<BookProduct> out = new ArrayList<>();
        String key = "%" + (q == null ? "" : q.trim()) + "%";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("findByTitleLike failed: " + q, e);
        }
    }

    @Override
    public int getStock(String productId) {
        String sql = "SELECT stock FROM BookProducts WHERE id=?";
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0; // keep current behavior
            }
        } catch (SQLException e) {
            throw new RuntimeException("getStock failed: " + productId, e);
        }
    }

    /** Atomic stock delta; prevents negative stock. */
    @Override
    public int updateStockDelta(String productId, int delta) {
        final String sel = "SELECT stock FROM BookProducts WHERE id=? FOR UPDATE";
        final String upd = "UPDATE BookProducts SET stock = ? WHERE id = ?";
        try (Connection c = DbManager.connect()) {
            boolean oldAuto = c.getAutoCommit();
            c.setAutoCommit(false);
            try (PreparedStatement s1 = c.prepareStatement(sel);
                 PreparedStatement s2 = c.prepareStatement(upd)) {

                s1.setString(1, productId);
                int current;
                try (ResultSet rs = s1.executeQuery()) {
                    if (!rs.next()) {
                        throw new RuntimeException("book not found: " + productId);
                    }
                    current = rs.getInt(1);
                }

                int next = current + delta;
                if (next < 0) {
                    throw new IllegalStateException("stock would become negative (current=" + current + ", delta=" + delta + ")");
                }

                s2.setInt(1, next);
                s2.setString(2, productId);
                int rows = s2.executeUpdate();

                c.commit();
                return rows;
            } catch (Exception ex) {
                c.rollback();
                throw (ex instanceof RuntimeException) ? (RuntimeException) ex
                        : new RuntimeException("updateStockDelta failed: " + productId, ex);
            } finally {
                c.setAutoCommit(oldAuto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("updateStockDelta failed: " + productId, e);
        }
    }

    // ---- Compatibility wrappers (keep existing callers working) ----
    public List<BookProduct> searchBooksByTitle(String q) { return findByTitleLike(q); }
    public int changeStockBy(String id, int delta) { return updateStockDelta(id, delta); }

    // ---- Mapper ----
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