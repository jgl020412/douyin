package com.atjgl.mapper;

import com.atjgl.my.mapper.MyMapper;
import com.atjgl.pojo.Vlog;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface VlogMapper extends MyMapper<Vlog> {
}