package com.example.aidevops.integration;

import com.example.aidevops.AiDevSecOpsApplication;
import com.example.aidevops.model.User;
import com.example.aidevops.repository.UserRepository;
import com.example.aidevops.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
    classes = AiDevSecOpsApplication.class,
    properties = "spring.profiles.active=test",
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@Transactional
@DisplayName("User Integration Tests - Service and Repository Layers")
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should test complete user lifecycle via service layer")
    void testCompleteUserLifecycle() {
        // 1. Create user
        User newUser = new User("Integration Test User", "integration@example.com");
        User createdUser = userService.createUser(newUser);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("Integration Test User");
        assertThat(createdUser.getEmail()).isEqualTo("integration@example.com");
        assertThat(createdUser.getCreatedAt()).isNotNull();

        // 2. Verify user exists in repository
        Optional<User> foundUser = userRepository.findById(createdUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Integration Test User");

        // 3. Get user by ID via service
        Optional<User> serviceFoundUser = userService.getUserById(createdUser.getId());
        assertThat(serviceFoundUser).isPresent();
        assertThat(serviceFoundUser.get().getName()).isEqualTo("Integration Test User");

        // 4. Get user by email via service
        Optional<User> emailFoundUser = userService.getUserByEmail("integration@example.com");
        assertThat(emailFoundUser).isPresent();
        assertThat(emailFoundUser.get().getId()).isEqualTo(createdUser.getId());

        // 5. Update user
        User updatedUser = userService.updateUser(createdUser.getId(), 
                new User("Updated Integration User", "updated.integration@example.com"));
        assertThat(updatedUser.getName()).isEqualTo("Updated Integration User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated.integration@example.com");

        // 6. Verify update in repository
        Optional<User> repoUpdatedUser = userRepository.findById(createdUser.getId());
        assertThat(repoUpdatedUser).isPresent();
        assertThat(repoUpdatedUser.get().getName()).isEqualTo("Updated Integration User");

        // 7. Partial update
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Partially Updated User");
        User partiallyUpdated = userService.partialUpdateUser(createdUser.getId(), updates);
        assertThat(partiallyUpdated.getName()).isEqualTo("Partially Updated User");
        assertThat(partiallyUpdated.getEmail()).isEqualTo("updated.integration@example.com"); // Should remain unchanged

        // 8. Search functionality
        List<User> searchResults = userService.searchUsers("Partially");
        assertThat(searchResults).hasSize(1);
        assertThat(searchResults.get(0).getName()).isEqualTo("Partially Updated User");

        // 9. Count users
        long userCount = userService.getUserCount();
        assertThat(userCount).isEqualTo(1);

        // 10. Delete user
        userService.deleteUser(createdUser.getId());

        // 11. Verify deletion
        Optional<User> deletedUser = userRepository.findById(createdUser.getId());
        assertThat(deletedUser).isEmpty();
        assertThat(userService.getUserCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should test bulk operations integration")
    void testBulkOperationsIntegration() {
        // Create multiple users
        User user1 = userService.createUser(new User("User 1", "user1@example.com"));
        User user2 = userService.createUser(new User("User 2", "user2@example.com"));
        User user3 = userService.createUser(new User("User 3", "user3@example.com"));

        // Verify all users exist
        assertThat(userService.getUserCount()).isEqualTo(3);

        // Test search across multiple users
        List<User> allUsers = userService.getAllUsers();
        assertThat(allUsers).hasSize(3);

        // Test search with query
        List<User> searchResults = userService.searchUsers("User");
        assertThat(searchResults).hasSize(3);

        // Test bulk delete
        List<Long> idsToDelete = List.of(user1.getId(), user2.getId());
        int deletedCount = userService.deleteMultipleUsers(idsToDelete);
        assertThat(deletedCount).isEqualTo(2);

        // Verify remaining user
        assertThat(userService.getUserCount()).isEqualTo(1);
        Optional<User> remainingUser = userService.getUserById(user3.getId());
        assertThat(remainingUser).isPresent();
        assertThat(remainingUser.get().getName()).isEqualTo("User 3");
    }

    @Test
    @DisplayName("Should test pagination integration")
    void testPaginationIntegration() {
        // Create multiple users
        for (int i = 1; i <= 10; i++) {
            userService.createUser(new User("User " + i, "user" + i + "@example.com"));
        }

        // Test pagination
        var pageable = org.springframework.data.domain.PageRequest.of(0, 3);
        var firstPage = userService.getUsersPaginated(pageable);
        
        assertThat(firstPage.getContent()).hasSize(3);
        assertThat(firstPage.getTotalElements()).isEqualTo(10);
        assertThat(firstPage.getTotalPages()).isEqualTo(4);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.hasNext()).isTrue();

        // Test second page
        var secondPageable = org.springframework.data.domain.PageRequest.of(1, 3);
        var secondPage = userService.getUsersPaginated(secondPageable);
        
        assertThat(secondPage.getContent()).hasSize(3);
        assertThat(secondPage.isFirst()).isFalse();
        assertThat(secondPage.hasNext()).isTrue();

        // Test last page
        var lastPageable = org.springframework.data.domain.PageRequest.of(3, 3);
        var lastPage = userService.getUsersPaginated(lastPageable);
        
        assertThat(lastPage.getContent()).hasSize(1);
        assertThat(lastPage.isLast()).isTrue();
        assertThat(lastPage.hasPrevious()).isTrue();
    }

    @Test
    @DisplayName("Should test sorting integration")
    void testSortingIntegration() {
        // Create users with different names
        userService.createUser(new User("Charlie", "charlie@example.com"));
        userService.createUser(new User("Alice", "alice@example.com"));
        userService.createUser(new User("Bob", "bob@example.com"));

        // Test ascending sort
        List<User> ascUsers = userService.getAllUsers("name", "asc");
        assertThat(ascUsers).hasSize(3);
        assertThat(ascUsers.get(0).getName()).isEqualTo("Alice");
        assertThat(ascUsers.get(1).getName()).isEqualTo("Bob");
        assertThat(ascUsers.get(2).getName()).isEqualTo("Charlie");

        // Test descending sort
        List<User> descUsers = userService.getAllUsers("name", "desc");
        assertThat(descUsers).hasSize(3);
        assertThat(descUsers.get(0).getName()).isEqualTo("Charlie");
        assertThat(descUsers.get(1).getName()).isEqualTo("Bob");
        assertThat(descUsers.get(2).getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Should test error handling integration")
    void testErrorHandlingIntegration() {
        // Test duplicate email creation
        User user1 = userService.createUser(new User("User 1", "duplicate@example.com"));
        
        assertThatThrownBy(() -> userService.createUser(new User("User 2", "duplicate@example.com")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");

        // Test user not found scenarios
        assertThatThrownBy(() -> userService.updateUser(999L, new User("Non-existent", "nonexistent@example.com")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        // Test partial update with duplicate email
        User user2 = userService.createUser(new User("User 2", "user2@example.com"));
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "duplicate@example.com");
        
        assertThatThrownBy(() -> userService.partialUpdateUser(user2.getId(), updates))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");

        // Test bulk delete with non-existent user
        assertThatThrownBy(() -> userService.deleteMultipleUsers(List.of(user1.getId(), 999L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should test repository integration directly")
    void testRepositoryIntegration() {
        // Test repository methods directly
        User user = new User("Repository Test", "repo@example.com");
        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();

        // Test custom repository methods
        Optional<User> foundByEmail = userRepository.findByEmail("repo@example.com");
        assertThat(foundByEmail).isPresent();

        boolean exists = userRepository.existsByEmail("repo@example.com");
        assertThat(exists).isTrue();

        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(notExists).isFalse();

        // Verify count
        long count = userRepository.count();
        assertThat(count).isEqualTo(1);

        // Test deletion
        userRepository.delete(savedUser);
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should test application context loads correctly")
    void testApplicationContextLoads() {
        // This test verifies that the Spring application context loads successfully
        assertThat(userRepository).isNotNull();
        assertThat(userService).isNotNull();
        
        // Test that autowiring worked correctly
        assertThat(userService.getUserCount()).isEqualTo(0); // Should start with empty database
    }

    @Test
    @DisplayName("Should test transaction rollback behavior")
    void testTransactionRollback() {
        // Create a user that should be rolled back
        User user = userService.createUser(new User("Transaction Test", "transaction@example.com"));
        assertThat(user.getId()).isNotNull();
        
        // Verify user exists within transaction
        assertThat(userService.getUserCount()).isEqualTo(1);
        
        // The @Transactional annotation on the class should roll back after each test
        // This is verified by other tests starting with empty database
    }
}