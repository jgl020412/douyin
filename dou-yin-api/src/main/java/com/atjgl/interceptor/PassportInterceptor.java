package com.atjgl.interceptor;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.exception.GraceException;
import com.atjgl.grace.result.ResponseStatusEnum;
import com.atjgl.util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 小亮
 * 通信证拦截器
 **/

@Slf4j
public class PassportInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        // 获取请求的IP地址，然后看redis中是否存在
        String ip = IPUtil.getRequestIp(request);
        boolean isExist = redisOperator.keyIsExist(MOBILE_SMSCODE + ":" + ip);
        if (isExist) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        /**
         * false表示请求拦截
         * true表示请求放行
         */
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
