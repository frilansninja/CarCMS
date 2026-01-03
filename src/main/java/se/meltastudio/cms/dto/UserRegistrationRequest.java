package se.meltastudio.cms.dto;

import se.meltastudio.cms.model.Role;
import java.util.Set;

public class UserRegistrationRequest {
    private String username;
    private String password;
    private Set<String> roles;
    private Long companyId; // Optional: only for SUPER_ADMIN

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
