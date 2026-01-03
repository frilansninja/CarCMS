package se.meltastudio.cms.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends User {
    private final Long companyId;

    public CustomUserDetails(UserDetails userDetails, Long companyId) {
        super(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return companyId;
    }
}
