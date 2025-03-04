package com.busuu.app.services.role;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.role.RoleDTO;
import com.busuu.app.entities.Role;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Role insertRole (String requestId, RoleDTO roleDTO) {
        try {
            if (roleRepository.existsByName(roleDTO.getName())) {
                throw new ExistDataException("Role's name is duplicated");
            }
            Role role = modelMapper.map(roleDTO, Role.class);
            role.setId(UUID.randomUUID().toString());
            return roleRepository.save(role);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to create role, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_ROLE, requestId);
        }
    }

    @Override
    public Role getRole(String requestId, String roleId) {
        try {
            return roleRepository.findById(roleId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Role with ID = " + roleId));
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get role, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ROLE, requestId);
        }
    }

    @Override
    @Transactional
    public Role updateRole(String requestId, String roleId, RoleDTO roleDTO) {
        try {
            Role existingRole = roleRepository.findById(roleId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Role with ID = " + roleId));
            existingRole.setName(roleDTO.getName());
            return roleRepository.save(existingRole);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to update role, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_ROLE, requestId);
        }
    }

    @Override
    public List<Role> getRoles(String requestId) {
        try {
            return roleRepository.findAll();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get roles, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ROLE, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteRole(String requestId, String roleId) {
        try {
            roleRepository.deleteById(roleId);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete role, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_ROLE, requestId);
        }
    }
}
