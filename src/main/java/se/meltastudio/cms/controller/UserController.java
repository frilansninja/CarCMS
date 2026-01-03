package se.meltastudio.cms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.dto.UserRegistrationRequest;
import se.meltastudio.cms.model.Role;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register/{customerId}")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest request, @PathVariable Long customerId) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> userService.findRoleByName(roleName)) // Hämta från databasen
                .filter(Optional::isPresent) // Ta bort ev. null-värden
                .map(Optional::get)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(userService.registerUser(user, customerId, roles));
    }


    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<se.meltastudio.cms.dto.UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(userService::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> updatedUser = userService.findById(id).map(existingUser -> {
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setRoles(userDetails.getRoles());
            return userService.save(existingUser);
        });

        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "roles", user.getRoles(),
                "companyId", user.getCompany() != null ? user.getCompany().getId() : null,
                "companyName", user.getCompany() != null ? user.getCompany().getName() : "Okänt"
        ));
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long userId, @RequestBody Map<String, Set<Role>> request, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Optional<User> loggedInUserOpt = userService.findByUsername(userDetails.getUsername());
        if (loggedInUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Logged in user not found");
        }
        User loggedInUser = loggedInUserOpt.get();

        Set<Role> newRoles = request.get("roles"); // 🔹 Hämta roller från request-body

        Optional<User> targetUser = userService.findById(userId);
        if (targetUser.isEmpty()) {
            return ResponseEntity.status(404).body("Användaren hittades inte.");
        }

        // 🔹 Kontrollera att admin och den aktuella användaren tillhör samma Customer
        if (!loggedInUser.getCompany().equals(targetUser.get().getCompany())) {
            return ResponseEntity.status(403).body("Du har inte behörighet att ändra roller för denna användare.");
        }

        // 🔹 Uppdatera roller om allt stämmer
        targetUser.get().setRoles(newRoles);
        userService.save(targetUser.get());

        return ResponseEntity.ok("Roller uppdaterade.");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ingen användare är inloggad.");
        }

        // 🔹 Hämta användaren från databasen baserat på `userDetails.getUsername()`
        User loggedInUser = userService.findByUsername(userDetails.getUsername())
                .orElse(null);

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Inloggad användare hittades inte i databasen.");
        }

        // 🔹 Kontrollera om användaren är SUPER_ADMIN
        boolean isSuperAdmin = loggedInUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("SUPER_ADMIN"));

        Long customerId;

        if (isSuperAdmin) {
            // 🔹 SUPER_ADMIN kan välja vilket företag användaren ska tillhöra
            if (request.getCompanyId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("SUPER_ADMIN måste ange ett företag.");
            }
            customerId = request.getCompanyId();
        } else {
            // 🔹 Vanliga admins skapar användare i sitt eget företag
            if (loggedInUser.getCompany() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Den inloggade användaren saknar en koppling till ett företag.");
            }
            customerId = loggedInUser.getCompany().getId();
        }

        System.out.println("✅ Skapar användare för Company ID: " + customerId);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> userService.findRoleByName(roleName)) // Hämta från databasen
                .filter(Optional::isPresent) // Ta bort ev. null-värden
                .map(Optional::get)
                .collect(Collectors.toSet());

        user.setRoles(roles);

        User newUser = userService.registerUser(user, customerId, roles);
        return ResponseEntity.ok(newUser);
    }
    @GetMapping("/mechanics")
    public ResponseEntity<?> getAllMechanics() {
        List<User> mechanics = userService.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equalsIgnoreCase("MECHANIC")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(mechanics);
    }






}
