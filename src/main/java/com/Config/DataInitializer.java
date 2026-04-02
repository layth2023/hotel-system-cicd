package com.Config;

import com.Role.Role;
import com.Role.RoleRepository;
import com.User.User;
import com.User.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Data initializer that seeds default roles and admin user on application startup.
 * Disabled for test profile as tests manage their own test data.
 */
@Component
@DependsOn("entityManagerFactory")
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initRoles();
        initAdminUser();
    }

    private void initRoles() {
        createRoleIfNotExists("ROLE_USER", "Standard user role");
        createRoleIfNotExists("ROLE_ADMIN", "Administrator role with full access");
        createRoleIfNotExists("ROLE_MANAGER", "Manager role with limited admin access");
    }

    private void createRoleIfNotExists(String name, String description) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            roleRepository.save(role);
            log.info("Created role: {}", name);
        }
    }

    private void initAdminUser() {
        String adminUsername = "admin";

        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail("admin@hotel.com");
            admin.setPassword(passwordEncoder.encode("Admin@1234"));
            admin.setEnabled(true);
            admin.setAccountLocked(false);
            admin.setFailedLoginAttempts(0);

            // Assign all roles to admin
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName("ROLE_ADMIN").ifPresent(roles::add);
            roleRepository.findByName("ROLE_USER").ifPresent(roles::add);
            roleRepository.findByName("ROLE_MANAGER").ifPresent(roles::add);
            admin.setRoles(roles);

            userRepository.save(admin);
            log.info("Created admin user: {} / Admin@1234", adminUsername);
        }
    }
}
