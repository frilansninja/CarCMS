package se.meltastudio.cms.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se.meltastudio.cms.dto.CompanyDTO;
import se.meltastudio.cms.dto.UserDTO;
import se.meltastudio.cms.model.Company;
import se.meltastudio.cms.model.Role;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.repository.CompanyRepository;
import se.meltastudio.cms.repository.RoleRepository;
import se.meltastudio.cms.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, CompanyRepository companyRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
    }

    public User registerUser(User user, Long customerId, Set<Role> roles) {
        Optional<Company> customer = companyRepository.findById(customerId);
        if (customer.isPresent()) {
            user.setCompany(customer.get());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(roles); // 🔹 Spara flera roller
            return userRepository.save(user);
        } else {
            throw new RuntimeException("Customer not found");
        }
    }



    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setRoles(userDetails.getRoles()); // 🔹 Uppdatera roller korrekt
            return userRepository.save(existingUser);
        });
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Page<User> searchUsers(CompanyDTO company, String search, String role, Long workplaceId, Pageable pageable) {
        Role roleEntity = null;

        if (role != null && !role.isEmpty()) {
            Optional<Role> roleOptional = roleRepository.findByName(role);
            if (roleOptional.isPresent()) {
                roleEntity = roleOptional.get();
            } else {
                return Page.empty();
            }
        }

        // Hämta Company från databasen baserat på CompanyDTO
        Optional<Company> companyEntity = companyRepository.findById(company.getId());

        if (companyEntity.isEmpty()) {
            return Page.empty();
        }

        return userRepository.searchUsers(companyEntity.get(), search, roleEntity, workplaceId, pageable);
    }



    public Optional<Role> findRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Konvertera User entitet till UserDTO (inkluderar company- och workplace-information)
     */
    public UserDTO toDTO(User user) {
        UserDTO.CompanyBasicDTO companyDTO = null;
        if (user.getCompany() != null) {
            companyDTO = new UserDTO.CompanyBasicDTO(
                    user.getCompany().getId(),
                    user.getCompany().getName()
            );
        }

        UserDTO.WorkplaceBasicDTO workplaceDTO = null;
        if (user.getWorkplace() != null) {
            workplaceDTO = new UserDTO.WorkplaceBasicDTO(
                    user.getWorkplace().getId(),
                    user.getWorkplace().getName()
            );
        }

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRoles(),
                companyDTO,
                workplaceDTO
        );
    }
}
