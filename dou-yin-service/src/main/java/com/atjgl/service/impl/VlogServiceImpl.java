package com.atjgl.service.impl;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.base.RabbitMQConfig;
import com.atjgl.bo.VlogBO;
import com.atjgl.enums.MessageEnum;
import com.atjgl.enums.YesOrNo;
import com.atjgl.grace.result.PagedGridResult;
import com.atjgl.mapper.MyLikedVlogMapper;
import com.atjgl.mapper.VlogMapper;
import com.atjgl.mapper.VlogMapperCustom;
import com.atjgl.mo.MessageMO;
import com.atjgl.pojo.MyLikedVlog;
import com.atjgl.pojo.Vlog;
import com.atjgl.service.VlogService;
import com.atjgl.util.JsonUtils;
import com.atjgl.vo.IndexVlogVO;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小亮
 **/

@Service
public class VlogServiceImpl extends BaseInfoProperties implements VlogService {

    @Autowired
    private Sid sid;

    @Autowired
    private VlogMapper vlogMapper;

    @Autowired
    private VlogMapperCustom vlogMapperCustom;

    @Autowired
    private MyLikedVlogMapper myLikedVlogMapper;

    @Autowired
    private FansServiceImpl fansService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public void createVlog(VlogBO vlogBO) {
        Vlog vlog = new Vlog();
        BeanUtils.copyProperties(vlogBO, vlog);

        String vid = sid.nextShort();
        vlog.setId(vid);
        vlog.setCommentsCounts(0);
        vlog.setLikeCounts(0);
        vlog.setCreatedTime(new Date());
        vlog.setUpdatedTime(new Date());
        vlog.setIsPrivate(YesOrNo.NO.type);

        vlogMapper.insert(vlog);
    }

    @Override
    public PagedGridResult queryVlogList(String userId, String search, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isNotBlank(search)) {
            map.put("search", search);
        }
        List<IndexVlogVO> indexVlogList = vlogMapperCustom.getIndexVlogList(map);
        for (IndexVlogVO i : indexVlogList) {
            i = setterOV(i, userId);
        }
        return setterPagedGrid(indexVlogList, page);
    }

    private IndexVlogVO setterOV(IndexVlogVO indexVlogVO, String userId) {
        if (StringUtils.isNotBlank(userId)) {
            String vlogerId = indexVlogVO.getVlogerId();
            boolean follow = fansService.isFollow(userId, vlogerId);
            indexVlogVO.setDoIFollowVloger(follow);
            indexVlogVO.setDoILikeThisVlog(isLike(userId, indexVlogVO.getVlogId()));
        }
        indexVlogVO.setLikeCounts(getCountsOfLike(indexVlogVO.getVlogId()));
        return indexVlogVO;
    }

    @Override
    public PagedGridResult getMyLikeList(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(userId)) {
            map.put("userId", userId);
        }
        List<IndexVlogVO> myLikedVlogList = vlogMapperCustom.getMyLikedVlogList(map);
        return setterPagedGrid(myLikedVlogList, page);
    }

    private boolean isLike(String myId, String vlogId) {
        boolean isLike = false;
        String value = redisOperator.get(REDIS_USER_LIKE_VLOG + ":" + myId + ":" + vlogId);
        if (value != null && value.equalsIgnoreCase("1")) {
            isLike = true;
        }
        return isLike;
    }

    @Override
    public IndexVlogVO queryVlogById(String id, String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        if (id != null && id != "" && !id.isEmpty()) {
            List<IndexVlogVO> detailVlog = vlogMapperCustom.getDetailById(map);
            IndexVlogVO indexVlogVO = detailVlog.get(0);
            indexVlogVO = setterOV(indexVlogVO, userId);
            return indexVlogVO;
        }
        return null;
    }

    @Transactional
    @Override
    public void changePrivateOrPublic(String userId, String vlogId, Integer yesOrNo) {
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", vlogId);
        criteria.andEqualTo("vlogerId", userId);
        Vlog vlog = new Vlog();
        vlog.setIsPrivate(yesOrNo);
        vlogMapper.updateByExampleSelective(vlog, example);
    }

    @Override
    public PagedGridResult queryMyVlogList(String userId, Integer page, Integer pageSize, Integer yesOrNo) {
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId", userId);
        criteria.andEqualTo("isPrivate", yesOrNo);
        PageHelper.startPage(page, pageSize);
        List<Vlog> vlogs = vlogMapper.selectByExample(example);
        return setterPagedGrid(vlogs, page);
    }

    @Transactional
    @Override
    public void like(String myId, String vlogId, String vlogerId) {
        String id = sid.nextShort();
        MyLikedVlog myLikedVlog = new MyLikedVlog();
        myLikedVlog.setId(id);
        myLikedVlog.setVlogId(vlogId);
        myLikedVlog.setUserId(myId);
        myLikedVlogMapper.insert(myLikedVlog);
        redisOperator.increment(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId, 1);
        redisOperator.increment(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);
        redisOperator.set(REDIS_USER_LIKE_VLOG + ":" + myId + ":" + vlogId, "1");
        Vlog vlog = vlogMapper.selectByPrimaryKey(vlogId);

        Map msgContent = new HashMap<>();
        msgContent.put("vlogId", vlog.getId());
        msgContent.put("vlogCover", vlog.getCover());
        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(myId);
        messageMO.setToUserId(vlogerId);
        messageMO.setMsgType(MessageEnum.LIKE_VLOG.type);
        messageMO.setMsgContent(msgContent);
        String payload = JsonUtils.objectToJson(messageMO);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,
                "sys.msg." + MessageEnum.FOLLOW_YOU.enValue,
                payload);
    }

    @Transactional
    @Override
    public void unlike(String myId, String vlogId, String vlogerId) {
        MyLikedVlog myLikedVlog = new MyLikedVlog();
        myLikedVlog.setVlogId(vlogId);
        myLikedVlog.setUserId(myId);
        myLikedVlogMapper.delete(myLikedVlog);
        redisOperator.decrement(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId, 1);
        redisOperator.decrement(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);
        redisOperator.del(REDIS_USER_LIKE_VLOG + ":" + myId + ":" + vlogId);
    }

    @Override
    public Integer getCountsOfLike(String vlogId) {
        String countStr = redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if (countStr == null) {
            countStr = "0";
        }
        return Integer.valueOf(countStr);
    }

    @Override
    public PagedGridResult getMyFollowVlogList(String myId, Integer page, Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PageHelper.startPage(page, pageSize);
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(myId)) {
            map.put("myId", myId);
        }
        List<IndexVlogVO> myFollowVlogList = vlogMapperCustom.getMyFollowVlogList(map);
        for (IndexVlogVO i : myFollowVlogList) {
            String vlogId = i.getVlogId();
            if (StringUtils.isNotBlank(myId)) {
                i.setDoIFollowVloger(true);
                i.setDoILikeThisVlog(isLike(myId, vlogId));
            }
            i.setLikeCounts(Integer.valueOf(getCountsOfLike(vlogId)));
        }
        return setterPagedGrid(myFollowVlogList, page);
    }

    @Override
    public PagedGridResult getMyFriendVlogList(String myId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(myId)) {
            map.put("myId", myId);
        }
        List<IndexVlogVO> myFriendVlogList = vlogMapperCustom.getMyFriendVlogList(map);
        for (IndexVlogVO i : myFriendVlogList) {
            String vlogId = i.getVlogId();
            if (StringUtils.isNotBlank(myId)) {
                i.setDoIFollowVloger(true);
                i.setDoILikeThisVlog(isLike(myId, vlogId));
            }
            i.setLikeCounts(Integer.valueOf(getCountsOfLike(vlogId)));
        }
        return setterPagedGrid(myFriendVlogList, page);
    }

    @Override
    public Vlog getVlog(String id) {
        return vlogMapper.selectByPrimaryKey(id);
    }

    @Override
    public void flushLike(String vlogeId, Integer counts) {
        Vlog vlog = new Vlog();
        vlog.setId(vlogeId);
        vlog.setLikeCounts(counts);
        vlogMapper.updateByPrimaryKeySelective(vlog);
    }
}
