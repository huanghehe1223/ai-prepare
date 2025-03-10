package upc.projectname.exerciseservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.Question;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.AnswerRecord;
import upc.projectname.exerciseservice.service.AnswerRecordService;
import upc.projectname.upccommon.domain.po.Student;

import java.util.List;

@Tag(name = "答题记录管理接口")
@RestController
@RequestMapping("/answerrecord")
@RequiredArgsConstructor
public class AnswerRecordController {

    private final AnswerRecordService answerRecordService;

    @Operation(summary = "根据ID查询答题记录")
    @GetMapping("/{id}")
    public Result<AnswerRecord> getAnswerRecord(@PathVariable Integer id) {
        AnswerRecord record = answerRecordService.getAnswerRecordById(id);
        return record != null ? Result.success(record) : Result.error("答题记录不存在");
    }

    @Operation(summary = "根据ID批量查询答题记录")
    @PostMapping("/batch")
    public Result<List<AnswerRecord>> getAnswerRecordByIds(@RequestBody List<Integer> ids) {
        List<AnswerRecord> records = answerRecordService.getAnswerRecordByIds(ids);
        return records != null && !records.isEmpty() ?
                Result.success(records) :
                Result.error("未找到答题记录信息");
    }

    @Operation(summary = "根据学生ID查询答题记录")
    @GetMapping("/student/{studentId}")
    public Result<List<AnswerRecord>> getAnswerRecordsByStudentId(@PathVariable Integer studentId) {
        List<AnswerRecord> records = answerRecordService.getAnswerRecordsByStudentId(studentId);
        return records != null && !records.isEmpty() ?
                Result.success(records) :
                Result.error("未找到该学生的答题记录");
    }

    @Operation(summary = "根据题目ID查询答题记录")
    @GetMapping("/question/{questionId}")
    public Result<List<AnswerRecord>> getAnswerRecordsByQuestionId(@PathVariable Integer questionId) {
        List<AnswerRecord> records = answerRecordService.getAnswerRecordsByQuestionId(questionId);
        return records != null && !records.isEmpty() ?
                Result.success(records) :
                Result.error("未找到该题目的答题记录");
    }

    @Operation(summary = "根据学生ID和题目ID查询答题记录")
    @GetMapping("/student/{studentId}/question/{questionId}")
    public Result<List<AnswerRecord>> getAnswerRecordsByStudentAndQuestion(
            @PathVariable Integer studentId,
            @PathVariable Integer questionId) {
        List<AnswerRecord> records = answerRecordService.getAnswerRecordsByStudentAndQuestion(studentId, questionId);
        return records != null && !records.isEmpty() ?
                Result.success(records) :
                Result.error("未找到相关答题记录");
    }

    @Operation(summary = "新增答题记录")
    @PostMapping
    public Result<AnswerRecord> saveAnswerRecord(@RequestBody AnswerRecord record) {
        return Result.success(record);
//        return answerRecordService.saveAnswerRecord(record) ?
//                Result.success(true, "添加成功") :
//                Result.error("添加失败");
    }

    @Operation(summary = "更新答题记录")
    @PutMapping
    public Result<Boolean> updateAnswerRecord(@RequestBody AnswerRecord record) {
        return answerRecordService.updateAnswerRecord(record) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除答题记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAnswerRecord(@PathVariable Integer id) {
        return answerRecordService.deleteAnswerRecord(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }


    @Operation(summary = "根据学生ID和题目组ID分页条件查询学生的答题结果(业务，token)")
    @PostMapping("/student/searchAnswerResult")
    public Result<IPage<StudentAnswerResult>> searchAnswerRecord(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam("studentId") Integer studentId,
            @RequestParam("questionGroupId") Integer questionGroupId,
            @RequestParam(value = "questionType", required = false) String questionType
    ) {
        IPage<StudentAnswerResult> studentAnswerResultIPage = answerRecordService.searchAnswerRecord(current, size, studentId, questionGroupId, questionType);
        return Result.success(studentAnswerResultIPage);
    }


    @Operation(summary = "批量保存或更新答题记录(业务)")
    @PostMapping("/batch/save")
    public Result<Boolean> saveOrUpdateBatchAnswerRecords(@RequestBody List<AnswerRecord> records, @RequestParam(required = false, value = "studentId") Integer studentId) {
        return answerRecordService.saveOrUpdateBatchAnswerRecords(records, studentId) ?
                Result.success(true, "批量保存成功") :
                Result.error("批量保存失败");
    }


    @Operation(summary = "根据习题组id和学生id（可选）查询答题记录（业务、token）")
    @PostMapping("/searchByQuestionGroupId")
    public Result<List<AnswerRecord>> searchByQuestionGroupId(@RequestParam("questionGroupId") Integer questionGroupId,
                                                              @RequestParam(value = "studentId", required = false) Integer studentId) {
        List<AnswerRecord> answerRecords = answerRecordService.searchByQuestionGroupId(questionGroupId, studentId);
        return Result.success(answerRecords);
    }

//    @Operation(summary = "统计某个题目的答题正确率（业务）")
//    @PostMapping("/statisticsStudentAnswerAccuracy")

    @Operation(summary = "根据学生ID和习题组ID（可选）查询答题时间最长/短的答题记录（业务、token）")
    @PostMapping("/searchByStudentIdAndQuestionGroupId")
    public Result<IPage<AnswerRecord>> searchByStudentIdAndQuestionGroupId(@RequestParam("studentId") Integer studentId,
                                                                           @RequestParam(defaultValue = "1") Integer current,
                                                                           @RequestParam(defaultValue = "10") Integer size,
                                                                           @RequestParam(value = "answerResult", defaultValue = "Right") String answerResult,       // 默认答对
                                                                           @RequestParam(value = "sortType", defaultValue = "asc") String sortType,         // 默认升序
                                                                           @RequestParam(value = "questionGroupId", required = false) Integer questionGroupId) {
        Page<AnswerRecord> page = new Page<>(current, size);
        IPage<AnswerRecord> answerRecords = answerRecordService.searchByStudentIdAndQuestionGroupId(studentId, questionGroupId, page, sortType,answerResult);
        return Result.success(answerRecords);
    }

    @Operation(summary = "获取某个习题组的平均做题时间（业务、token）")
    @GetMapping("/getAverageTimeByGroupId")
    public Result<Double> getAverageTimeByGroupId(@RequestParam("questionGroupId") Integer questionGroupId) {
        Double averageTime = answerRecordService.getAverageTimeByGroupId(questionGroupId);
        return Result.success(averageTime);
    }

}