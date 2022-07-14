package com.atjgl.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author 小亮
 * 有关更新用户信息的事务对象类
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdatedUsersBO {
    private String id;
    private String nickname;
    private String imoocNum;
    private String face;
    private Integer sex;
    private Date birthday;
    private String country;
    private String province;
    private String city;
    private String description;
    private String bgImg;
    private Integer canImoocNumBeUpdated;
}

