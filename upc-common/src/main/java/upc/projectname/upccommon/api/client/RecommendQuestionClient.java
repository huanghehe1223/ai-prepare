package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.RecommendQuestion;

import java.util.List;

@FeignClient(name = "exercise-service", contextId = "recommendQuestionClient")
public interface RecommendQuestionClient {

    @GetMapping("/recommendquestion/{id}")
    Result<RecommendQuestion> getRecommendQuestion(@PathVariable Integer id);

    @PostMapping("/recommendquestion/batch")
    Result<List<RecommendQuestion>> getRecommendQuestionByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/recommendquestion/group/{groupId}")
    Result<List<RecommendQuestion>> getRecommendQuestionsByGroupId(@PathVariable Integer groupId);

    @PostMapping("/recommendquestion")
    Result<Boolean> saveRecommendQuestion(@RequestBody RecommendQuestion question);

    @PutMapping("/recommendquestion")
    Result<Boolean> updateRecommendQuestion(@RequestBody RecommendQuestion question);

    @DeleteMapping("/recommendquestion/{id}")
    Result<Boolean> deleteRecommendQuestion(@PathVariable Integer id);
} 