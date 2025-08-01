package io.github.hs96wings.streaming_server.common.configs;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class RedisConfigTest {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    @DisplayName("RedisConnectionFactory가 스프링 컨텍스트에 정상 등록되어야 한다")
    void redisConnectionFactoryShouldBePresent() {
        assertThat(redisConnectionFactory).isNotNull();
    }
}
