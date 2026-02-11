package com.Service;

import com.DTO.RoleResponseDTO;
import java.util.List;

public interface RoleService {

    List<RoleResponseDTO> findAll();
    void initDefaultRoles();
}
