package upc.projectname.upccommon.api.config;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableFeignClients(basePackages= "upc.projectname.upccommon.api.client")
public class OpenFeignConfig {
}
