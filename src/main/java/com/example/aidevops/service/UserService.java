package com.example.aidevops.service;

import com.example.aidevops.model.User;
import com.example.aidevops.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getAllUsers(String sortBy, String sortDir) {
        if (sortBy == null || sortBy.isEmpty()) {
            return userRepository.findAll();
        }
        
        Sort.Direction direction = (sortDir != null && sortDir.equalsIgnoreCase("desc")) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        
        return userRepository.findAll(sort);
    }
    
    public Page<User> getUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }
    
    public List<User> searchUsers(String query) {
        // Simple search implementation - in real app you might use full-text search
        List<User> allUsers = userRepository.findAll();
        List<User> matchingUsers = new ArrayList<>();
        
        String lowerQuery = query.toLowerCase();
        for (User user : allUsers) {
            if (user.getName().toLowerCase().contains(lowerQuery) || 
                user.getEmail().toLowerCase().contains(lowerQuery)) {
                matchingUsers.add(user);
            }
        }
        
        return matchingUsers;
    }
    
    public long getUserCount() {
        return userRepository.count();
    }
    
    public User partialUpdateUser(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Update only the fields that are provided
        if (updates.containsKey("name")) {
            String name = (String) updates.get("name");
            if (name != null && !name.trim().isEmpty()) {
                user.setName(name.trim());
            }
        }
        
        if (updates.containsKey("email")) {
            String email = (String) updates.get("email");
            if (email != null && !email.trim().isEmpty()) {
                // Check if email already exists for a different user
                Optional<User> existingUser = userRepository.findByEmail(email);
                if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                    throw new RuntimeException("Email already exists for another user");
                }
                user.setEmail(email.trim());
            }
        }
        
        return userRepository.save(user);
    }
    
    public int deleteMultipleUsers(List<Long> userIds) {
        int deletedCount = 0;
        List<String> notFoundIds = new ArrayList<>();
        
        for (Long id : userIds) {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                userRepository.delete(user.get());
                deletedCount++;
            } else {
                notFoundIds.add(String.valueOf(id));
            }
        }
        
        if (!notFoundIds.isEmpty()) {
            throw new RuntimeException("Users not found with IDs: " + String.join(", ", notFoundIds));
        }
        
        return deletedCount;
    }
}