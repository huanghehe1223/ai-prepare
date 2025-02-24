package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Project;

import java.util.List;

@FeignClient(name = "project-service", contextId = "projectClient")
public interface ProjectClient {

    @GetMapping("/project/{id}")
    Result<Project> getProject(@PathVariable Integer id);

    @PostMapping("/project/batch")
    Result<List<Project>> getProjectByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/project/class/{classId}")
    Result<List<Project>> getProjectsByClassId(@PathVariable Integer classId);

    @PostMapping("/project")
    Result<Boolean> saveProject(@RequestBody Project project);

    @PutMapping("/project")
    Result<Boolean> updateProject(@RequestBody Project project);

    @DeleteMapping("/project/{id}")
    Result<Boolean> deleteProject(@PathVariable Integer id);
} 