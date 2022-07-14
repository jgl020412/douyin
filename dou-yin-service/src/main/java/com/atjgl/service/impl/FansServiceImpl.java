package com.atjgl.service.impl;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.base.RabbitMQConfig;
import com.atjgl.enums.MessageEnum;
import com.atjgl.enums.YesOrNo;
import com.atjgl.grace.result.PagedGridResult;
import com.atjgl.mapper.FansMapper;
import com.atjgl.mapper.FansMapperCustom;
import com.atjgl.mo.MessageMO;
import com.atjgl.pojo.Fans;
import com.atjgl.service.FansService;
import com.atjgl.util.JsonUtils;
import com.atjgl.vo.FansVO;
import com.atjgl.vo.VlogerVO;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小亮
 **/

@Service
public class FansServiceImpl extends BaseInfoProperties implements FansService {

    @Autowired
    private Sid sid;

    @Autowired
    private FansMapper fansMapper;

    @Autowired
    private FansMapperCustom fansMapperCustom;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public void doFollows(String myId, String vlogerId) {
        Fans fans = new Fans();
        String fid = sid.nextShort();
        fans.setId(fid);
        fans.setFanId(myId);
        fans.setVlogerId(vlogerId);
        Fans vloger = queryFansRelationship(vlogerId, myId);
        if (vloger != null) {
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);
            vloger.setIsFanFriendOfMine(YesOrNo.YES.type);
            fansMapper.updateByPrimaryKeySelective(vloger);
        } else {
            fans.setIsFanFriendOfMine(YesOrNo.NO.type);
        }
        redisOperator.increment(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redisOperator.increment(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);
        redisOperator.set(REDIS_MY_AND_VLOGER_RELATIONSHIP + ":" + myId + ":" + vlogerId, "1");
        fansMapper.insert(fans);
        MessageMO messageMO = new MessageMO();
        messageMO.setToUserId(vlogerId);
        messageMO.setFromUserId(myId);
        messageMO.setMsgType(MessageEnum.FOLLOW_YOU.type);
        String payload = JsonUtils.objectToJson(messageMO);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,
                "sys.msg." + MessageEnum.FOLLOW_YOU.enValue,
                payload);
    }

    @Transactional
    @Override
    public void doCancel(String myId, String vlogerId) {
        Fans fans = queryFansRelationship(myId, vlogerId);
        if (fans != null && fans.getIsFanFriendOfMine() == YesOrNo.YES.type) {
            Fans relationship = queryFansRelationship(vlogerId, myId);
            relationship.setIsFanFriendOfMine(YesOrNo.NO.type);
            fansMapper.updateByPrimaryKeySelective(relationship);
        }
        fansMapper.delete(fans);
        redisOperator.decrement(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redisOperator.decrement(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);
        redisOperator.del(REDIS_MY_AND_VLOGER_RELATIONSHIP + ":" + myId + ":" + vlogerId);
    }

    @Override
    public boolean isFollow(String myId, String vlogerId) {
        Fans fans = queryFansRelationship(myId, vlogerId);
        return fans != null;
    }

    @Override
    public PagedGridResult queryFollowList(String myId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PageHelper.startPage(page, pageSize);
        List<VlogerVO> vlogerVOS = fansMapperCustom.queryFollowList(map);
        return setterPagedGrid(vlogerVOS, page);
    }

    @Override
    public PagedGridResult queryFansList(String myId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PageHelper.startPage(page, pageSize);
        map.put("myId", myId);
        List<FansVO> fansVOS = fansMapperCustom.queryFansList(map);
        for (FansVO f : fansVOS) {
            String value =
                    redisOperator.get(REDIS_MY_AND_VLOGER_RELATIONSHIP + ":" + myId + ":" + f.getFanId());
            if (StringUtils.isNotBlank(value) && value.equals("1")) {
                f.setFriend(true);
            }
        }
        return setterPagedGrid(fansVOS, page);
    }


    /**
     * 判断两个人是否存在粉丝关系
     */
    private Fans queryFansRelationship(String fanId, String vlogerId) {
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("fanId", fanId);
        criteria.andEqualTo("vlogerId", vlogerId);
        List<Fans> fans = fansMapper.selectByExample(example);
        if (fans != null && fans.size() != 0 && !fans.isEmpty())  {
            return fans.get(0);
        }
        return null;
    }

}
