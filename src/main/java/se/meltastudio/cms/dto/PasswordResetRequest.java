package se.meltastudio.cms.dto;

public class PasswordResetRequest {
    private String username; // This is the email

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
