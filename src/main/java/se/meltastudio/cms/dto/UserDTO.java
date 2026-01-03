package se.meltastudio.cms.dto;

import se.meltastudio.cms.model.Role;

import java.util.Set;

public class UserDTO {
    private Long id;
    private String username;
    private Set<Role> roles;
    private CompanyBasicDTO company;
    private WorkplaceBasicDTO workplace;

    public UserDTO() {
    }

    public UserDTO(Long id, String username, Set<Role> roles, CompanyBasicDTO company, WorkplaceBasicDTO workplace) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.company = company;
        this.workplace = workplace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public CompanyBasicDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyBasicDTO company) {
        this.company = company;
    }

    public WorkplaceBasicDTO getWorkplace() {
        return workplace;
    }

    public void setWorkplace(WorkplaceBasicDTO workplace) {
        this.workplace = workplace;
    }

    // Enkel DTO för company-information (för att undvika cirkulära referenser)
    public static class CompanyBasicDTO {
        private Long id;
        private String name;

        public CompanyBasicDTO() {
        }

        public CompanyBasicDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Enkel DTO för workplace-information (för att undvika cirkulära referenser)
    public static class WorkplaceBasicDTO {
        private Long id;
        private String name;

        public WorkplaceBasicDTO() {
        }

        public WorkplaceBasicDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
