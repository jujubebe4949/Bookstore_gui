
package Bookstore_gui.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class PasswordUtil {
    private static final SecureRandom RND = new SecureRandom();
    private PasswordUtil(){}

    public static String newSaltHex() {
        byte[] b = new byte[16];
        RND.nextBytes(b);
        return HexFormat.of().formatHex(b);
    }

    public static String hashHex(String password, String saltHex) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(HexFormat.of().parseHex(saltHex));
            byte[] out = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(out);
        } catch (Exception e) { throw new RuntimeException("hash failed", e); }
    }

    public static boolean verify(String password, String saltHex, String hashHex) {
        return hashHex(password, saltHex).equalsIgnoreCase(hashHex);
    }
}