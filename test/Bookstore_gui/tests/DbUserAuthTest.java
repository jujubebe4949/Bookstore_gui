// path: test/Bookstore_gui/tests/DbUserAuthTest.java
package Bookstore_gui.tests;

import Bookstore_gui.db.DbManager;
import Bookstore_gui.db.DbUserRepository;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests user authentication (register & login) with Derby database. */
public class DbUserAuthTest extends TestBootstrap {

    private final DbUserRepository users = new DbUserRepository();
    private static final String EMAIL = "authcase@example.com";
    private static final String NAME  = "AuthUser";
    private static final String PASS  = "p@ssw0rd";

    @Before
    public void setup() throws Exception {
        DbManager.initSchema();
        // Remove test user if already exists
        try (var c = DbManager.connect();
             var ps = c.prepareStatement("DELETE FROM Users WHERE email=?")) {
            ps.setString(1, EMAIL.toLowerCase());
            ps.executeUpdate();
        }
        // Register new test user (password uses salt+hash)
        users.register(NAME, EMAIL, PASS);
    }

    @Test
    public void authenticate_success_withCorrectPassword() {
        String id = users.authenticate(EMAIL, PASS);
        assertNotNull(id); // should succeed
    }

    @Test
    public void authenticate_fail_withWrongPassword() {
        String id = users.authenticate(EMAIL, "WRONG");
        assertNull(id); // should fail
    }
}