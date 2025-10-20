// path: src/Bookstore_gui/db/DbManager.java
package Bookstore_gui.db;

import java.sql.*;

public final class DbManager {
    private static final String URL  = "jdbc:derby:BookStoreDB;create=true";
    private static final String USER = "app";
    private static final String PW   = "app";

    private static Connection conn;
    private DbManager() {}

    public static synchronized Connection connect() throws SQLException {
        try { Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); } catch (ClassNotFoundException ignore) {}
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PW);
        }
        return conn;
    }

    public static synchronized void closeQuietly() {
        if (conn != null) { try { conn.close(); } catch (Exception ignore) {} conn = null; }
        try { DriverManager.getConnection("jdbc:derby:;shutdown=true"); } catch (SQLException ignore) {}
    }

    public static void initSchema() throws SQLException {
        connect();
        execIgnoreExists("""
          CREATE TABLE Users(
            id    VARCHAR(36)  PRIMARY KEY,
            name  VARCHAR(50)  NOT NULL,
            email VARCHAR(100) NOT NULL UNIQUE
          )
        """);
        execIgnoreExists("""
          CREATE TABLE BookProducts(
            id VARCHAR(10) PRIMARY KEY,
            title VARCHAR(200) NOT NULL,
            description VARCHAR(500),
            price DOUBLE NOT NULL,
            stock INT NOT NULL,
            author VARCHAR(100)
          )
        """);
        execIgnoreExists("""
          CREATE TABLE Orders(
            id VARCHAR(36) PRIMARY KEY,
            userId VARCHAR(36),
            created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
          )
        """);
        execIgnoreExists("""
          CREATE TABLE OrderItems(
            orderId VARCHAR(36),
            productId VARCHAR(10),
            title VARCHAR(200),
            quantity INT,
            price DOUBLE,
            PRIMARY KEY(orderId, productId)
          )
        """);

        ensureUserAuthColumns();
        ensureNameLowerInfrastructure();  
        try { SeedData.insertSampleBooks(); } catch (Exception ignore) {}
    }

    private static void execIgnoreExists(String ddl) throws SQLException {
        try (Statement st = connect().createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            if (!"X0Y32".equals(e.getSQLState())) throw e;
        }
    }

    private static void ensureUserAuthColumns() throws SQLException {
        if (!columnExists("USERS", "PASSWORD_SALT")) {
            try (Statement s = connect().createStatement()) {
                s.executeUpdate("ALTER TABLE Users ADD COLUMN password_salt VARCHAR(64)");
            }
        }
        if (!columnExists("USERS", "PASSWORD_HASH")) {
            try (Statement s = connect().createStatement()) {
                s.executeUpdate("ALTER TABLE Users ADD COLUMN password_hash VARCHAR(64)");
            }
        }
    }

    private static void ensureNameLowerInfrastructure() throws SQLException {
        
        if (!columnExists("USERS", "NAME_LOWER")) {
            try (Statement s = connect().createStatement()) {
                s.executeUpdate("ALTER TABLE Users ADD COLUMN name_lower VARCHAR(50)");
            }
            try (Statement s = connect().createStatement()) {
                s.executeUpdate("UPDATE Users SET name_lower = LOWER(name) WHERE name IS NOT NULL");
            }
        }

        // 2) BEFORE INSERT/UPDATE 
        execIgnoreExistsAny("""
          CREATE TRIGGER trg_users_name_bi
          NO CASCADE BEFORE INSERT ON Users
          REFERENCING NEW AS n
          FOR EACH ROW MODE DB2SQL
          SET n.name_lower = LOWER(n.name)
        """);

        execIgnoreExistsAny("""
          CREATE TRIGGER trg_users_name_bu
          NO CASCADE BEFORE UPDATE OF name ON Users
          REFERENCING NEW AS n
          FOR EACH ROW MODE DB2SQL
          SET n.name_lower = LOWER(n.name)
        """);

        // 3) UNIQUE 
        execIgnoreExistsAny("""
          CREATE UNIQUE INDEX ux_users_name_lower ON Users(name_lower)
        """);
    }

    private static void execIgnoreExistsAny(String ddl) throws SQLException {
        try (Statement st = connect().createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            String state = e.getSQLState();
            String msg = (e.getMessage() == null ? "" : e.getMessage()).toUpperCase();
            if ("X0Y32".equals(state) || "X0Y68".equals(state) || msg.contains("ALREADY EXISTS")) return;
            throw e;
        }
    }

    private static boolean columnExists(String tbl, String col) throws SQLException {
        String q = """
            SELECT 1 FROM SYS.SYSCOLUMNS c
              JOIN SYS.SYSTABLES t ON c.REFERENCEID = t.TABLEID
             WHERE UPPER(t.TABLENAME) = ? AND UPPER(c.COLUMNNAME) = ?
        """;
        try (PreparedStatement ps = connect().prepareStatement(q)) {
            ps.setString(1, tbl);
            ps.setString(2, col);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
}