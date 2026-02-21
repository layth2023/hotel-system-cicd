package com.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Page<RoleResponseDTO> getAll(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(RoleMapper::toResponse);
    }

    @Override
    public RoleResponseDTO getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));

        return RoleMapper.toResponse(role);
    }

    @Override
    public RoleResponseDTO create(RoleRequestDTO dto) {

        String name = normalizeRoleName(dto.getName());

        if (roleRepository.existsByName(name)) {
            throw new RoleAlreadyExistsException("Role already exists: " + name);
        }

        Role role = RoleMapper.toEntity(dto);
        role.setName(name);

        Role saved = roleRepository.save(role);
        return RoleMapper.toResponse(saved);
    }

    @Override
    public RoleResponseDTO update(Long id, RoleRequestDTO dto) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));

        if (dto.getName() != null) {
            String newName = normalizeRoleName(dto.getName());

            if (!role.getName().equalsIgnoreCase(newName)
                    && roleRepository.existsByName(newName)) {
                throw new RoleAlreadyExistsException("Role already exists: " + newName);
            }

            role.setName(newName);
        }

        if (dto.getDescription() != null)
            role.setDescription(dto.getDescription());

        if (dto.getActive() != null)
            role.setActive(dto.getActive());

        Role updated = roleRepository.save(role);
        return RoleMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));

        roleRepository.delete(role);
    }

    private String normalizeRoleName(String input) {
        if (input == null || input.isBlank()) {
            throw new RoleBadRequestException("Role name cannot be empty");
        }

        String name = input.trim().toUpperCase();

        if (!name.startsWith("ROLE_")) {
            name = "ROLE_" + name;
        }

        return name;
    }
}