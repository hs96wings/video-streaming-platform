package io.github.hs96wings.streaming_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VideoStreamingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoStreamingServerApplication.class, args);
	}

}
