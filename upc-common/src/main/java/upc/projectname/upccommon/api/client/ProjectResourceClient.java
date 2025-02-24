package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.ProjectResource;

import java.util.List;

@FeignClient(name = "teachingprocess-resource-service", contextId = "projectResourceClient")
public interface ProjectResourceClient {

    @GetMapping("/projectresource/{id}")
    Result<ProjectResource> getProjectResource(@PathVariable Integer id);

    @PostMapping("/projectresource/batch")
    Result<List<ProjectResource>> getProjectResourceByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/projectresource/project/{projectId}")
    Result<List<ProjectResource>> getProjectResourcesByProjectId(@PathVariable Integer projectId);

    @PostMapping("/projectresource")
    Result<Boolean> saveProjectResource(@RequestBody ProjectResource projectResource);

    @PutMapping("/projectresource")
    Result<Boolean> updateProjectResource(@RequestBody ProjectResource projectResource);

    @DeleteMapping("/projectresource/{id}")
    Result<Boolean> deleteProjectResource(@PathVariable Integer id);
} 