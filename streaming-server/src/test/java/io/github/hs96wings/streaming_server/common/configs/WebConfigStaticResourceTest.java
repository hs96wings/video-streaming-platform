package io.github.hs96wings.streaming_server.common.configs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.upload.dir=uploads-test" // 임시 디렉토리
})
public class WebConfigStaticResourceTest {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() throws IOException {
        // upload-test/sample.txt 생성
        Path uploadPath = Paths.get("uploads-test");
        Files.createDirectories(uploadPath);
        Files.write(uploadPath.resolve("sample.txt"), "테스트입니다".getBytes());
    }

    @Test
    @DisplayName("/uploads/sample.txt 경로로 정적 리소스를 성공적으로 로드해야 한다")
    void shouldServeStaticResource() throws Exception {
        mockMvc.perform(get("/uploads/sample.txt"))
                .andExpect(status().isOk())
                .andExpect(content().bytes("테스트입니다".getBytes(StandardCharsets.UTF_8)));
    }

    @AfterEach
    void cleanUp() throws Exception {
        Files.deleteIfExists(Paths.get("upload-test/sample.txt"));
        Files.deleteIfExists(Paths.get("upload-test"));
    }
}
