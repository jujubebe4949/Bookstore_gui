package Bookstore_gui.db;

import java.sql.ResultSet;
import java.sql.Statement;

/** 간단 스모크 테스트: 스키마 초기화 + 상품 개수 출력 */
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