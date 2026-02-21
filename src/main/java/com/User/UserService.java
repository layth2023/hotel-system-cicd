package com.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponseDTO> getAll(Pageable pageable);

    UserResponseDTO getById(Long id);

    UserResponseDTO create(UserRequestDTO dto);

    UserResponseDTO update(Long id, UserUpdateRequestDTO dto);

    void delete(Long id);

    UserResponseDTO setEnabled(Long id, boolean enabled);

    UserResponseDTO addRoleToUser(Long userId, Long roleId);

    UserResponseDTO removeRoleFromUser(Long userId, Long roleId);
}