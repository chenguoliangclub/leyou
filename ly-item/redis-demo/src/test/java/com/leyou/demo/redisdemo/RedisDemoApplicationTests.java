package com.leyou.demo.redisdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;

@SpringBootTest
class RedisDemoApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("test","hello world");
        String test = redisTemplate.opsForValue().get("test");
        System.out.println(test);
        BoundHashOperations<String, Object, Object> user = redisTemplate.boundHashOps("user");
/*        user.put("name","cgl");
        user.put("age","55");*/
        Map<Object, Object> entries = user.entries();
        System.out.println(entries);
    }

}
