package com.megvii.sng.dzh.webdemo.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: renhongqiang
 * @Date: 2020/5/12 10:31 上午
 **/
@Slf4j
public class QpsInterceptor extends HandlerInterceptorAdapter {

    /**
     * 是否开启限流
     */
    @Value("${rate.limit.switch:false}")
    private boolean qpsSwitch;

    /**
     * 限流次数
     */
    @Value("${rate.limit.qps:100}")
    private Integer qpsLimit;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 保存计算器信息.
     */
    private Map<String, RedisRateLimiter> apiRateLimiter = new HashMap<>();

    private final String API_RATE_KEY = "ApiRateKey";
    private final String KEY_PREFIX = "S_L_";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        if (!qpsSwitch) {
            return true;
        }

        RedisRateLimiter limiter = apiRateLimiter.get(API_RATE_KEY);
        if (limiter == null || limiter.getPermitsPerUnit() != qpsLimit) {
            //不存或者速率有变化
            limiter = new RedisRateLimiter(redisTemplate, qpsLimit);
            apiRateLimiter.put(API_RATE_KEY, limiter);
        }

        //限制速率
        boolean rateAcquire = limiter.acquire(KEY_PREFIX + API_RATE_KEY);

        if (rateAcquire) {
            return true;
        } else {
            //超过速率限制
            log.warn("Service rate limit exceeded, please try again later.");
            try {
                response.setCharacterEncoding("utf-8");
                response.getWriter().write(JSON.toJSONString("Service rate limit exceeded"));
            } catch (IOException e) {
                log.error("io exception when writer error msg");
            } finally {
                try {
                    response.getWriter().close();
                } catch (IOException e) {
                    log.error("io exception when close response writer");
                }
            }
            return false;
        }
    }




}
