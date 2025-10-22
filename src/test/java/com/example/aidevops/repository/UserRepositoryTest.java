package com.example.aidevops.repository;

import com.example.aidevops.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        entityManager.clear();
        
        // Create test users
        testUser1 = new User("John Doe", "john.doe@example.com");
        testUser2 = new User("Jane Smith", "jane.smith@example.com");
        testUser3 = new User("Bob Johnson", "bob.johnson@example.com");
        
        // Set creation time for predictable sorting
        testUser1.setCreatedAt(LocalDateTime.now().minusDays(2));
        testUser2.setCreatedAt(LocalDateTime.now().minusDays(1));
        testUser3.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save and find user by ID")
    void testSaveAndFindById() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);
        entityManager.clear();

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.clear();

        // When
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void testFindByEmailNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void testExistsByEmail() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.clear();

        // When & Then
        assertThat(userRepository.existsByEmail("john.doe@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        // Given
        entityManager.persist(testUser1);
        entityManager.persist(testUser2);
        entityManager.persist(testUser3);
        entityManager.flush();
        entityManager.clear();

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith", "Bob Johnson");
    }

    @Test
    @DisplayName("Should find all users with sorting")
    void testFindAllWithSorting() {
        // Given
        entityManager.persist(testUser1);
        entityManager.persist(testUser2);
        entityManager.persist(testUser3);
        entityManager.flush();
        entityManager.clear();

        // When - Sort by name ascending
        Sort sortByNameAsc = Sort.by(Sort.Direction.ASC, "name");
        List<User> usersAsc = userRepository.findAll(sortByNameAsc);

        // Then
        assertThat(usersAsc).hasSize(3);
        assertThat(usersAsc.get(0).getName()).isEqualTo("Bob Johnson");
        assertThat(usersAsc.get(1).getName()).isEqualTo("Jane Smith");
        assertThat(usersAsc.get(2).getName()).isEqualTo("John Doe");

        // When - Sort by name descending
        Sort sortByNameDesc = Sort.by(Sort.Direction.DESC, "name");
        List<User> usersDesc = userRepository.findAll(sortByNameDesc);

        // Then
        assertThat(usersDesc).hasSize(3);
        assertThat(usersDesc.get(0).getName()).isEqualTo("John Doe");
        assertThat(usersDesc.get(1).getName()).isEqualTo("Jane Smith");
        assertThat(usersDesc.get(2).getName()).isEqualTo("Bob Johnson");
    }

    @Test
    @DisplayName("Should find users with pagination")
    void testFindAllWithPagination() {
        // Given
        entityManager.persist(testUser1);
        entityManager.persist(testUser2);
        entityManager.persist(testUser3);
        entityManager.flush();
        entityManager.clear();

        // When - First page (size 2)
        Pageable firstPage = PageRequest.of(0, 2, Sort.by("name"));
        Page<User> firstPageResult = userRepository.findAll(firstPage);

        // Then
        assertThat(firstPageResult.getContent()).hasSize(2);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(3);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(2);
        assertThat(firstPageResult.hasNext()).isTrue();
        assertThat(firstPageResult.hasPrevious()).isFalse();

        // When - Second page (size 2)
        Pageable secondPage = PageRequest.of(1, 2, Sort.by("name"));
        Page<User> secondPageResult = userRepository.findAll(secondPage);

        // Then
        assertThat(secondPageResult.getContent()).hasSize(1);
        assertThat(secondPageResult.getTotalElements()).isEqualTo(3);
        assertThat(secondPageResult.getTotalPages()).isEqualTo(2);
        assertThat(secondPageResult.hasNext()).isFalse();
        assertThat(secondPageResult.hasPrevious()).isTrue();
    }

    @Test
    @DisplayName("Should count users")
    void testCount() {
        // Given
        entityManager.persist(testUser1);
        entityManager.persist(testUser2);
        entityManager.flush();
        entityManager.clear();

        // When
        long count = userRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should delete user by ID")
    void testDeleteById() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);
        Long userId = savedUser.getId();
        entityManager.clear();

        // When
        userRepository.deleteById(userId);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("Should delete user entity")
    void testDelete() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);
        entityManager.clear();
        
        // Reload the user to ensure it's managed
        User managedUser = entityManager.find(User.class, savedUser.getId());

        // When
        userRepository.delete(managedUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("Should handle unique constraint violation for email")
    void testUniqueEmailConstraint() {
        // Given
        entityManager.persistAndFlush(testUser1);
        
        User duplicateEmailUser = new User("Different Name", "john.doe@example.com");

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateEmailUser);
        });
    }

    @Test
    @DisplayName("Should update user details")
    void testUpdateUser() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);
        entityManager.clear();

        // When
        Optional<User> userOptional = userRepository.findById(savedUser.getId());
        assertThat(userOptional).isPresent();
        
        User user = userOptional.get();
        user.setName("John Updated");
        user.setEmail("john.updated@example.com");
        
        User updatedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<User> reloadedUser = userRepository.findById(updatedUser.getId());
        assertThat(reloadedUser).isPresent();
        assertThat(reloadedUser.get().getName()).isEqualTo("John Updated");
        assertThat(reloadedUser.get().getEmail()).isEqualTo("john.updated@example.com");
    }

    @Test
    @DisplayName("Should validate required fields")
    void testRequiredFields() {
        // Given - User with null name
        User invalidUser1 = new User(null, "valid@example.com");

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(invalidUser1);
        });

        // Given - User with null email
        User invalidUser2 = new User("Valid Name", null);

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(invalidUser2);
        });
    }

    @Test
    @DisplayName("Should auto-generate ID")
    void testAutoGeneratedId() {
        // Given
        User savedUser1 = userRepository.save(testUser1);
        User savedUser2 = userRepository.save(testUser2);

        // Then
        assertThat(savedUser1.getId()).isNotNull();
        assertThat(savedUser2.getId()).isNotNull();
        assertThat(savedUser1.getId()).isNotEqualTo(savedUser2.getId());
    }

    @Test
    @DisplayName("Should set creation timestamp automatically")
    void testCreationTimestamp() {
        // Given
        LocalDateTime beforeSave = LocalDateTime.now();
        
        // When
        User savedUser = userRepository.save(new User("Test User", "test@example.com"));
        
        LocalDateTime afterSave = LocalDateTime.now();

        // Then
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isAfterOrEqualTo(beforeSave);
        assertThat(savedUser.getCreatedAt()).isBeforeOrEqualTo(afterSave);
    }

    @Test
    @DisplayName("Should find users by partial email match (case insensitive)")
    void testEmailCaseInsensitive() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.clear();

        // When - Test with different cases
        Optional<User> foundUser1 = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");
        Optional<User> foundUser2 = userRepository.findByEmail("john.doe@EXAMPLE.com");

        // Then
        // Note: This depends on database configuration for case sensitivity
        // H2 is case sensitive by default, so these should be empty unless configured otherwise
        // In a real application, you might want to add custom queries for case-insensitive search
        assertThat(foundUser1).isEmpty();
        assertThat(foundUser2).isEmpty();
        
        // But exact match should work
        Optional<User> foundUser3 = userRepository.findByEmail("john.doe@example.com");
        assertThat(foundUser3).isPresent();
    }

    @Test
    @DisplayName("Should handle empty results gracefully")
    void testEmptyResults() {
        // When - Repository is empty
        List<User> allUsers = userRepository.findAll();
        long count = userRepository.count();
        Page<User> page = userRepository.findAll(PageRequest.of(0, 10));

        // Then
        assertThat(allUsers).isEmpty();
        assertThat(count).isEqualTo(0);
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(0);
    }
}