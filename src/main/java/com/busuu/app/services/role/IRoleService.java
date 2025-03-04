package com.busuu.app.services.role;

import com.busuu.app.dtos.requests.role.RoleDTO;
import com.busuu.app.entities.Role;

import java.util.List;

public interface IRoleService {
    Role insertRole (String requestId, RoleDTO roleDTO);
    Role getRole (String requestId, String roleId);
    Role updateRole (String requestId, String roleId, RoleDTO roleDTO);
    List<Role> getRoles (String requestId);
    void deleteRole (String requestId, String roleId);
}
