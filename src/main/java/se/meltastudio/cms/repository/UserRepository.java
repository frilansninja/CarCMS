package se.meltastudio.cms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.meltastudio.cms.dto.CompanyDTO;
import se.meltastudio.cms.model.Company;
import se.meltastudio.cms.model.Role;
import se.meltastudio.cms.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 🔹 Hämta en användare med alla roller (säkerställer att roller alltid laddas)
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    // 🔹 Hämta alla användare för en Customer (med paginering)
    Page<User> findByCompany(Company company, Pageable pageable);

    // 🔹 Hämta användare för en Customer och filtrera på roll (med paginering)
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE u.company = :company AND r = :role")
    Page<User> findByCompanyAndRoles(@Param("company") Company company, @Param("role") Role role, Pageable pageable);

    // 🔹 Kombinerad metod för att söka och filtrera efter roll och workplace
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.roles r WHERE u.company = :company " +
            "AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) " +
            "AND (:role IS NULL OR r = :role) " +
            "AND (:workplaceId IS NULL OR u.workplace.id = :workplaceId)")
    Page<User> searchUsers(@Param("company") Company company,
                           @Param("username") String username,
                           @Param("role") Role role,
                           @Param("workplaceId") Long workplaceId,
                           Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND :role MEMBER OF u.roles")
    List<User> findByCompanyIdAndRole(@Param("companyId") Long companyId, @Param("role") Role role);


}
