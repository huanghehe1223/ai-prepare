package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.StudentResource;

import java.util.List;

@FeignClient(name = "teachingprocess-resource-service", contextId = "studentResourceClient")
public interface StudentResourceClient {

    @GetMapping("/studentresource/{id}")
    Result<StudentResource> getStudentResource(@PathVariable Integer id);

    @PostMapping("/studentresource/batch")
    Result<List<StudentResource>> getStudentResourceByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/studentresource/student/{studentId}")
    Result<List<StudentResource>> getStudentResourcesByStudentId(@PathVariable Integer studentId);
    
    @GetMapping("/studentresource/project/{projectId}")
    Result<List<StudentResource>> getStudentResourcesByProjectId(@PathVariable Integer projectId);
    
    @GetMapping("/studentresource/student/{studentId}/project/{projectId}")
    Result<List<StudentResource>> getStudentResourcesByStudentIdAndProjectId(
            @PathVariable Integer studentId, 
            @PathVariable Integer projectId);

    @PostMapping("/studentresource")
    Result<Boolean> saveStudentResource(@RequestBody StudentResource studentResource);

    @PutMapping("/studentresource")
    Result<Boolean> updateStudentResource(@RequestBody StudentResource studentResource);

    @DeleteMapping("/studentresource/{id}")
    Result<Boolean> deleteStudentResource(@PathVariable Integer id);
} 