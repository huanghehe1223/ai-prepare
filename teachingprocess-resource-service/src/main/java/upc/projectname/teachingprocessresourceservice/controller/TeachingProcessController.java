package upc.projectname.teachingprocessresourceservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.TeachingProcess;
import upc.projectname.teachingprocessresourceservice.service.TeachingProcessService;

import java.util.List;

@Tag(name = "教学过程管理接口")
@RestController
@RequestMapping("/teachingprocess")
@RequiredArgsConstructor
public class TeachingProcessController {

    private final TeachingProcessService teachingProcessService;

    @Operation(summary = "根据ID查询教学过程")
    @GetMapping("/{id}")
    public Result<TeachingProcess> getTeachingProcess(@PathVariable Integer id) {
        TeachingProcess teachingProcess = teachingProcessService.getTeachingProcessById(id);
        return teachingProcess != null ? Result.success(teachingProcess) : Result.error("教学过程不存在");
    }

    @Operation(summary = "根据ID批量查询教学过程")
    @PostMapping("/batch")
    public Result<List<TeachingProcess>> getTeachingProcessByIds(@RequestBody List<Integer> ids) {
        List<TeachingProcess> processes = teachingProcessService.getTeachingProcessByIds(ids);
        return processes != null && !processes.isEmpty() ? 
                Result.success(processes) : 
                Result.error("未找到教学过程信息");
    }
    
    @Operation(summary = "根据项目ID查询教学过程")
    @GetMapping("/project/{projectId}")
    public Result<List<TeachingProcess>> getTeachingProcessByProjectId(@PathVariable Integer projectId) {
        List<TeachingProcess> processes = teachingProcessService.getTeachingProcessByProjectId(projectId);
        return processes != null && !processes.isEmpty() ? 
                Result.success(processes) : 
                Result.error("未找到该项目的教学过程信息");
    }

    @Operation(summary = "新增教学过程")
    @PostMapping
    public Result<Boolean> saveTeachingProcess(@RequestBody TeachingProcess teachingProcess) {
        return teachingProcessService.saveTeachingProcess(teachingProcess) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新教学过程")
    @PutMapping
    public Result<Boolean> updateTeachingProcess(@RequestBody TeachingProcess teachingProcess) {
        return teachingProcessService.updateTeachingProcess(teachingProcess) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除教学过程")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTeachingProcess(@PathVariable Integer id) {
        return teachingProcessService.deleteTeachingProcess(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }
} 