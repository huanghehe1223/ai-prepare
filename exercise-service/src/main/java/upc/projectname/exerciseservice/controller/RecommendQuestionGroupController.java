package upc.projectname.exerciseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.RecommendQuestionGroup;
import upc.projectname.exerciseservice.service.RecommendQuestionGroupService;

import java.util.List;

@Tag(name = "推荐题目组管理接口")
@RestController
@RequestMapping("/recommendquestiongroup")
@RequiredArgsConstructor
public class RecommendQuestionGroupController {

    private final RecommendQuestionGroupService recommendQuestionGroupService;

    @Operation(summary = "根据ID查询推荐题目组")
    @GetMapping("/{id}")
    public Result<RecommendQuestionGroup> getRecommendQuestionGroup(@PathVariable Integer id) {
        RecommendQuestionGroup group = recommendQuestionGroupService.getRecommendQuestionGroupById(id);
        return group != null ? Result.success(group) : Result.error("推荐题目组不存在");
    }

    @Operation(summary = "根据ID批量查询推荐题目组")
    @PostMapping("/batch")
    public Result<List<RecommendQuestionGroup>> getRecommendQuestionGroupByIds(@RequestBody List<Integer> ids) {
        List<RecommendQuestionGroup> groups = recommendQuestionGroupService.getRecommendQuestionGroupByIds(ids);
        return groups != null && !groups.isEmpty() ? 
                Result.success(groups) : 
                Result.error("未找到推荐题目组信息");
    }
    
    @Operation(summary = "根据学生ID查询推荐题目组")
    @GetMapping("/student/{studentId}")
    public Result<List<RecommendQuestionGroup>> getRecommendQuestionGroupsByStudentId(@PathVariable Integer studentId) {
        List<RecommendQuestionGroup> groups = recommendQuestionGroupService.getRecommendQuestionGroupsByStudentId(studentId);
        return groups != null && !groups.isEmpty() ? 
                Result.success(groups) : 
                Result.error("未找到该学生的推荐题目组");
    }

    @Operation(summary = "新增推荐题目组")
    @PostMapping
    public Result<Boolean> saveRecommendQuestionGroup(@RequestBody RecommendQuestionGroup group) {
        return recommendQuestionGroupService.saveRecommendQuestionGroup(group) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新推荐题目组")
    @PutMapping
    public Result<Boolean> updateRecommendQuestionGroup(@RequestBody RecommendQuestionGroup group) {
        return recommendQuestionGroupService.updateRecommendQuestionGroup(group) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除推荐题目组")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRecommendQuestionGroup(@PathVariable Integer id) {
        return recommendQuestionGroupService.deleteRecommendQuestionGroup(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }

    @Operation(summary = "做完题目后提交推荐题目组（业务，token）")
    @PostMapping("/submit")
    public Result<Boolean> submitRecommendQuestionGroup(@RequestParam Integer projectId,@RequestParam Integer studentId) {
        return recommendQuestionGroupService.submitRecommendQuestionGroup(projectId,studentId) ?
                Result.success(true, "提交成功") :
                Result.error("提交失败");
    }
} 