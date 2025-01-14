package upc.projectname.upcredisstarter.redisutils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisUtils {

    //这个bean对象来自于主项目（微服务项目）springboot自动配置的StringRedisTemplate
    @Autowired
    public StringRedisTemplate stringRedisTemplate;



    public Boolean setObject(String key, Object object) {
        if (object == null) {
            return false;
        }
        try {
            String jsonString = FastjsonUtils.toJsonString(object);
            stringRedisTemplate.opsForValue().set(key, jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //setObjectWithSecond,增加过期时间，传参为秒
    public Boolean setObjectWithSecond(String key, Object object, Integer expireTime) {
        if (object == null) {
            return false;
        }
        try {
            String jsonString = FastjsonUtils.toJsonString(object);
            stringRedisTemplate.opsForValue().set(key, jsonString, expireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //setObjectWithMinute,增加过期时间，传参为分钟
    public Boolean setObjectWithMinute(String key, Object object, Integer expireTime) {
        if (object == null) {
            return false;
        }
        try {
            String jsonString = FastjsonUtils.toJsonString(object);
            stringRedisTemplate.opsForValue().set(key, jsonString, expireTime, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }





    //获取Object
    public <T> T getObject(String key, Class<T> clazz) {
        try {
            String jsonString = stringRedisTemplate.opsForValue().get(key);
            return FastjsonUtils.parseObject(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }
    //获取list
    public <T> List<T> getList(String key, Class<T> clazz) {
        try {
            String jsonString = stringRedisTemplate.opsForValue().get(key);
            return FastjsonUtils.parseArray(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    //设置String
    public Boolean setString(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //setStringWithSecond,增加过期时间，传参为秒
    public Boolean setStringWithSecond(String key, String value, Integer expireTime) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //setStringWithMinute,增加过期时间，传参为分钟
    public Boolean setStringWithMinute(String key, String value, Integer expireTime) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    //getString 获取String
    public String getString(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }

    //判断key是否存在
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }


    //删除一个key-value
    public Boolean deleteKey(String key) {
        return stringRedisTemplate.delete(key);
    }




}
