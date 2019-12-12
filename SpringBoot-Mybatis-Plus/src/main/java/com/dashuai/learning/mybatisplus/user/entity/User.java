package com.dashuai.learning.mybatisplus.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author zoey
 * @since 2019-12-12
 */
@Accessors(chain = true)
@Data
public class User {

    private static final long serialVersionUID = 1L;


    @TableId
    private Long id;
    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;

    @TableField(fill = FieldFill.INSERT)
    private Date createAt;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateAt;

    @Version
    private Integer version;

}
