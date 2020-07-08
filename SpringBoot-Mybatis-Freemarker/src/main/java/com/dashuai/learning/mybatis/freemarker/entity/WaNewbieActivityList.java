package com.dashuai.learning.mybatis.freemarker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 活动列表
 * </p>
 *
 * @author liaozihong
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WaNewbieActivityList extends Model<WaNewbieActivityList> {

    private static final long serialVersionUID = -1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 活动图片
     */
    private String activityImg;

    /**
     * 按钮颜色
     */
    private String buttonColor;

    /**
     * 按钮文案
     */
    private String buttonText;

    /**
     * 跳转路径类型,0:小程序客服,1:接龙详情,2:微信列表,3:团购编辑页
     */
    private Boolean jumpType;

    /**
     * 是否启用,0启用,1不启用
     */
    private Boolean isEnable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
