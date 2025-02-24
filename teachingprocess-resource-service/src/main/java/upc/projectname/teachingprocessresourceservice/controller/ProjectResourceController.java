package upc.projectname.teachingprocessresourceservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.ProjectResource;
import upc.projectname.teachingprocessresourceservice.service.ProjectResourceService;

import java.util.List;

@Tag(name = "项目资源管理接口")
@RestController
@RequestMapping("/projectresource")
@RequiredArgsConstructor
public class ProjectResourceController {

    private final ProjectResourceService projectResourceService;

    @Operation(summary = "根据ID查询项目资源")
    @GetMapping("/{id}")
    public Result<ProjectResource> getProjectResource(@PathVariable Integer id) {
        ProjectResource projectResource = projectResourceService.getProjectResourceById(id);
        return projectResource != null ? Result.success(projectResource) : Result.error("项目资源不存在");
    }

    @Operation(summary = "根据ID批量查询项目资源")
    @PostMapping("/batch")
    public Result<List<ProjectResource>> getProjectResourceByIds(@RequestBody List<Integer> ids) {
        List<ProjectResource> resources = projectResourceService.getProjectResourceByIds(ids);
        return resources != null && !resources.isEmpty() ? 
                Result.success(resources) : 
                Result.error("未找到项目资源信息");
    }
    
    @Operation(summary = "根据项目ID查询项目资源")
    @GetMapping("/project/{projectId}")
    public Result<List<ProjectResource>> getProjectResourcesByProjectId(@PathVariable Integer projectId) {
        List<ProjectResource> resources = projectResourceService.getProjectResourcesByProjectId(projectId);
        return resources != null && !resources.isEmpty() ? 
                Result.success(resources) : 
                Result.error("未找到该项目的资源信息");
    }

    @Operation(summary = "新增项目资源")
    @PostMapping
    public Result<Boolean> saveProjectResource(@RequestBody ProjectResource projectResource) {
        return projectResourceService.saveProjectResource(projectResource) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新项目资源")
    @PutMapping
    public Result<Boolean> updateProjectResource(@RequestBody ProjectResource projectResource) {
        return projectResourceService.updateProjectResource(projectResource) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除项目资源")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProjectResource(@PathVariable Integer id) {
        return projectResourceService.deleteProjectResource(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }
} 