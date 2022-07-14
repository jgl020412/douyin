package com.atjgl.controller;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.bo.RegisterLoginBO;
import com.atjgl.grace.result.GraceJSONResult;
import com.atjgl.grace.result.ResponseStatusEnum;
import com.atjgl.pojo.Users;
import com.atjgl.service.impl.UserServiceImpl;
import com.atjgl.util.IPUtil;
import com.atjgl.util.SMSUtils;
import com.atjgl.vo.UsersVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

/**
 * @author 小亮
 **/
@Slf4j
@RestController
@Api(tags = "PassportController 通行证注册接口模块")
@RequestMapping("passport")
public class PassportController extends BaseInfoProperties {

    @Autowired
    private SMSUtils smsUtils;

    @Autowired
    private UserServiceImpl userService;

    @ApiOperation(value = "发送验证码")
    @PostMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(@RequestParam String mobile,
                                      HttpServletRequest httpServletRequest) throws Exception {
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.ok();
        }

        // 获取用户的IP，并进行限制用户60秒之内，只能请求一次验证码
        String ip = IPUtil.getRequestIp(httpServletRequest);
        redisOperator.setnx60s(MOBILE_SMSCODE + ":" + ip, ip);

        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";

        smsUtils.sendSMS(mobile, code);
        log.info(code);

        // 把验证码添加redis中间件，将用户收到的验证码存储在redis，用于后续验证
        redisOperator.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);

        return GraceJSONResult.ok();
    }

    @ApiOperation(value = "用户一键登录/注册")
    @PostMapping("/login")
    public GraceJSONResult doLogin(@Valid @RequestBody RegisterLoginBO registerLoginBO) {
        String mobile = registerLoginBO.getMobile();
        String smsCode = registerLoginBO.getSmsCode();
        // 1.从redis中获得校验验证码是否正确
        String redisCode = redisOperator.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(smsCode)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 2.查询数据库，判断用户是否注册
        Users users = userService.queryMobileIsExist(mobile);
        if (users == null) {
            users = userService.createUser(mobile);
        }

        // 3.如果用户不为空，表示已注册用户，那么保存用户会话信息
        String uToken = UUID.randomUUID().toString();
        redisOperator.set(REDIS_USER_TOKEN + ":" + users.getId(), uToken);
        log.info(uToken);
        // 4. 用户注册成功后，删除redis中的短信验证码，验证码只能用一次，用过后作废
        redisOperator.del(MOBILE_SMSCODE + ":" + mobile);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users, usersVO);
        usersVO.setUserToken(uToken);
        return GraceJSONResult.ok(usersVO);
    }

    @ApiOperation(value = "用户一键退出")
    @PostMapping("/logout")
    public GraceJSONResult doLogout(@RequestParam String userId, HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse) {
        log.info(userId);
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        return GraceJSONResult.ok();
    }

}
