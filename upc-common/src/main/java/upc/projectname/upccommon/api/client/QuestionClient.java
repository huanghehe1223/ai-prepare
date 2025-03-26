package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Question;

import java.util.List;

@FeignClient(name = "exercise-service", contextId = "questionClient")
public interface QuestionClient {

    @GetMapping("/question/{id}")
    Result<Question> getQuestion(@PathVariable Integer id);

    @PostMapping("/question/batch")
    Result<List<Question>> getQuestionByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/question/group/{groupId}")
    Result<List<Question>> getQuestionsByGroupId(@PathVariable Integer groupId);

    @PostMapping("/question")
    Result<Boolean> saveQuestion(@RequestBody Question question);

    @PutMapping("/question")
    Result<Boolean> updateQuestion(@RequestBody Question question);

    @DeleteMapping("/question/{id}")
    Result<Boolean> deleteQuestion(@PathVariable Integer id);

    @PostMapping("/question/batch/add")
    Result<Boolean> saveQuestions(@RequestBody List<Question> questions);

} 