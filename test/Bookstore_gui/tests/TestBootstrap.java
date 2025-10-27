package Bookstore_gui.tests;

import Bookstore_gui.db.DbManager;
import org.junit.BeforeClass;
import java.nio.file.*;

/** Initializes a clean Derby test database before running JUnit tests. */
public class TestBootstrap {

    @BeforeClass
    public static void boot() throws Exception {
        System.setProperty("java.awt.headless", "true"); // Disable GUI popups

        Path home = Paths.get("build/test-derby-home");
        if (Files.exists(home)) {
            Files.walk(home)
                 .sorted((a, b) -> b.compareTo(a))
                 .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignore) {} });
        }
        Files.createDirectories(home);

        System.setProperty("derby.system.home", home.toAbsolutePath().toString());
        DbManager.initSchema(); // Initialize schema
    }
}