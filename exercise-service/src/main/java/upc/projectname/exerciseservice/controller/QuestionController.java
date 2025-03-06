package upc.projectname.exerciseservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Question;
import upc.projectname.exerciseservice.service.QuestionService;

import java.util.List;

@Tag(name = "题目管理接口")
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "根据ID查询题目")
    @GetMapping("/{id}")
    public Result<Question> getQuestion(@PathVariable Integer id) {
        Question question = questionService.getQuestionById(id);
        return question != null ? Result.success(question) : Result.error("题目不存在");
    }

    @Operation(summary = "根据ID批量查询题目")
    @PostMapping("/batch")
    public Result<List<Question>> getQuestionByIds(@RequestBody List<Integer> ids) {
        List<Question> questions = questionService.getQuestionByIds(ids);
        return questions != null && !questions.isEmpty() ? 
                Result.success(questions) : 
                Result.error("未找到题目信息");
    }
    
    @Operation(summary = "根据题目组ID查询题目")
    @GetMapping("/group/{groupId}")
    public Result<List<Question>> getQuestionsByGroupId(@PathVariable Integer groupId) {
        List<Question> questions = questionService.getQuestionsByGroupId(groupId);
        return questions != null && !questions.isEmpty() ? 
                Result.success(questions) : 
                Result.error("未找到该题目组的题目信息");
    }

    @Operation(summary = "根据题目组ID分页查询题目（业务）")
    @PostMapping("/group/batch")
    public Result<IPage<Question>> getQuestionsByGroupIdAndPage(@RequestParam Integer groupId,
                                                                @RequestParam(defaultValue = "1") Integer current,
                                                                @RequestParam(defaultValue = "10") Integer size,
                                                                @RequestParam(required = false) String questionType) {
        Page<Question> page = new Page<>(current, size);
        IPage<Question> questions = questionService.getQuestionsByGroupIdAndPage(page, groupId,questionType);
        return Result.success(questions);
    }

    @Operation(summary = "新增题目")
    @PostMapping
    public Result<Boolean> saveQuestion(@RequestBody Question question) {
        return questionService.saveQuestion(question) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新题目信息")
    @PutMapping
    public Result<Boolean> updateQuestion(@RequestBody Question question) {
        return questionService.updateQuestion(question) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除题目")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteQuestion(@PathVariable Integer id) {
        return questionService.deleteQuestion(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }

    @Operation(summary = "批量增加习题（业务）")
    @PostMapping("/batch/add")
    public Result<Boolean> saveQuestions(@RequestBody List<Question> questions) {
        return questionService.saveQuestions(questions) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }
} 