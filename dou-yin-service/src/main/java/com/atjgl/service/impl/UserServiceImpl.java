package com.atjgl.service.impl;

import com.atjgl.bo.UpdatedUsersBO;
import com.atjgl.enums.Sex;
import com.atjgl.enums.UserInfoModifyType;
import com.atjgl.enums.YesOrNo;
import com.atjgl.exception.GraceException;
import com.atjgl.grace.result.ResponseStatusEnum;
import com.atjgl.mapper.UsersMapper;
import com.atjgl.pojo.Users;
import com.atjgl.service.UserService;
import com.atjgl.util.DateUtil;
import com.atjgl.util.DesensitizationUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author 小亮
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;
    private static final String USER_FACE1 = "";

    @Override
    public Users queryMobileIsExist(String mobile) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("mobile", mobile);
        Users users = usersMapper.selectOneByExample(userExample);
        return users;
    }

    @Transactional
    @Override
    public Users createUser(String mobile) {
        String userId = sid.nextShort();
        Users user = new Users();
        user.setId(userId);
        user.setMobile(mobile);
        user.setNickname("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setImoocNum("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(USER_FACE1);

        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");
        user.setCanImoocNumBeUpdated(YesOrNo.YES.type);

        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }

    @Override
    public Users queryUser(String userId) {
        return usersMapper.selectByPrimaryKey(userId);
    }

    @Transactional
    @Override
    public Users updateUserInfo(UpdatedUsersBO updatedUsersBO) {
        Users users = new Users();
        BeanUtils.copyProperties(updatedUsersBO, users);
        int result = usersMapper.updateByPrimaryKeySelective(users);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }
        return queryUser(updatedUsersBO.getId());
    }

    @Transactional
    @Override
    public Users updateUserInfo(UpdatedUsersBO updatedUsersBO, Integer type) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        if (type == UserInfoModifyType.NICKNAME.type) {
            criteria.andEqualTo("nickname", updatedUsersBO.getNickname());
            Users users = usersMapper.selectOneByExample(example);
            if (users != null) {
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }
        }
        if (type == UserInfoModifyType.IMOOCNUM.type) {
            criteria.andEqualTo("imoocNum", updatedUsersBO.getImoocNum());
            Users users = usersMapper.selectOneByExample(example);
            if (users != null) {
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_IMOOCNUM_EXIST_ERROR);
            }
            Users tempUser = queryUser(updatedUsersBO.getId());
            if (tempUser.getCanImoocNumBeUpdated() == YesOrNo.NO.type) {
                GraceException.display(ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }
            updatedUsersBO.setCanImoocNumBeUpdated(YesOrNo.NO.type);
        }
        Users users = updateUserInfo(updatedUsersBO);
        return users;
    }
}