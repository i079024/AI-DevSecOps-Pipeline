package com.example.aidevops.service;

import com.example.aidevops.model.User;
import com.example.aidevops.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com");
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());

        User user2 = new User("Jane Smith", "jane@example.com");
        user2.setId(2L);
        user2.setCreatedAt(LocalDateTime.now());

        User user3 = new User("Bob Johnson", "bob@example.com");
        user3.setId(3L);
        user3.setCreatedAt(LocalDateTime.now());

        testUsers = Arrays.asList(testUser, user2, user3);
    }

    @Test
    @DisplayName("Should return all users")
    void testGetAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(testUsers);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return all users with sorting")
    void testGetAllUsersWithSorting() {
        // Given
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        when(userRepository.findAll(sort)).thenReturn(testUsers);

        // When
        List<User> result = userService.getAllUsers("name", "asc");

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(testUsers);
        verify(userRepository).findAll(sort);
    }

    @Test
    @DisplayName("Should return all users with default sorting when parameters are null")
    void testGetAllUsersWithNullSorting() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When
        List<User> result = userService.getAllUsers(null, null);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(testUsers);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return users with pagination")
    void testGetUsersPaginated() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> userPage = new PageImpl<>(testUsers.subList(0, 2), pageable, testUsers.size());
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<User> result = userService.getUsersPaginated(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should find user by ID")
    void testGetUserById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void testGetUserByIdNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find user by email")
    void testGetUserByEmail() {
        // Given
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail("john@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void testGetUserByEmailNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should create new user successfully")
    void testCreateUser() {
        // Given
        User newUser = new User("New User", "new@example.com");
        User savedUser = new User("New User", "new@example.com");
        savedUser.setId(4L);
        savedUser.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(savedUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getName()).isEqualTo("New User");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing email")
    void testCreateUserWithExistingEmail() {
        // Given
        User newUser = new User("New User", "john@example.com");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with email john@example.com already exists");

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update existing user")
    void testUpdateUser() {
        // Given
        User userDetails = new User("Updated Name", "updated@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = userService.updateUser(1L, userDetails);

        // Then
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUserNotFound() {
        // Given
        User userDetails = new User("Updated Name", "updated@example.com");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(999L, userDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 999");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should partially update user")
    void testPartialUpdateUser() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = userService.partialUpdateUser(1L, updates);

        // Then
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("john@example.com"); // Should remain unchanged
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when partially updating non-existent user")
    void testPartialUpdateUserNotFound() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.partialUpdateUser(999L, updates))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 999");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating email to existing one")
    void testPartialUpdateUserWithExistingEmail() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "jane@example.com");
        
        User existingUser = new User("Jane Smith", "jane@example.com");
        existingUser.setId(2L);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> userService.partialUpdateUser(1L, updates))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already exists for another user");

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("jane@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user by ID")
    void testDeleteUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 999");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should search users by name and email")
    void testSearchUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When
        List<User> result = userService.searchUsers("john");

        // Then - Should find both "John Doe" and "Bob Johnson" (contains "john")
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName)
                .containsExactlyInAnyOrder("John Doe", "Bob Johnson");
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should search users by email")
    void testSearchUsersByEmail() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When
        List<User> result = userService.searchUsers("jane@example.com");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jane Smith");
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users found in search")
    void testSearchUsersNoResults() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When
        List<User> result = userService.searchUsers("nonexistent");

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should delete multiple users by IDs")
    void testDeleteMultipleUsers() {
        // Given
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUsers.get(0)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUsers.get(1)));
        when(userRepository.findById(3L)).thenReturn(Optional.of(testUsers.get(2)));
        doNothing().when(userRepository).delete(any(User.class));

        // When
        int result = userService.deleteMultipleUsers(userIds);

        // Then
        assertThat(result).isEqualTo(3);
        verify(userRepository, times(3)).findById(anyLong());
        verify(userRepository, times(3)).delete(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when some users not found in bulk delete")
    void testDeleteMultipleUsersWithNotFound() {
        // Given
        List<Long> userIds = Arrays.asList(1L, 999L, 2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUsers.get(0)));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUsers.get(1)));

        // When & Then
        assertThatThrownBy(() -> userService.deleteMultipleUsers(userIds))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Users not found with IDs: 999");

        verify(userRepository, times(3)).findById(anyLong());
    }

    @Test
    @DisplayName("Should get total count of users")
    void testGetUserCount() {
        // Given
        when(userRepository.count()).thenReturn(5L);

        // When
        long result = userService.getUserCount();

        // Then
        assertThat(result).isEqualTo(5L);
        verify(userRepository).count();
    }

    @Test
    @DisplayName("Should handle null search query")
    void testSearchUsersWithNullQuery() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When & Then
        assertThatThrownBy(() -> userService.searchUsers(null))
                .isInstanceOf(NullPointerException.class);

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should handle empty search query")
    void testSearchUsersWithEmptyQuery() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When
        List<User> result = userService.searchUsers("");

        // Then
        assertThat(result).hasSize(3); // All users should match empty string
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should verify interaction counts")
    void testInteractionCounts() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When - Call method multiple times
        userService.getAllUsers();
        userService.getAllUsers();
        userService.getAllUsers();

        // Then - Verify it was called exactly 3 times
        verify(userRepository, times(3)).findAll();
    }

    @Test
    @DisplayName("Should verify no unexpected interactions")
    void testNoUnexpectedInteractions() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        userService.getUserById(1L);

        // Then - Verify only expected method was called
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should test case insensitive search")
    void testCaseInsensitiveSearch() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When
        List<User> result1 = userService.searchUsers("JOHN");
        List<User> result2 = userService.searchUsers("john");
        List<User> result3 = userService.searchUsers("John");

        // Then - Should find both "John Doe" and "Bob Johnson" (contains "john") for all case variations
        assertThat(result1).hasSize(2);
        assertThat(result2).hasSize(2);
        assertThat(result3).hasSize(2);
        verify(userRepository, times(3)).findAll();
    }

    @Test
    @DisplayName("Should handle partial update with empty values")
    void testPartialUpdateWithEmptyValues() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "");
        updates.put("email", "   ");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = userService.partialUpdateUser(1L, updates);

        // Then - Empty values should not update the fields
        assertThat(result.getName()).isEqualTo("John Doe"); // Should remain unchanged
        assertThat(result.getEmail()).isEqualTo("john@example.com"); // Should remain unchanged
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }
}