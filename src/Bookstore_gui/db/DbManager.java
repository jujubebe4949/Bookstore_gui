// path: src/Bookstore_gui/db/DBManager.java
package Bookstore_gui.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbManager {
    private static final String URL  = "jdbc:derby:BookStoreDB;create=true"; // Embedded
    private static final String USER = "app";
    private static final String PW   = "app";

    private static Connection conn;

    private DbManager() {}

    public static synchronized Connection connect() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PW);
        }
        return conn;
    }

    public static synchronized void closeQuietly() {
        if (conn != null) {
            try { conn.close(); } catch (Exception ignored) {}
            conn = null;
        }
    }

    public static void initSchema() throws SQLException {
        connect();

        // users (옵션: 나중에 orders/reviews 사용 대비)
        execIgnoreExists("""
            CREATE TABLE users(
              id    VARCHAR(36) PRIMARY KEY,
              name  VARCHAR(50)  NOT NULL,
              email VARCHAR(100) NOT NULL UNIQUE
            )
        """);

        // 핵심: BookProducts (과제의 BookProduct 모델 대응)
        execIgnoreExists("""
            CREATE TABLE BookProducts(
              id          VARCHAR(10)  PRIMARY KEY,
              title       VARCHAR(200) NOT NULL,
              description VARCHAR(500),
              price       DOUBLE       NOT NULL,
              stock       INT          NOT NULL,
              author      VARCHAR(100)
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
            orderId   VARCHAR(36),
            productId VARCHAR(10),
            title     VARCHAR(200),   -- 🆕 제목도 저장 (뷰가 직접 출력)
            quantity  INT,
            price     DOUBLE,
            PRIMARY KEY(orderId, productId)
        )
        """);

        // (필요 시) reviews / orders 등은 추후 확장 가능
        // execIgnoreExists("CREATE TABLE reviews(...)");
        // execIgnoreExists("CREATE TABLE orders(...)");

        // 샘플 20권 주입 (중복 시 건너뜀)
        try { SeedData.insertSampleBooks(); } catch (Exception e) { e.printStackTrace(); }
    }

    // Derby에는 CREATE TABLE IF NOT EXISTS가 없어, '이미 존재(X0Y32)'만 무시
    private static void execIgnoreExists(String ddl) throws SQLException {
        try (Statement st = connect().createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            if (!"X0Y32".equals(e.getSQLState())) throw e;
        }
    }
}