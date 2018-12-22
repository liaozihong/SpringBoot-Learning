package com.dashuai.learning.jwt.model;

import java.io.Serializable;

/**
 * Role permission
 * <p/>
 * Created in 2018.12.21
 * <p/>
 *
 * @author Liaozihong
 */
public class RolePermission  implements Serializable {
    private static final long serialVersionUID = 3811966295048880600L;
    private Integer roleId;

    private Integer permissionId;

    /**
     * Gets role id.
     *
     * @return the role id
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * Sets role id.
     *
     * @param roleId the role id
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets permission id.
     *
     * @return the permission id
     */
    public Integer getPermissionId() {
        return permissionId;
    }

    /**
     * Sets permission id.
     *
     * @param permissionId the permission id
     */
    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
}