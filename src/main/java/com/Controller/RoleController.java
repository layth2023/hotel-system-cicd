package com.Controller;

import com.DTO.RoleResponseDTO;
import com.Service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @GetMapping
    public List<RoleResponseDTO> getAllRoles() {
        return service.findAll();
    }
}
