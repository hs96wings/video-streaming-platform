package io.github.hs96wings.streaming_server.common.configs;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class RedisConnectIntegrationTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("Redis에 데이터를 쓰고 읽을 수 있어야 한다")
    void redis_writeAndRead_shouldSucceed() {
        redisTemplate.opsForValue().set("testKey", "testValue");
        String result = redisTemplate.opsForValue().get("testKey");

        assertThat(result).isEqualTo("testValue");
    }
}
