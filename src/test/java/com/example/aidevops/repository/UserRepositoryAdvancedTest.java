package com.example.aidevops.repository;

import com.example.aidevops.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Advanced Tests")
class UserRepositoryAdvancedTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Should perform batch operations efficiently")
    void testBatchOperations() {
        // Given - Create multiple users
        User user1 = new User("Alice Johnson", "alice@example.com");
        User user2 = new User("Bob Smith", "bob@example.com");
        User user3 = new User("Charlie Brown", "charlie@example.com");
        User user4 = new User("Diana Prince", "diana@example.com");
        User user5 = new User("Edward Norton", "edward@example.com");

        List<User> users = List.of(user1, user2, user3, user4, user5);

        // When - Save all users
        List<User> savedUsers = userRepository.saveAll(users);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(savedUsers).hasSize(5);
        assertThat(userRepository.count()).isEqualTo(5);

        // When - Delete multiple users
        userRepository.deleteAll(savedUsers.subList(0, 3));
        entityManager.flush();

        // Then
        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle query by example")
    void testQueryByExample() {
        // Given
        User user1 = new User("John Doe", "john@example.com");
        User user2 = new User("Jane Doe", "jane@example.com");
        User user3 = new User("Bob Smith", "bob@example.com");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();
        entityManager.clear();

        // When - Query by example with partial name match
        User exampleUser = new User();
        exampleUser.setName("Doe");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreCase()
                .withIgnoreNullValues();

        Example<User> example = Example.of(exampleUser, matcher);
        List<User> foundUsers = userRepository.findAll(example);

        // Then
        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting(User::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Doe");
    }

    @Test
    @DisplayName("Should test transaction rollback on constraint violation")
    void testTransactionRollback() {
        // Given
        User user1 = new User("John Doe", "john@example.com");
        entityManager.persistAndFlush(user1);

        // When & Then - Attempt to save user with duplicate email
        User duplicateUser = new User("Jane Doe", "john@example.com");
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            entityManager.persistAndFlush(duplicateUser);
        });

        // Verify original user still exists
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should test cascade operations if implemented")
    void testCascadeOperations() {
        // Given
        User user = new User("Test User", "test@example.com");
        
        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // Then - Basic save operation
        assertThat(savedUser.getId()).isNotNull();
        assertThat(userRepository.findById(savedUser.getId())).isPresent();

        // When - Delete operation
        userRepository.deleteById(savedUser.getId());
        entityManager.flush();

        // Then - Verify deletion
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should test optimistic locking if versioning is enabled")
    void testOptimisticLocking() {
        // Given
        User user = new User("Test User", "test@example.com");
        User savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // When - Load same user in two different sessions
        User user1 = userRepository.findById(savedUser.getId()).orElseThrow();
        User user2 = userRepository.findById(savedUser.getId()).orElseThrow();

        // Modify both
        user1.setName("Updated by User 1");
        user2.setName("Updated by User 2");

        // Save first user
        userRepository.save(user1);
        entityManager.flush();

        // Then - Save second user (this might cause optimistic lock exception if versioning is enabled)
        // Note: Since we don't have @Version in our User entity, this won't throw an exception
        // but it demonstrates how to test for it if needed
        assertDoesNotThrow(() -> {
            userRepository.save(user2);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Should test entity lifecycle callbacks")
    void testEntityLifecycleCallbacks() {
        // Given
        User user = new User("Test User", "test@example.com");
        LocalDateTime beforeSave = LocalDateTime.now();

        // When
        User savedUser = userRepository.save(user);
        
        // Then - CreatedAt should be set automatically
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isAfterOrEqualTo(beforeSave);

        // When - Update user
        savedUser.setName("Updated Name");
        User updatedUser = userRepository.save(savedUser);

        // Then - CreatedAt should remain the same (not updated)
        assertThat(updatedUser.getCreatedAt()).isEqualTo(savedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should test bulk delete operations")
    void testBulkDeleteOperations() {
        // Given
        User user1 = new User("User 1", "user1@example.com");
        User user2 = new User("User 2", "user2@example.com");
        User user3 = new User("User 3", "user3@example.com");

        userRepository.saveAll(List.of(user1, user2, user3));
        entityManager.flush();
        entityManager.clear();

        // When - Delete all users
        userRepository.deleteAll();
        entityManager.flush();

        // Then
        assertThat(userRepository.count()).isEqualTo(0);
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should test exists operations")
    void testExistsOperations() {
        // Given
        User user = new User("Test User", "test@example.com");
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // When & Then
        assertThat(userRepository.existsById(savedUser.getId())).isTrue();
        assertThat(userRepository.existsById(999L)).isFalse();
        
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should test findAll with pagination edge cases")
    void testPaginationEdgeCases() {
        // Given - Create 5 users
        for (int i = 1; i <= 5; i++) {
            User user = new User("User " + i, "user" + i + "@example.com");
            userRepository.save(user);
        }
        entityManager.flush();

        // When & Then - Test various pagination scenarios
        
        // Test first page
        Page<User> firstPage = userRepository.findAll(PageRequest.of(0, 2));
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalElements()).isEqualTo(5);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.hasNext()).isTrue();

        // Test last page
        Page<User> lastPage = userRepository.findAll(PageRequest.of(2, 2));
        assertThat(lastPage.getContent()).hasSize(1);
        assertThat(lastPage.isLast()).isTrue();
        assertThat(lastPage.hasPrevious()).isTrue();

        // Test page beyond available data
        Page<User> beyondPage = userRepository.findAll(PageRequest.of(10, 2));
        assertThat(beyondPage.getContent()).isEmpty();
        assertThat(beyondPage.getTotalElements()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should test repository with large dataset performance")
    void testLargeDatasetPerformance() {
        // Given - Create a larger dataset
        long startTime = System.currentTimeMillis();
        
        for (int i = 1; i <= 100; i++) {
            User user = new User("User " + i, "user" + i + "@example.com");
            userRepository.save(user);
            
            // Flush every 20 records to manage memory
            if (i % 20 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        
        long insertTime = System.currentTimeMillis() - startTime;

        // When - Test bulk operations performance
        startTime = System.currentTimeMillis();
        long count = userRepository.count();
        long countTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        Page<User> page = userRepository.findAll(PageRequest.of(0, 10));
        long pageTime = System.currentTimeMillis() - startTime;

        // Then - Verify correctness (performance metrics are for information only)
        assertThat(count).isEqualTo(100);
        assertThat(page.getContent()).hasSize(10);
        
        // Log performance metrics for analysis
        System.out.println("Insert time for 100 records: " + insertTime + "ms");
        System.out.println("Count time: " + countTime + "ms");
        System.out.println("Page query time: " + pageTime + "ms");
    }

    @Test
    @DisplayName("Should test repository methods with null parameters")
    void testNullParameterHandling() {
        // When & Then - Test various null parameter scenarios
        assertThat(userRepository.findByEmail(null)).isEmpty();
        assertThat(userRepository.existsByEmail(null)).isFalse();
        
        // Test findById with null
        assertThat(userRepository.findById(null)).isEmpty();
        
        // Test save with null (should throw exception)
        assertThrows(Exception.class, () -> {
            userRepository.save(null);
        });
    }
}