package Bookstore_gui.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SeedData {

    public static void insertSampleBooks() throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM BookProducts WHERE id = ?";
        String insertSql = """
            INSERT INTO BookProducts (id, title, description, price, stock, author)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DbManager.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            addBook(conn, checkStmt, insertStmt, "B001", "Crying In H Mart",
                    "Memoir of family, food, and identity.", 25, 5, "Michelle Zauner");

            addBook(conn, checkStmt, insertStmt, "B002", "Project Hail Mary",
                    "A lone astronaut on a desperate mission.", 24, 5, "Andy Weir");

            addBook(conn, checkStmt, insertStmt, "B003", "The Ministry For The Future",
                    "Near-future climate survival story.", 28, 5, "Kim Stanley Robinson");

            addBook(conn, checkStmt, insertStmt, "B004", "Two Places To Call Home",
                    "Picture book about two loving homes.", 21, 5, "Phil Earle");

            addBook(conn, checkStmt, insertStmt, "B005", "He Puka Ngohe",
                    "Māori activity workbook for learners.", 27, 5, "Katherine Q. Merewether & Pania Papa");

            addBook(conn, checkStmt, insertStmt, "B006", "Before George",
                    "A girl rebuilds identity after tragedy.", 28, 5, "Deborah Robertson");

            addBook(conn, checkStmt, insertStmt, "B007", "How To Loiter In A Turf War",
                    "Sharp, funny Auckland urban tale.", 28, 5, "Jessica (Coco Solid) Hansell");

            addBook(conn, checkStmt, insertStmt, "B008", "Verity",
                    "Dark, addictive psychological thriller.", 28, 5, "Colleen Hoover");

            addBook(conn, checkStmt, insertStmt, "B009", "The Dispossessed",
                    "Classic twin-world utopia/anarchism.", 25, 5, "Ursula K. Le Guin");

            addBook(conn, checkStmt, insertStmt, "B010", "The Bullet That Missed",
                    "Thursday Murder Club #3 mystery.", 26, 5, "Richard Osman");

            addBook(conn, checkStmt, insertStmt, "B011", "Don Binney: Flight Path",
                    "Illustrated study of iconic NZ artist Don Binney.", 90, 3, "Gregory O'Brien");

            addBook(conn, checkStmt, insertStmt, "B012", "Art And Court Of James VI and I",
                    "Art and objects of a dynamic royal court.", 80, 4, "Kate Anderson et al.");

            addBook(conn, checkStmt, insertStmt, "B013", "Reasons Not To Worry",
                    "Practical, modern Stoicism guide.", 33, 5, "Brigid Delaney");

            addBook(conn, checkStmt, insertStmt, "B014", "Exercised",
                    "Science of activity, rest, and health.", 26, 5, "Daniel Lieberman");

            addBook(conn, checkStmt, insertStmt, "B015", "Recipetin Eats: Dinner",
                    "150 fail-proof dinner recipes.", 50, 5, "Nagi Maehashi");

            addBook(conn, checkStmt, insertStmt, "B016", "Vegful",
                    "Vegetable-forward cookbook for everyone.", 55, 5, "Nadia Lim");

            addBook(conn, checkStmt, insertStmt, "B017", "Easy Wins",
                    "12 flavour hits, 125 recipes.", 60, 5, "Anna Jones");

            addBook(conn, checkStmt, insertStmt, "B018", "Pachinko",
                    "Epic Korean-Japanese family saga.", 25, 5, "Min Jin Lee");

            addBook(conn, checkStmt, insertStmt, "B019", "Stone Yard Devotional",
                    "Moving novel of grief and forgiveness.", 38, 5, "Charlotte Wood");

            addBook(conn, checkStmt, insertStmt, "B020", "Beartown",
                    "A hockey town tested by crisis.", 26, 5, "Fredrik Backman");
        }
    }

    private static void addBook(Connection conn,
                                PreparedStatement checkStmt,
                                PreparedStatement insertStmt,
                                String id, String title, String desc,
                                double price, int stock, String author) throws SQLException {
        checkStmt.setString(1, id);
        try (ResultSet rs = checkStmt.executeQuery()) {
            rs.next();
            if (rs.getInt(1) == 0) { 
                insertStmt.setString(1, id);
                insertStmt.setString(2, title);
                insertStmt.setString(3, desc);
                insertStmt.setDouble(4, price);
                insertStmt.setInt(5, stock);
                insertStmt.setString(6, author);
                insertStmt.executeUpdate();
            }
        }
    }
}