package com.Mapper;

import com.DTO.RoleRequestDTO;
import com.DTO.RoleResponseDTO;
import com.Entity.Role;

public class RoleMapper {

    private RoleMapper() {}

    public static Role toEntity(RoleRequestDTO dto) {
        Role role = new Role();
        role.setName(dto.getName());
        return role;
    }

    public static RoleResponseDTO toDto(Role role) {
        return new RoleResponseDTO(
                role.getId(),
                role.getName()
        );
    }
}
