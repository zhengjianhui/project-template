package com.yzf.project.projectservice.config.redis.template;


import com.yzf.project.projectservice.config.redis.serializer.HessianSerializationRedisSerializer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author zhengjianhui on 18/3/16
 */
public class HessianRedisTemplate extends RedisTemplate<String, Object> {

    private HessianSerializationRedisSerializer hessianSerializationRedisSerializer;

    public HessianRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        setKeySerializer(stringSerializer);

        setHashKeySerializer(stringSerializer);

    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        setValueSerializer(hessianSerializationRedisSerializer);
        setHashValueSerializer(hessianSerializationRedisSerializer);
    }

    public HessianSerializationRedisSerializer getHessianSerializationRedisSerializer() {
        return hessianSerializationRedisSerializer;
    }

    public void setHessianSerializationRedisSerializer(HessianSerializationRedisSerializer hessianSerializationRedisSerializer) {
        this.hessianSerializationRedisSerializer = hessianSerializationRedisSerializer;
    }

}
