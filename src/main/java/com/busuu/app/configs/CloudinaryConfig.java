package com.busuu.app.configs;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {
    private final String CLOUD_NAME = "didu61lbz";
    private final String API_KEY = "394597577871926";
    private final String API_SECRET = "_q3GGWzgGYFjylrVPf29NLRH3OU";

    @Bean
    public Cloudinary cloudinary () {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        return new Cloudinary(config);
    }
}
