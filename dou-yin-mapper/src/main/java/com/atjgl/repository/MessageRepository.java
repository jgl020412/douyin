package com.atjgl.repository;

import com.atjgl.mo.MessageMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 小亮
 **/

@Repository
public interface MessageRepository extends MongoRepository<MessageMO, String> {
    public List<MessageMO> findAllByToUserIdEqualsOrderByCreateTimeDesc(String toUserId, Pageable pageable);
}
