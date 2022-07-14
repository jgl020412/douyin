package com.atjgl.mapper;

import com.atjgl.vo.FansVO;
import com.atjgl.vo.VlogerVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FansMapperCustom {
    public List<FansVO> queryFansList(@Param("paramMap")Map<String, Object> map);
    public List<VlogerVO> queryFollowList(@Param("paramMap")Map<String, Object> map);
}