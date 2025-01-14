package upc.projectname.upcapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import upc.projectname.upcpojo.pojo.Result;

@FeignClient("task-service")
public interface TaskClient {

    @GetMapping("/test")
    Result testTasks();
}





