package com.zone01oujda.moblogging.config;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.user.enums.Gender;
import com.zone01oujda.moblogging.user.enums.ProfileType;
import com.zone01oujda.moblogging.user.enums.Role;
import com.zone01oujda.moblogging.user.repository.UserRepository;

@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminSeeder.class);

    private final Environment env;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(Environment env, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.env = env;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean enabled = Boolean.parseBoolean(env.getProperty("admin.seed.enabled", "true"));
        if (!enabled) {
            return;
        }

        String username = trim(env.getProperty("admin.seed.username", env.getProperty("ADMIN_USERNAME")));
        String email = trim(env.getProperty("admin.seed.email", env.getProperty("ADMIN_EMAIL")));
        String password = trim(env.getProperty("admin.seed.password", env.getProperty("ADMIN_PASSWORD")));

        if (isBlank(username) || isBlank(email) || isBlank(password)) {
            logger.warn("Admin seeding skipped: missing admin.seed.username/email/password (or ADMIN_* env vars).");
            return;
        }

        User existing = userRepository.findByUsernameOrEmail(username)
            .or(() -> userRepository.findByUsernameOrEmail(email))
            .orElse(null);

        if (existing != null) {
            if (existing.getRole() != Role.ADMIN) {
                existing.setRole(Role.ADMIN);
                userRepository.save(existing);
                logger.info("Admin role granted to existing user: {}", existing.getUsername());
            } else {
                logger.info("Admin user already exists: {}", existing.getUsername());
            }
            return;
        }

        String firstName = trim(env.getProperty("admin.seed.first-name", env.getProperty("ADMIN_FIRST_NAME", "Admin")));
        String lastName = trim(env.getProperty("admin.seed.last-name", env.getProperty("ADMIN_LAST_NAME", "User")));
        Gender gender = parseGender(env.getProperty("admin.seed.gender", env.getProperty("ADMIN_GENDER", "PREFER_NOT_TO_SAY")));
        ProfileType profileType = parseProfileType(env.getProperty("admin.seed.profile-type", env.getProperty("ADMIN_PROFILE_TYPE", "PUBLIC")));

        User admin = new User(username, email, passwordEncoder.encode(password));
        admin.setFirstName(Objects.requireNonNullElse(firstName, "Admin"));
        admin.setLastName(Objects.requireNonNullElse(lastName, "User"));
        admin.setGender(gender);
        admin.setProfileType(profileType);
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
        logger.info("Admin user created: {}", username);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private Gender parseGender(String value) {
        try {
            return Gender.valueOf(value);
        } catch (Exception e) {
            return Gender.PREFER_NOT_TO_SAY;
        }
    }

    private ProfileType parseProfileType(String value) {
        try {
            return ProfileType.valueOf(value);
        } catch (Exception e) {
            return ProfileType.PUBLIC;
        }
    }
}
