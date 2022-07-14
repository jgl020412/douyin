package com.atjgl.service;

import com.atjgl.bo.UpdatedUsersBO;
import com.atjgl.pojo.Users;

/**
 * @author 小亮
 **/

public interface UserService {

    /**
     * 判断用户是否存在，如果存在则返回用户信息
     */
    public Users queryMobileIsExist(String mobile);

    /**
     * 创建用户信息，并返回用户对象
     */
    public Users createUser(String mobile);

    /**
     * 根据用户主键来查询用户对象信息
     */
    public Users queryUser(String userId);

    /**
     * 修改用户信息
     */
    public Users updateUserInfo(UpdatedUsersBO updatedUsersBO);

    /**
     * 修改用户信息
     */
    public Users updateUserInfo(UpdatedUsersBO updatedUsersBO, Integer type);
}
