package com.megvii.sng.dzh.webdemo.interceptor;

import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yihongzhi
 * @version 1.0
 * @date 2019-11-25 15:15
 */
@Data
public class RedisRateLimiter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private int permitsPerUnit;

    private static final int PERIOD_SECOND_TTL = 10;

    public RedisRateLimiter(RedisTemplate redisTemplate, int permitsPerUnit) {
        this.redisTemplate = redisTemplate;
        this.permitsPerUnit = permitsPerUnit;
    }

    /**
     * @Author chenyisheng
     * @Date 2019/1/29 10:41
     * @Description 获取令牌
     * @Version 1.0
     **/
    public boolean acquire(String keyPrefix) {
        boolean rtv = false;

        //S_L_000008:timestamp
        String keyName = getKeyNameForSecond(keyPrefix);

        List<String> keys = new ArrayList<>();
        keys.add(keyName);

        List<String> argvs = new ArrayList<>();

        //10秒过期
        argvs.add(String.valueOf(PERIOD_SECOND_TTL));
        argvs.add(String.valueOf(permitsPerUnit));

        //incr当前key的值
        //如果计数为1则设置expire
        //如果不为1则比较是否超过限制
        Long val = evalScript(new SecondScript(), keys, argvs);
        rtv = (val > 0);

        return rtv;
    }

    private String getKeyNameForSecond(String keyPrefix) {
        String keyName = keyPrefix + ":" + getRedisTime() / 1000;
        return keyName;
    }

    private long getRedisTime() {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.time();
            }
        });
    }

    private long evalScript(RedisScript<Long> script, List<String> keys, List<String> args) {
        return redisTemplate.execute(script, keys, args.toArray());
    }

    private static class SecondScript implements RedisScript<Long> {
        //key  10 60
        /**
         * 1.对当前的key 计数增加1 如果计数==1则说明是第一创建，设置一个有效期 如果不是第一次创建则比较是否超过了设置的值
         */
        private static final String LUA_SECOND_SCRIPT = " local current; "
                + " current = redis.call('incr',KEYS[1]); "
                + " if tonumber(current) == 1 then "
                + " 	redis.call('expire',KEYS[1],ARGV[1]); "
                + "     return 1; "
                + " else"
                + " 	if tonumber(current) <= tonumber(ARGV[2]) then "
                + "     	return 1; "
                + "		else "
                + "			return -1; "
                + "   end "
                + " end ";

        @Override
        public String getSha1() {
            return DigestUtils.sha1Hex(LUA_SECOND_SCRIPT);
        }

        @Override
        public Class<Long> getResultType() {
            return Long.class;
        }

        @Override
        public String getScriptAsString() {
            return LUA_SECOND_SCRIPT;
        }
    }
}
