package com.zone01oujda.moblogging.admin.service;

import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.user.repository.UserRepository;

/**
 * Service class for admin operations
 */
@Service
public class AdminService {
    
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get dashboard statistics
     * @return dashboard data
     */
    public java.util.Map<String, Object> getDashboardStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("timestamp", java.time.LocalDateTime.now());
        return stats;
    }

    /**
     * Ban a user by ID
     * @param userId the user ID to ban
     * @throws ResourceNotFoundException if user not found
     */
    public void banUser(Long userId) {
        com.zone01oujda.moblogging.entity.User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setBanned(true);
        userRepository.save(user);
    }

    /**
     * Unban a user by ID
     * @param userId the user ID to unban
     * @throws ResourceNotFoundException if user not found
     */
    public void unbanUser(Long userId) {
        com.zone01oujda.moblogging.entity.User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setBanned(false);
        userRepository.save(user);
    }
}
