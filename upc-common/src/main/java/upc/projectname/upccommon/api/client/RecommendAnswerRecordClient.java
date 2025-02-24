package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.RecommendAnswerRecord;

import java.util.List;

@FeignClient(name = "exercise-service", contextId = "recommendAnswerRecordClient")
public interface RecommendAnswerRecordClient {

    @GetMapping("/recommendanswerrecord/{id}")
    Result<RecommendAnswerRecord> getRecommendAnswerRecord(@PathVariable Integer id);

    @PostMapping("/recommendanswerrecord/batch")
    Result<List<RecommendAnswerRecord>> getRecommendAnswerRecordByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/recommendanswerrecord/question/{questionId}")
    Result<List<RecommendAnswerRecord>> getRecommendAnswerRecordsByQuestionId(@PathVariable Integer questionId);

    @PostMapping("/recommendanswerrecord")
    Result<Boolean> saveRecommendAnswerRecord(@RequestBody RecommendAnswerRecord record);

    @PutMapping("/recommendanswerrecord")
    Result<Boolean> updateRecommendAnswerRecord(@RequestBody RecommendAnswerRecord record);

    @DeleteMapping("/recommendanswerrecord/{id}")
    Result<Boolean> deleteRecommendAnswerRecord(@PathVariable Integer id);
} 