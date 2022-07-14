package com.atjgl.mapper;

import com.atjgl.my.mapper.MyMapper;
import com.atjgl.pojo.Comment;
import com.atjgl.vo.CommentVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentMapperCustom extends MyMapper<Comment> {

    public List<CommentVO> getCommentList(@Param("paramMap")Map<String, Object> map);

}