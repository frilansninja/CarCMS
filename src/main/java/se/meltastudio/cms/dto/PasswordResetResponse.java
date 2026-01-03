package se.meltastudio.cms.dto;

public class PasswordResetResponse {
    private String resetLink;
    private String message;

    public PasswordResetResponse(String resetLink, String message) {
        this.resetLink = resetLink;
        this.message = message;
    }

    public String getResetLink() {
        return resetLink;
    }

    public void setResetLink(String resetLink) {
        this.resetLink = resetLink;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
