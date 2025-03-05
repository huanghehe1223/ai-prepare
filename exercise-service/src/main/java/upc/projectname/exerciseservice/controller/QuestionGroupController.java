package upc.projectname.exerciseservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.QuestionGroup;
import upc.projectname.exerciseservice.service.QuestionGroupService;

import java.util.List;

@Tag(name = "题目组管理接口")
@RestController
@RequestMapping("/questiongroup")
@RequiredArgsConstructor
public class QuestionGroupController {

    private final QuestionGroupService questionGroupService;

    @Operation(summary = "根据ID查询题目组")
    @GetMapping("/{id}")
    public Result<QuestionGroup> getQuestionGroup(@PathVariable Integer id) {
        QuestionGroup questionGroup = questionGroupService.getQuestionGroupById(id);
        return questionGroup != null ? Result.success(questionGroup) : Result.error("题目组不存在");
    }

    @Operation(summary = "根据ID批量查询题目组")
    @PostMapping("/batch")
    public Result<List<QuestionGroup>> getQuestionGroupByIds(@RequestBody List<Integer> ids) {
        List<QuestionGroup> questionGroups = questionGroupService.getQuestionGroupByIds(ids);
        return questionGroups != null && !questionGroups.isEmpty() ? 
                Result.success(questionGroups) : 
                Result.error("未找到题目组信息");
    }
    
    @Operation(summary = "根据项目ID查询题目组")
    @GetMapping("/project/{projectId}")
    public Result<List<QuestionGroup>> getQuestionGroupsByProjectId(@PathVariable Integer projectId) {
        List<QuestionGroup> questionGroups = questionGroupService.getQuestionGroupsByProjectId(projectId);
        return questionGroups != null && !questionGroups.isEmpty() ? 
                Result.success(questionGroups) : 
                Result.error("未找到该项目的题目组信息");
    }

    @Operation(summary = "新增题目组（业务）")
    @PostMapping
    public Result<Boolean> saveQuestionGroup(@RequestBody QuestionGroup questionGroup) {
        return questionGroupService.saveQuestionGroup(questionGroup) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新题目组信息")
    @PutMapping
    public Result<Boolean> updateQuestionGroup(@RequestBody QuestionGroup questionGroup) {
        return questionGroupService.updateQuestionGroup(questionGroup) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除题目组")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteQuestionGroup(@PathVariable Integer id) {
        return questionGroupService.deleteQuestionGroup(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }


    @Operation(summary = "教师根据项目ID、发布情况和题目组类型查询题目组（业务）")
    @PostMapping("/searchByPage/batch")
    public Result<List<QuestionGroup>> searchQuestionGroupByPage(
            @RequestParam Integer projectId,
            @RequestParam(required = false) Integer groupStatus,
            @RequestParam(required = false) String groupType) {
        List<QuestionGroup> questionGroups = questionGroupService.searchQuestionGroupByPage(projectId, groupStatus, groupType);
        return !questionGroups.isEmpty() ? Result.success(questionGroups) : Result.error("未找到题目组信息");
    }

    @Operation(summary = "根据题目组id更新题目组状态，默认发布（业务）")
    @PutMapping("/updateStatus")
    public Result<Boolean> updateQuestionGroupStatus(@RequestParam Integer groupId,
                                                     @RequestParam(defaultValue = "1") Integer groupStatus) {
        return questionGroupService.updateQuestionGroupStatus(groupId, groupStatus) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }
} 