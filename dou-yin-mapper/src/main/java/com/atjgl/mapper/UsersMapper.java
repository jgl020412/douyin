package com.atjgl.mapper;

import com.atjgl.my.mapper.MyMapper;
import com.atjgl.pojo.Users;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersMapper extends MyMapper<Users> {
}