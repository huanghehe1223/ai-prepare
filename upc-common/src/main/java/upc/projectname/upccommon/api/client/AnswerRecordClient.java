package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.AnswerRecord;

import java.util.List;

@FeignClient(name = "exercise-service", contextId = "answerRecordClient")
public interface AnswerRecordClient {

    @GetMapping("/answerrecord/{id}")
    Result<AnswerRecord> getAnswerRecord(@PathVariable Integer id);

    @PostMapping("/answerrecord/batch")
    Result<List<AnswerRecord>> getAnswerRecordByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/answerrecord/student/{studentId}")
    Result<List<AnswerRecord>> getAnswerRecordsByStudentId(@PathVariable Integer studentId);
    
    @GetMapping("/answerrecord/question/{questionId}")
    Result<List<AnswerRecord>> getAnswerRecordsByQuestionId(@PathVariable Integer questionId);
    
    @GetMapping("/answerrecord/student/{studentId}/question/{questionId}")
    Result<List<AnswerRecord>> getAnswerRecordsByStudentAndQuestion(
            @PathVariable Integer studentId,
            @PathVariable Integer questionId);

    @PostMapping("/answerrecord")
    Result<Boolean> saveAnswerRecord(@RequestBody AnswerRecord record);

    @PutMapping("/answerrecord")
    Result<Boolean> updateAnswerRecord(@RequestBody AnswerRecord record);

    @DeleteMapping("/answerrecord/{id}")
    Result<Boolean> deleteAnswerRecord(@PathVariable Integer id);
} 