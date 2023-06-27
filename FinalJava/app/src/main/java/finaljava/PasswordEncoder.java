package finaljava;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordEncoder {
    public static String encoded(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashingPasswordBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashingPasswordBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
