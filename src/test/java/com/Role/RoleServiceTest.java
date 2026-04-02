package com.Role;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Role Service Tests")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleRequestDTO requestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        role.setDescription("Standard user role");
        role.setActive(true);

        requestDTO = new RoleRequestDTO();
        requestDTO.setName("USER");
        requestDTO.setDescription("Standard user role");

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Create Role Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create role successfully")
        void shouldCreateRole() {
            when(roleRepository.existsByName("ROLE_USER")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenReturn(role);

            RoleResponseDTO result = roleService.create(requestDTO);

            assertNotNull(result);
            assertEquals("ROLE_USER", result.getName());
            verify(roleRepository).save(any(Role.class));
        }

        @Test
        @DisplayName("Should add ROLE_ prefix if not present")
        void shouldAddRolePrefix() {
            when(roleRepository.existsByName("ROLE_ADMIN")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenReturn(role);

            RoleRequestDTO dto = new RoleRequestDTO();
            dto.setName("ADMIN");

            roleService.create(dto);

            verify(roleRepository).existsByName("ROLE_ADMIN");
        }

        @Test
        @DisplayName("Should not duplicate ROLE_ prefix")
        void shouldNotDuplicateRolePrefix() {
            when(roleRepository.existsByName("ROLE_USER")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenReturn(role);

            RoleRequestDTO dto = new RoleRequestDTO();
            dto.setName("ROLE_USER");

            roleService.create(dto);

            verify(roleRepository).existsByName("ROLE_USER");
        }

        @Test
        @DisplayName("Should throw exception when role name exists")
        void shouldThrowExceptionWhenNameExists() {
            when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

            assertThrows(RoleAlreadyExistsException.class, () -> roleService.create(requestDTO));
            verify(roleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when role name is blank")
        void shouldThrowExceptionWhenNameBlank() {
            requestDTO.setName("");

            assertThrows(RoleBadRequestException.class, () -> roleService.create(requestDTO));
        }

        @Test
        @DisplayName("Should throw exception when role name is null")
        void shouldThrowExceptionWhenNameNull() {
            requestDTO.setName(null);

            assertThrows(RoleBadRequestException.class, () -> roleService.create(requestDTO));
        }

        @Test
        @DisplayName("Should uppercase role name")
        void shouldUppercaseRoleName() {
            when(roleRepository.existsByName("ROLE_MANAGER")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenReturn(role);

            RoleRequestDTO dto = new RoleRequestDTO();
            dto.setName("manager");

            roleService.create(dto);

            verify(roleRepository).existsByName("ROLE_MANAGER");
        }
    }

    @Nested
    @DisplayName("Get Role Tests")
    class GetTests {

        @Test
        @DisplayName("Should get role by id")
        void shouldGetById() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

            RoleResponseDTO result = roleService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("ROLE_USER", result.getName());
        }

        @Test
        @DisplayName("Should throw exception when role not found")
        void shouldThrowExceptionWhenNotFound() {
            when(roleRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> roleService.getById(999L));
        }

        @Test
        @DisplayName("Should get all roles paginated")
        void shouldGetAllPaginated() {
            Page<Role> rolePage = new PageImpl<>(List.of(role));
            when(roleRepository.findAll(pageable)).thenReturn(rolePage);

            Page<RoleResponseDTO> result = roleService.getAll(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should return empty page when no roles")
        void shouldReturnEmptyPageWhenNoRoles() {
            Page<Role> emptyPage = new PageImpl<>(List.of());
            when(roleRepository.findAll(pageable)).thenReturn(emptyPage);

            Page<RoleResponseDTO> result = roleService.getAll(pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Update Role Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update role successfully")
        void shouldUpdateRole() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            when(roleRepository.existsByName("ROLE_ADMIN")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenReturn(role);

            RoleRequestDTO updateDTO = new RoleRequestDTO();
            updateDTO.setName("ADMIN");
            updateDTO.setDescription("Admin role");

            RoleResponseDTO result = roleService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals("ROLE_ADMIN", role.getName());
            verify(roleRepository).save(any(Role.class));
        }

        @Test
        @DisplayName("Should update description only")
        void shouldUpdateDescriptionOnly() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            when(roleRepository.save(any(Role.class))).thenReturn(role);

            RoleRequestDTO updateDTO = new RoleRequestDTO();
            updateDTO.setName(null);
            updateDTO.setDescription("Updated description");

            roleService.update(1L, updateDTO);

            assertEquals("Updated description", role.getDescription());
            assertEquals("ROLE_USER", role.getName());
        }

        @Test
        @DisplayName("Should update active status")
        void shouldUpdateActiveStatus() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            when(roleRepository.save(any(Role.class))).thenReturn(role);

            RoleRequestDTO updateDTO = new RoleRequestDTO();
            updateDTO.setActive(false);

            roleService.update(1L, updateDTO);

            assertFalse(role.isActive());
        }

        @Test
        @DisplayName("Should allow updating to same name")
        void shouldAllowUpdatingToSameName() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            when(roleRepository.save(any(Role.class))).thenReturn(role);

            RoleRequestDTO updateDTO = new RoleRequestDTO();
            updateDTO.setName("USER");

            roleService.update(1L, updateDTO);

            verify(roleRepository, never()).existsByName(anyString());
        }

        @Test
        @DisplayName("Should throw exception when updating to existing name")
        void shouldThrowExceptionWhenUpdatingToExistingName() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            when(roleRepository.existsByName("ROLE_ADMIN")).thenReturn(true);

            RoleRequestDTO updateDTO = new RoleRequestDTO();
            updateDTO.setName("ADMIN");

            assertThrows(RoleAlreadyExistsException.class, () -> roleService.update(1L, updateDTO));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent role")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            when(roleRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> roleService.update(999L, requestDTO));
        }
    }

    @Nested
    @DisplayName("Delete Role Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete role")
        void shouldDeleteRole() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

            roleService.delete(1L);

            verify(roleRepository).delete(role);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent role")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            when(roleRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> roleService.delete(999L));
        }
    }
}
