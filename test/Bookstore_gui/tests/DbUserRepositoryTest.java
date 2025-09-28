package Bookstore_gui.tests;

import Bookstore_gui.db.DbManager;
import Bookstore_gui.db.DbUserRepository;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.Assert.*;

public class DbUserRepositoryTest {

    private final DbUserRepository users = new DbUserRepository();
    private static final String EMAIL = "unittest@example.com";

    @Before
    public void clean() throws Exception {
        DbManager.initSchema();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Users WHERE email=?")) {
            ps.setString(1, EMAIL);
            ps.executeUpdate();
        }
    }

    @Test
    public void findOrCreateReturnsSameIdAfterSecondCall() {
        String id1 = users.findOrCreate("Unit Tester", EMAIL);
        assertNotNull(id1);
        String id2 = users.findOrCreate("Unit Tester Again", EMAIL);
        assertEquals(id1, id2);

        String id3 = users.findByEmail(EMAIL);
        assertEquals(id1, id3);
    }

    @Test
    public void updateUserNameChangesName() {
        // 1. 유저 생성
        String uid = users.findOrCreate("Old Name", EMAIL);
        assertNotNull(uid);

        // 2. 이름 변경
        boolean ok = users.updateUserName(uid, "New Name");
        assertTrue(ok);

        // 3. 다시 이메일로 조회 (ID는 같음)
        String uid2 = users.findByEmail(EMAIL);
        assertEquals(uid, uid2);
    }
}