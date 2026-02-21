package com.Role;

public class RoleMapper {

    private RoleMapper() {}

    public static RoleResponseDTO toResponse(Role role) {
        long count = role.getUsers() == null ? 0 : role.getUsers().size();

        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isActive(),
                count
        );
    }

    public static Role toEntity(RoleRequestDTO dto) {
        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setActive(dto.getActive() == null ? true : dto.getActive());
        return role;
    }
}