package Bookstore_gui.db;

import java.sql.ResultSet;
import java.sql.Statement;

public class DbSmoke {
    public static void main(String[] args) throws Exception {
        DbManager.initSchema();
        try (Statement st = DbManager.connect().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM BookProducts")) {
            rs.next();
            System.out.println("Products count = " + rs.getInt(1));
        }
        DbManager.closeQuietly();
    }
}