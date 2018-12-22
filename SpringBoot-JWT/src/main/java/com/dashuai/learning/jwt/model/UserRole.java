package com.dashuai.learning.jwt.model;

import java.io.Serializable;

/**
 * User role
 * <p/>
 * Created in 2018.12.21
 * <p/>
 *
 * @author Liaozihong
 */
public class UserRole  implements Serializable {

    private static final long serialVersionUID = -7317214882734267788L;

    public UserRole() {
    }
    public UserRole(Integer roleId, Integer uid) {
        this.roleId = roleId;
        this.uid = uid;
    }

    private Integer roleId;

    private Integer uid;

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
     * Gets uid.
     *
     * @return the uid
     */
    public Integer getUid() {
        return uid;
    }

    /**
     * Sets uid.
     *
     * @param uid the uid
     */
    public void setUid(Integer uid) {
        this.uid = uid;
    }
}