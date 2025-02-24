package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.TeachingProcess;

import java.util.List;

@FeignClient(name = "teachingprocess-resource-service", contextId = "teachingProcessClient")
public interface TeachingProcessClient {

    @GetMapping("/teachingprocess/{id}")
    Result<TeachingProcess> getTeachingProcess(@PathVariable Integer id);

    @PostMapping("/teachingprocess/batch")
    Result<List<TeachingProcess>> getTeachingProcessByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/teachingprocess/project/{projectId}")
    Result<List<TeachingProcess>> getTeachingProcessByProjectId(@PathVariable Integer projectId);

    @PostMapping("/teachingprocess")
    Result<Boolean> saveTeachingProcess(@RequestBody TeachingProcess teachingProcess);

    @PutMapping("/teachingprocess")
    Result<Boolean> updateTeachingProcess(@RequestBody TeachingProcess teachingProcess);

    @DeleteMapping("/teachingprocess/{id}")
    Result<Boolean> deleteTeachingProcess(@PathVariable Integer id);
} 