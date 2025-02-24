package upc.projectname.projectservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.projectservice.service.ProjectService;

import java.util.List;

@Tag(name = "项目管理接口")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "根据ID查询项目")
    @GetMapping("/{id}")
    public Result<Project> getProject(@PathVariable Integer id) {
        Project project = projectService.getProjectById(id);
        return project != null ? Result.success(project) : Result.error("项目不存在");
    }

    @Operation(summary = "根据ID批量查询项目")
    @PostMapping("/batch")
    public Result<List<Project>> getProjectByIds(@RequestBody List<Integer> ids) {
        List<Project> projects = projectService.getProjectByIds(ids);
        return projects != null && !projects.isEmpty() ? 
                Result.success(projects) : 
                Result.error("未找到项目信息");
    }
    
    @Operation(summary = "根据班级ID查询项目")
    @GetMapping("/class/{classId}")
    public Result<List<Project>> getProjectsByClassId(@PathVariable Integer classId) {
        List<Project> projects = projectService.getProjectsByClassId(classId);
        return projects != null && !projects.isEmpty() ? 
                Result.success(projects) : 
                Result.error("未找到该班级的项目信息");
    }

    @Operation(summary = "新增项目")
    @PostMapping
    public Result<Boolean> saveProject(@RequestBody Project project) {
        return projectService.saveProject(project) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新项目信息")
    @PutMapping
    public Result<Boolean> updateProject(@RequestBody Project project) {
        return projectService.updateProject(project) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProject(@PathVariable Integer id) {
        return projectService.deleteProject(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }
} 