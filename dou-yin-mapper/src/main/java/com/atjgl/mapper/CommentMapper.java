package com.atjgl.mapper;

import com.atjgl.my.mapper.MyMapper;
import com.atjgl.pojo.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMapper extends MyMapper<Comment> {
}