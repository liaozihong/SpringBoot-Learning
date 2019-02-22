package com.dashuai.learning.validator.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UserInfo {

    @NotNull(message = "id不能为空")
    private Integer id;
    @NotNull(message = "userName不能为空")
    @Length(min = 2, max = 12, message = "userName长度在2-12之间")
    private String userName;
    @NotNull(message = "sex不能为空")
    @Min(value = 0, message = "性别只能为0、1、2，0为男，1为女，2为中性")
    @Max(value = 2, message = "性别只能为0、1、2，0为男，1为女，2为中性")
    private Integer sex;
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄最小为0")
    @Max(value = 150, message = "年龄最高为150")
    private Integer age;

}
