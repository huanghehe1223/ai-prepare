package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("user-class-service")
public interface StudentClient {

    @GetMapping("/student")
    String getStudent();

}
