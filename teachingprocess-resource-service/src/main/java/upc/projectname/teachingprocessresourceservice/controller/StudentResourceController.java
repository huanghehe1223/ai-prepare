package upc.projectname.teachingprocessresourceservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.StudentResource;
import upc.projectname.teachingprocessresourceservice.service.StudentResourceService;

import java.util.List;

@Tag(name = "学生资源管理接口")
@RestController
@RequestMapping("/studentresource")
@RequiredArgsConstructor
public class StudentResourceController {

    private final StudentResourceService studentResourceService;

    @Operation(summary = "根据ID查询学生资源")
    @GetMapping("/{id}")
    public Result<StudentResource> getStudentResource(@PathVariable Integer id) {
        StudentResource studentResource = studentResourceService.getStudentResourceById(id);
        return studentResource != null ? Result.success(studentResource) : Result.error("学生资源不存在");
    }

    @Operation(summary = "根据ID批量查询学生资源")
    @PostMapping("/batch")
    public Result<List<StudentResource>> getStudentResourceByIds(@RequestBody List<Integer> ids) {
        List<StudentResource> resources = studentResourceService.getStudentResourceByIds(ids);
        return resources != null && !resources.isEmpty() ? 
                Result.success(resources) : 
                Result.error("未找到学生资源信息");
    }
    
    @Operation(summary = "根据学生ID查询资源")
    @GetMapping("/student/{studentId}")
    public Result<List<StudentResource>> getStudentResourcesByStudentId(@PathVariable Integer studentId) {
        List<StudentResource> resources = studentResourceService.getStudentResourcesByStudentId(studentId);
        return resources != null && !resources.isEmpty() ? 
                Result.success(resources) : 
                Result.error("未找到该学生的资源信息");
    }
    
    @Operation(summary = "根据项目ID查询学生资源")
    @GetMapping("/project/{projectId}")
    public Result<List<StudentResource>> getStudentResourcesByProjectId(@PathVariable Integer projectId) {
        List<StudentResource> resources = studentResourceService.getStudentResourcesByProjectId(projectId);
        return resources != null && !resources.isEmpty() ? 
                Result.success(resources) : 
                Result.error("未找到该项目的学生资源信息");
    }
    
    @Operation(summary = "根据学生ID和项目ID查询资源")
    @GetMapping("/student/{studentId}/project/{projectId}")
    public Result<List<StudentResource>> getStudentResourcesByStudentIdAndProjectId(
            @PathVariable Integer studentId,
            @PathVariable Integer projectId) {
        List<StudentResource> resources = studentResourceService.getStudentResourcesByStudentIdAndProjectId(studentId, projectId);
        return resources != null && !resources.isEmpty() ? 
                Result.success(resources) : 
                Result.error("未找到该学生在此项目中的资源信息");
    }

    @Operation(summary = "新增学生资源")
    @PostMapping
    public Result<Boolean> saveStudentResource(@RequestBody StudentResource studentResource) {
        return studentResourceService.saveStudentResource(studentResource) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新学生资源")
    @PutMapping
    public Result<Boolean> updateStudentResource(@RequestBody StudentResource studentResource) {
        return studentResourceService.updateStudentResource(studentResource) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除学生资源")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteStudentResource(@PathVariable Integer id) {
        return studentResourceService.deleteStudentResource(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }
} 