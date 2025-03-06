package upc.projectname.exerciseservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.RecommendQuestion;
import upc.projectname.exerciseservice.service.RecommendQuestionService;

import java.util.List;

@Tag(name = "推荐题目管理接口")
@RestController
@RequestMapping("/recommendquestion")
@RequiredArgsConstructor
public class RecommendQuestionController {

    private final RecommendQuestionService recommendQuestionService;

    @Operation(summary = "根据ID查询推荐题目")
    @GetMapping("/{id}")
    public Result<RecommendQuestion> getRecommendQuestion(@PathVariable Integer id) {
        RecommendQuestion question = recommendQuestionService.getRecommendQuestionById(id);
        return question != null ? Result.success(question) : Result.error("推荐题目不存在");
    }

    @Operation(summary = "根据ID批量查询推荐题目")
    @PostMapping("/batch")
    public Result<List<RecommendQuestion>> getRecommendQuestionByIds(@RequestBody List<Integer> ids) {
        List<RecommendQuestion> questions = recommendQuestionService.getRecommendQuestionByIds(ids);
        return questions != null && !questions.isEmpty() ? 
                Result.success(questions) : 
                Result.error("未找到推荐题目信息");
    }
    
    @Operation(summary = "根据题目组ID查询推荐题目")
    @GetMapping("/group/{groupId}")
    public Result<List<RecommendQuestion>> getRecommendQuestionsByGroupId(@PathVariable Integer groupId) {
        List<RecommendQuestion> questions = recommendQuestionService.getRecommendQuestionsByGroupId(groupId);
        return questions != null && !questions.isEmpty() ? 
                Result.success(questions) : 
                Result.error("未找到该题目组的推荐题目");
    }

    @Operation(summary = "新增推荐题目")
    @PostMapping
    public Result<Boolean> saveRecommendQuestion(@RequestBody RecommendQuestion question) {
        return recommendQuestionService.saveRecommendQuestion(question) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新推荐题目")
    @PutMapping
    public Result<Boolean> updateRecommendQuestion(@RequestBody RecommendQuestion question) {
        return recommendQuestionService.updateRecommendQuestion(question) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除推荐题目")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRecommendQuestion(@PathVariable Integer id) {
        return recommendQuestionService.deleteRecommendQuestion(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }

    @Operation(summary = "根据groupId分页查询推荐习题（业务）")
    @GetMapping("/group/recommend")
    public Result<IPage<RecommendQuestion>> getRecommendQuestionsByGroupId(@RequestParam Integer groupId,
                                                                           @RequestParam(required = false) String questionType,
                                                                           @RequestParam(defaultValue = "1") Integer current,
                                                                           @RequestParam(defaultValue = "10") Integer size) {
        Page<RecommendQuestion> page = new Page<>(current, size);
        IPage<RecommendQuestion> questions = recommendQuestionService.getRecommendQuestionsByGroupIdAndPage(groupId, page, questionType);
        return questions != null && !questions.getRecords().isEmpty() ?
                Result.success(questions) :
                Result.error("未找到该题目组的推荐题目");
    }
}