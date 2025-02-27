package upc.projectname.exerciseservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.RecommendAnswerRecord;
import upc.projectname.exerciseservice.service.RecommendAnswerRecordService;

import java.util.List;

@Tag(name = "推荐答题记录管理接口")
@RestController
@RequestMapping("/recommendanswerrecord")
@RequiredArgsConstructor
public class RecommendAnswerRecordController {

    private final RecommendAnswerRecordService recommendAnswerRecordService;

    @Operation(summary = "根据ID查询推荐答题记录")
    @GetMapping("/{id}")
    public Result<RecommendAnswerRecord> getRecommendAnswerRecord(@PathVariable Integer id) {
        RecommendAnswerRecord record = recommendAnswerRecordService.getRecommendAnswerRecordById(id);
        return record != null ? Result.success(record) : Result.error("推荐答题记录不存在");
    }

    @Operation(summary = "根据ID批量查询推荐答题记录")
    @PostMapping("/batch")
    public Result<List<RecommendAnswerRecord>> getRecommendAnswerRecordByIds(@RequestBody List<Integer> ids) {
        List<RecommendAnswerRecord> records = recommendAnswerRecordService.getRecommendAnswerRecordByIds(ids);
        return records != null && !records.isEmpty() ? 
                Result.success(records) : 
                Result.error("未找到推荐答题记录信息");
    }
    
    @Operation(summary = "根据题目ID查询推荐答题记录")
    @GetMapping("/question/{questionId}")
    public Result<List<RecommendAnswerRecord>> getRecommendAnswerRecordsByQuestionId(@PathVariable Integer questionId) {
        List<RecommendAnswerRecord> records = recommendAnswerRecordService.getRecommendAnswerRecordsByQuestionId(questionId);
        return records != null && !records.isEmpty() ? 
                Result.success(records) : 
                Result.error("未找到该题目的推荐答题记录");
    }

    @Operation(summary = "新增推荐答题记录")
    @PostMapping
    public Result<Boolean> saveRecommendAnswerRecord(@RequestBody RecommendAnswerRecord record) {
        return recommendAnswerRecordService.saveRecommendAnswerRecord(record) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新推荐答题记录")
    @PutMapping
    public Result<Boolean> updateRecommendAnswerRecord(@RequestBody RecommendAnswerRecord record) {
        return recommendAnswerRecordService.updateRecommendAnswerRecord(record) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除推荐答题记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRecommendAnswerRecord(@PathVariable Integer id) {
        return recommendAnswerRecordService.deleteRecommendAnswerRecord(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }

    @Operation(summary = "根据groupID分页条件查询学生的推荐习题答题结果(业务)")
    @PostMapping("/student/searchRecommendAnswerResult")
    public Result<IPage<StudentAnswerResult>> searchAnswerRecord(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam("questionGroupId") Integer questionGroupId,
            @RequestParam(value = "questionType",required = false) String questionType
    ){
        IPage<StudentAnswerResult> studentRecommendAnswerResultIPage = recommendAnswerRecordService.searchRecommendAnswerRecord(current, size,  questionGroupId,questionType);
        return Result.success(studentRecommendAnswerResultIPage);
    }


} 