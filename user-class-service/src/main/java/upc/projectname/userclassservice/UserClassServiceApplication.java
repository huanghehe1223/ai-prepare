package upc.projectname.userclassservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import upc.projectname.upccommon.security.config.MySecurityConfig;

@SpringBootApplication
@Import(MySecurityConfig.class)
public class UserClassServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserClassServiceApplication.class, args);
    }

}
