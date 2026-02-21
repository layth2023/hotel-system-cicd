package com.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    Page<RoleResponseDTO> getAll(Pageable pageable);

    RoleResponseDTO getById(Long id);

    RoleResponseDTO create(RoleRequestDTO dto);

    RoleResponseDTO update(Long id, RoleRequestDTO dto);

    void delete(Long id);
}