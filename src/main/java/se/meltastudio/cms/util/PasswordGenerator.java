package se.meltastudio.cms.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (args.length == 0) {
            System.out.println("Usage: java PasswordGenerator <password>");
            System.out.println("\nExample passwords:");
            System.out.println("admin -> " + encoder.encode("admin"));
            System.out.println("password123 -> " + encoder.encode("password123"));
            return;
        }

        String plainPassword = args[0];
        String hashedPassword = encoder.encode(plainPassword);

        System.out.println("\n===========================================");
        System.out.println("Plain password: " + plainPassword);
        System.out.println("BCrypt hash: " + hashedPassword);
        System.out.println("===========================================\n");
        System.out.println("SQL to update user password:");
        System.out.println("UPDATE users SET password = '" + hashedPassword + "' WHERE username = 'admin';");
        System.out.println("===========================================\n");
    }
}
