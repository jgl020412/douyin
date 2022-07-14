package com.atjgl.bo;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author 小亮
 * 有关注册的事务对象类
 **/

@Data
@ToString
public class RegisterLoginBO {

    @NotBlank(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机号长度不正确")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    private String smsCode;

}
