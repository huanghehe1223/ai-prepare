package upc.projectname.upcapi.config;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableFeignClients(basePackages= "upc.projectname.upcapi.client")
public class OpenFeignConfig {
}
