package com.dashuai.learning.jwt.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User info
 * <p/>
 * Created in 2018.12.21
 * <p/>
 *
 * @author Liaozihong
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1571055250200918899L;

    public UserInfo() {

    }
    public UserInfo(UserInfo info) {
        this.username = info.getUsername();
        this.password = info.getPassword();
        this.uid = info.getUid();
        this.name = info.getName();
        this.salt = info.getSalt();
    }

    private Integer uid;
    private String name;
    private String password;

    private String salt;

    private Byte state;

    private String username;

    private Date lastLoginTime;
    private List<Role> roleList;

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
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

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * Gets salt.
     *
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Sets salt.
     *
     * @param salt the salt
     */
    public void setSalt(String salt) {
        this.salt = salt == null ? null : salt.trim();
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public Byte getState() {
        return state;
    }
    /**
     * 密码盐.
     *
     * @return
     */
    public String getCredentialsSalt() {
        return this.username + this.salt;
    }
    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(Byte state) {
        this.state = state;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * Gets last login time.
     *
     * @return the last login time
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * Sets last login time.
     *
     * @param lastLoginTime the last login time
     */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}