package com.java.ipm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.java.ipm.domain.Role;
import com.java.ipm.entity.User;
import com.java.ipm.repository.UserRepository;

/**
 * Creates the initial ADMIN account on startup if it does not exist yet.
 * Admins cannot self-register, so this is how the first one is provisioned.
 */
@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;
    private final String email;

    public AdminAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                   @Value("${app.admin.username}") String username,
                                   @Value("${app.admin.password}") String password,
                                   @Value("${app.admin.email}") String email) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEmail(email);
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        log.info("Created initial admin account '{}'", username);
    }
}
