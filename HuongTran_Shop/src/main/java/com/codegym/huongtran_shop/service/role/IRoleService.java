package com.codegym.huongtran_shop.service.role;

import com.codegym.huongtran_shop.model.Role;
import com.codegym.huongtran_shop.model.dto.role.RoleDTO;
import com.codegym.huongtran_shop.service.IGeneralService;

import java.util.List;

public interface IRoleService extends IGeneralService<Role> {
    List<RoleDTO> getAllRoleDTO();

    Role findByCode(String code);
}
