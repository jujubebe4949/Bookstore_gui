// path: test/Bookstore_gui/tests/DbUserRepositoryTest.java
package Bookstore_gui.tests;

import Bookstore_gui.db.DbManager;
import Bookstore_gui.db.DbUserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;

/** Tests for DbUserRepository: idempotent findOrCreate and name update. */
public class DbUserRepositoryTest {

    private final DbUserRepository users = new DbUserRepository();
    private static final String EMAIL = "unittest@example.com";

    @Before
    public void setUp() throws Exception {
        DbManager.initSchema();
        deleteByEmail(EMAIL);
    }

    @After
    public void tearDown() throws Exception {
        deleteByEmail(EMAIL);
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
    public void updateUserNameChangesName() throws Exception {
        String uid = users.findOrCreate("Old Name", EMAIL);
        assertNotNull(uid);

        boolean ok = users.updateUserName(uid, "New Name");
        assertTrue(ok);

        // verify via direct SQL (why: repository may not expose read-by-id with name)
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement("SELECT name FROM Users WHERE id=?")) {
            ps.setString(1, uid);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("New Name", rs.getString(1));
            }
        }
    }

    @Test
    public void updateUserNameReturnsFalseForUnknownId() {
        boolean ok = users.updateUserName("non-existent-id", "Who Cares");
        assertFalse(ok);
    }

    private static void deleteByEmail(String email) throws Exception {
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Users WHERE email=?")) {
            ps.setString(1, email);
            ps.executeUpdate();
        }
    }
}