package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.QuestionGroup;

import java.util.List;

@FeignClient(name = "exercise-service", contextId = "questionGroupClient")
public interface QuestionGroupClient {

    @GetMapping("/questiongroup/{id}")
    Result<QuestionGroup> getQuestionGroup(@PathVariable Integer id);

    @PostMapping("/questiongroup/batch")
    Result<List<QuestionGroup>> getQuestionGroupByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/questiongroup/project/{projectId}")
    Result<List<QuestionGroup>> getQuestionGroupsByProjectId(@PathVariable Integer projectId);

    @PostMapping("/questiongroup")
    Result<Boolean> saveQuestionGroup(@RequestBody QuestionGroup questionGroup);

    @PutMapping("/questiongroup")
    Result<Boolean> updateQuestionGroup(@RequestBody QuestionGroup questionGroup);

    @DeleteMapping("/questiongroup/{id}")
    Result<Boolean> deleteQuestionGroup(@PathVariable Integer id);
} 