package upc.projectname.exerciseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.QuestionGroupStatus;
import upc.projectname.exerciseservice.service.QuestionGroupStatusService;

import java.util.List;

@Tag(name = "题目组状态管理接口")
@RestController
@RequestMapping("/questiongroupstatus")
@RequiredArgsConstructor
public class QuestionGroupStatusController {

    private final QuestionGroupStatusService statusService;

    @Operation(summary = "根据ID查询题目组状态")
    @GetMapping("/{id}")
    public Result<QuestionGroupStatus> getStatus(@PathVariable Integer id) {
        QuestionGroupStatus status = statusService.getQuestionGroupStatusById(id);
        return status != null ? Result.success(status) : Result.error("状态不存在");
    }

    @Operation(summary = "根据ID批量查询题目组状态")
    @PostMapping("/batch")
    public Result<List<QuestionGroupStatus>> getStatusByIds(@RequestBody List<Integer> ids) {
        List<QuestionGroupStatus> statuses = statusService.getQuestionGroupStatusByIds(ids);
        return statuses != null && !statuses.isEmpty() ? 
                Result.success(statuses) : 
                Result.error("未找到状态信息");
    }
    
    @Operation(summary = "根据学生ID查询题目组状态")
    @GetMapping("/student/{studentId}")
    public Result<List<QuestionGroupStatus>> getStatusByStudentId(@PathVariable Integer studentId) {
        List<QuestionGroupStatus> statuses = statusService.getStatusByStudentId(studentId);
        return statuses != null && !statuses.isEmpty() ? 
                Result.success(statuses) : 
                Result.error("未找到该学生的状态信息");
    }
    
    @Operation(summary = "根据题目组ID查询状态")
    @GetMapping("/group/{groupId}")
    public Result<List<QuestionGroupStatus>> getStatusByGroupId(@PathVariable Integer groupId) {
        List<QuestionGroupStatus> statuses = statusService.getStatusByGroupId(groupId);
        return statuses != null && !statuses.isEmpty() ? 
                Result.success(statuses) : 
                Result.error("未找到该题目组的状态信息");
    }

    @Operation(summary = "根据题目组ID和学生ID查询状态")
    @GetMapping("/group/{groupId}/student/{studentId}")
    public Result<List<QuestionGroupStatus>> getStatusByGroupIdAndStudent(@PathVariable Integer groupId, @PathVariable Integer studentId) {
        List<QuestionGroupStatus> statuses = statusService.getStatusByGroupIdAndStudentId(groupId, studentId);
        return statuses != null && !statuses.isEmpty() ? 
                Result.success(statuses) : 
                Result.error("未找到相关状态信息");
    }

    @Operation(summary = "新增题目组状态")
    @PostMapping
    public Result<Boolean> saveStatus(@RequestBody QuestionGroupStatus status) {
        return statusService.saveQuestionGroupStatus(status) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新题目组状态")
    @PutMapping
    public Result<Boolean> updateStatus(@RequestBody QuestionGroupStatus status) {
        return statusService.updateQuestionGroupStatus(status) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除题目组状态")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteStatus(@PathVariable Integer id) {
        return statusService.deleteQuestionGroupStatus(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }
} 