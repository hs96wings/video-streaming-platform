package io.github.hs96wings.streaming_server.common.configs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WebConfigCorsTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("CORS 설정: 허용된 Origin 목록에 포함된 경우 OPTIONS 요청이 허용되어야 한다")
    void corsConfig_shouldAllowOptionsRequest_fromAllowedOrigin() throws Exception {
        mockMvc.perform(options("/api/test/me")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("CORS 설정: 허용되지 않는 Origin 목록에 포함된 경우 403을 반환해야 한다")
    void corsConfig_shouldReturnForbiddenForDisallowedOrigin() throws Exception {
        mockMvc.perform(options("/api/test/me")
                        .header("Origin", "http://example.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());

    }
}
