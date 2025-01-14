package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import upc.projectname.upccommon.domain.po.Result;


@FeignClient("task-service")
public interface TaskClient {

    @GetMapping("/test")
    Result testTasks();
}





