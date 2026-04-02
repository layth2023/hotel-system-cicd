package com.User;

import com.Role.Role;
import com.Role.RoleNotFoundException;
import com.Role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO requestDTO;
    private UserUpdateRequestDTO updateDTO;
    private Role role;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setRoles(new HashSet<>());

        requestDTO = new UserRequestDTO();
        requestDTO.setUsername("testuser");
        requestDTO.setEmail("test@example.com");
        requestDTO.setPassword("TestPassword123!");

        updateDTO = new UserUpdateRequestDTO();
        updateDTO.setUsername("updateduser");
        updateDTO.setEmail("updated@example.com");

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUser() {
            when(userRepository.existsByUsername("testuser")).thenReturn(false);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserResponseDTO result = userService.create(requestDTO);

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            verify(passwordEncoder).encode(requestDTO.getPassword());
        }

        @Test
        @DisplayName("Should throw exception when username is null or blank")
        void shouldThrowExceptionWhenUsernameBlank() {
            requestDTO.setUsername("");

            assertThrows(UserBadRequestException.class, () -> userService.create(requestDTO));
        }

        @Test
        @DisplayName("Should throw exception when username exists")
        void shouldThrowExceptionWhenUsernameExists() {
            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            assertThrows(UserAlreadyExistsException.class, () -> userService.create(requestDTO));
        }

        @Test
        @DisplayName("Should throw exception when email exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(userRepository.existsByUsername("testuser")).thenReturn(false);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            assertThrows(UserAlreadyExistsException.class, () -> userService.create(requestDTO));
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetTests {

        @Test
        @DisplayName("Should get user by id")
        void shouldGetById() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            UserResponseDTO result = userService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("testuser", result.getUsername());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getById(999L));
        }

        @Test
        @DisplayName("Should get all users paginated")
        void shouldGetAllPaginated() {
            Page<User> userPage = new PageImpl<>(List.of(user));
            when(userRepository.findAll(pageable)).thenReturn(userPage);

            Page<UserResponseDTO> result = userService.getAll(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername("updateduser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserResponseDTO result = userService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals("updateduser", user.getUsername());
        }

        @Test
        @DisplayName("Should update user password")
        void shouldUpdatePassword() {
            updateDTO.setPassword("NewPassword123!");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername("updateduser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
            when(passwordEncoder.encode("NewPassword123!")).thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            userService.update(1L, updateDTO);

            verify(passwordEncoder).encode("NewPassword123!");
        }

        @Test
        @DisplayName("Should throw exception when updating to existing username")
        void shouldThrowExceptionWhenUpdatingToExistingUsername() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername("updateduser")).thenReturn(true);

            assertThrows(UserAlreadyExistsException.class, () -> userService.update(1L, updateDTO));
        }

        @Test
        @DisplayName("Should throw exception when updating to existing email")
        void shouldThrowExceptionWhenUpdatingToExistingEmail() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername("updateduser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(true);

            assertThrows(UserAlreadyExistsException.class, () -> userService.update(1L, updateDTO));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.update(999L, updateDTO));
        }

        @Test
        @DisplayName("Should update enabled status")
        void shouldUpdateEnabledStatus() {
            updateDTO.setEnabled(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername("updateduser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            userService.update(1L, updateDTO);

            assertFalse(user.isEnabled());
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete user")
        void shouldDeleteUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            userService.delete(1L);

            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.delete(999L));
        }
    }

    @Nested
    @DisplayName("Enable/Disable User Tests")
    class EnableDisableTests {

        @Test
        @DisplayName("Should set enabled to true")
        void shouldEnableUser() {
            user.setEnabled(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserResponseDTO result = userService.setEnabled(1L, true);

            assertTrue(user.isEnabled());
        }

        @Test
        @DisplayName("Should set enabled to false")
        void shouldDisableUser() {
            user.setEnabled(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserResponseDTO result = userService.setEnabled(1L, false);

            assertFalse(user.isEnabled());
        }

        @Test
        @DisplayName("Should throw exception for non-existent user")
        void shouldThrowExceptionForNonExistentUser() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.setEnabled(999L, true));
        }
    }

    @Nested
    @DisplayName("Role Management Tests")
    class RoleTests {

        @Test
        @DisplayName("Should add role to user")
        void shouldAddRoleToUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserResponseDTO result = userService.addRoleToUser(1L, 1L);

            assertTrue(user.getRoles().contains(role));
        }

        @Test
        @DisplayName("Should throw exception when adding role to non-existent user")
        void shouldThrowExceptionWhenAddingRoleToNonExistentUser() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.addRoleToUser(999L, 1L));
        }

        @Test
        @DisplayName("Should throw exception when adding non-existent role")
        void shouldThrowExceptionWhenAddingNonExistentRole() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roleRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> userService.addRoleToUser(1L, 999L));
        }

        @Test
        @DisplayName("Should remove role from user")
        void shouldRemoveRoleFromUser() {
            user.getRoles().add(role);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserResponseDTO result = userService.removeRoleFromUser(1L, 1L);

            assertFalse(user.getRoles().contains(role));
        }
    }
}
