package com.codegym.huongtran_shop.controller.api;

import com.codegym.huongtran_shop.service.role.IRoleService;
import com.codegym.huongtran_shop.model.dto.role.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleAPI {
    @Autowired
    private IRoleService roleService;

    @GetMapping()
    public ResponseEntity<?> getAllRoleDTO() {
        List<RoleDTO> roleDTOS = roleService.getAllRoleDTO();
        if (roleDTOS.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(roleDTOS, HttpStatus.OK);
    }
}
