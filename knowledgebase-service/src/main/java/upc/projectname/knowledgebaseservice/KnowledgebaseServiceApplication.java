package upc.projectname.knowledgebaseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import upc.projectname.upccommon.security.config.MySecurityConfig;

@SpringBootApplication
@Import(MySecurityConfig.class)
public class KnowledgebaseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowledgebaseServiceApplication.class, args);
    }

}
