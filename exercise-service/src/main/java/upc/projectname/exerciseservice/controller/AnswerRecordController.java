package upc.projectname.exerciseservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
import upc.projectname.upccommon.domain.dto.AccuracyRateDTO;
import upc.projectname.upccommon.domain.dto.AverageScoreDTO;
import upc.projectname.upccommon.domain.dto.AverageTimeDTO;
import upc.projectname.upccommon.domain.dto.ExtremeAnswerTimeDTO;
import upc.projectname.upccommon.domain.dto.KnowledgePointScoreDTO;

import java.util.List;

@Tag(name = "答题记录管理接口")
@RestController
@RequestMapping("/answerrecord")
@RequiredArgsConstructor
public class AnswerRecordController {

    private final AnswerRecordService answerRecordService;


    @Operation(summary = "测试docker-compose")
    @GetMapping("/testdocker")
    public Result<String> testDocker() {
        return Result.success("docker-compose测试成功,version3-exercise");
    }


    @Operation(summary = "根据ID查询答题记录")
    @GetMapping("/{id}")
    public Result<AnswerRecord> getAnswerRecord(@PathVariable Integer id) {
        AnswerRecord record = answerRecordService.getAnswerRecordById(id);
        return record != null ? Result.success(record) : Result.error("答题记录不存在了");
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
                @RequestParam(value = "questionType",required = false) String questionType
                ){
        IPage<StudentAnswerResult> studentAnswerResultIPage = answerRecordService.searchAnswerRecord(current, size, studentId, questionGroupId,questionType);
        return Result.success(studentAnswerResultIPage);
    }









    @Operation(summary = "批量保存或更新答题记录(业务)")
    @PostMapping("/batch/save")
    public Result<Boolean> saveOrUpdateBatchAnswerRecords(@RequestBody List<AnswerRecord> records,@RequestParam(required = false,value = "studentId") Integer studentId) {
        return answerRecordService.saveOrUpdateBatchAnswerRecords(records,studentId) ?
                Result.success(true, "批量保存成功") :
                Result.error("批量保存失败");
    }

    @Operation(summary = "获取学生做题平均时间统计")
    @GetMapping("/student/{studentId}/group/{groupId}/averagetime")
    public Result<AverageTimeDTO> getAverageTime(
            @PathVariable Integer studentId,
            @PathVariable Integer groupId) {
        AverageTimeDTO averageTime = answerRecordService.getAverageTime(studentId, groupId);
        return Result.success(averageTime);
    }

    @Operation(summary = "获取学生做题正确率统计")
    @GetMapping("/student/{studentId}/group/{groupId}/accuracy")
    public Result<AccuracyRateDTO> getAccuracyRate(
            @PathVariable Integer studentId,
            @PathVariable Integer groupId) {
        AccuracyRateDTO accuracyRate = answerRecordService.getAccuracyRate(studentId, groupId);
        return Result.success(accuracyRate);
    }

    @Operation(summary = "获取学生做题平均分统计")
    @GetMapping("/student/{studentId}/group/{groupId}/averagescore")
    public Result<AverageScoreDTO> getAverageScore(
            @PathVariable Integer studentId,
            @PathVariable Integer groupId) {
        AverageScoreDTO averageScore = answerRecordService.getAverageScore(studentId, groupId);
        return Result.success(averageScore);
    }

    @Operation(summary = "获取学生做题时间极值统计")
    @GetMapping("/student/{studentId}/group/{groupId}/extremetime")
    public Result<ExtremeAnswerTimeDTO> getExtremeTimeRecords(
            @PathVariable Integer studentId,
            @PathVariable Integer groupId) {
        ExtremeAnswerTimeDTO extremeTimeRecords = answerRecordService.getExtremeTimeRecords(studentId, groupId);
        return Result.success(extremeTimeRecords);
    }

    @Operation(summary = "获取学生各知识点平均分统计")
    @GetMapping("/student/{studentId}/group/{groupId}/knowledgepoint/score")
    public Result<List<KnowledgePointScoreDTO>> getKnowledgePointScores(
            @PathVariable Integer studentId,
            @PathVariable Integer groupId) {
        List<KnowledgePointScoreDTO> knowledgePointScores = answerRecordService.getKnowledgePointScores(studentId, groupId);
        return Result.success(knowledgePointScores);
    }
}