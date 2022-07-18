package com.atguigu.gulimall.product;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;


    @Test
    public void contextLoads(){ }

    @Test
    public void testRedis(){

        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "world");
        System.out.println(ops.get("hello"));
    }

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }




}
