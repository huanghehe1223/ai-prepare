package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.RecommendQuestionGroup;

import java.util.List;

@FeignClient(name = "exercise-service", contextId = "recommendQuestionGroupClient")
public interface RecommendQuestionGroupClient {

    @GetMapping("/recommendquestiongroup/{id}")
    Result<RecommendQuestionGroup> getRecommendQuestionGroup(@PathVariable Integer id);

    @PostMapping("/recommendquestiongroup/batch")
    Result<List<RecommendQuestionGroup>> getRecommendQuestionGroupByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/recommendquestiongroup/student/{studentId}")
    Result<List<RecommendQuestionGroup>> getRecommendQuestionGroupsByStudentId(@PathVariable Integer studentId);

    @PostMapping("/recommendquestiongroup")
    Result<Boolean> saveRecommendQuestionGroup(@RequestBody RecommendQuestionGroup group);

    @PutMapping("/recommendquestiongroup")
    Result<Boolean> updateRecommendQuestionGroup(@RequestBody RecommendQuestionGroup group);

    @DeleteMapping("/recommendquestiongroup/{id}")
    Result<Boolean> deleteRecommendQuestionGroup(@PathVariable Integer id);
} 