package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.QuestionGroupStatus;

import java.util.List;

@FeignClient(name = "exercise-service", contextId = "questionGroupStatusClient")
public interface QuestionGroupStatusClient {

    @GetMapping("/questiongroupstatus/{id}")
    Result<QuestionGroupStatus> getStatus(@PathVariable Integer id);

    @PostMapping("/questiongroupstatus/batch")
    Result<List<QuestionGroupStatus>> getStatusByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/questiongroupstatus/student/{studentId}")
    Result<List<QuestionGroupStatus>> getStatusByStudentId(@PathVariable Integer studentId);
    
    @GetMapping("/questiongroupstatus/group/{groupId}")
    Result<List<QuestionGroupStatus>> getStatusByGroupId(@PathVariable Integer groupId);

    @PostMapping("/questiongroupstatus")
    Result<Boolean> saveStatus(@RequestBody QuestionGroupStatus status);

    @PutMapping("/questiongroupstatus")
    Result<Boolean> updateStatus(@RequestBody QuestionGroupStatus status);

    @DeleteMapping("/questiongroupstatus/{id}")
    Result<Boolean> deleteStatus(@PathVariable Integer id);

    @GetMapping("/questiongroupstatus/group/{groupId}/student/{studentId}")
    Result<List<QuestionGroupStatus>> getStatusByGroupIdAndStudent(
            @PathVariable Integer groupId, 
            @PathVariable Integer studentId);
} 