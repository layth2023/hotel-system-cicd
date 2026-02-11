package com.Service;

import com.DTO.RoleResponseDTO;
import com.Entity.Role;
import com.Mapper.RoleMapper;
import com.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initDefaultRoles() {
        createIfNotExists("ADMIN");
        createIfNotExists("STAFF");
        createIfNotExists("USER");
    }

    private void createIfNotExists(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return roleRepository.save(role);
                });
    }

    @Override
    public List<RoleResponseDTO> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(RoleMapper::toDto)
                .toList();
    }
}
