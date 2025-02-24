package upc.projectname.upccommon.api.config;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import upc.projectname.upccommon.api.interceptor.FeignUserHeaderInterceptor;


@Configuration
@EnableFeignClients(basePackages= "upc.projectname.upccommon.api.client", defaultConfiguration = OpenFeignConfig.class)
public class OpenFeignConfig {
    @Bean
    public FeignUserHeaderInterceptor feignUserHeaderInterceptor() {
        return new FeignUserHeaderInterceptor();
    }
}
