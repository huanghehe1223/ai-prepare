package upc.projectname.upcredisstarter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import upc.projectname.upcredisstarter.redisutils.RedisUtils;

@Configuration
public class RedisConfig {

    @Bean
    RedisUtils redisUtils() {
        return new RedisUtils();
    }


}
