package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.ClassStudent;
import upc.projectname.upccommon.domain.po.Student;

import java.util.List;

@FeignClient(name = "user-class-service", contextId = "classStudentClient")
public interface ClassStudentClient {

    @GetMapping("/classstudent/{id}")
    Result<ClassStudent> getClassStudent(@PathVariable Integer id);

    @PostMapping("/classstudent/batch")
    Result<List<ClassStudent>> getClassStudentByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/classstudent/class/{classId}")
    Result<List<ClassStudent>> getClassStudentsByClassId(@PathVariable Integer classId);
    
    @GetMapping("/classstudent/student/{studentId}")
    Result<List<ClassStudent>> getClassStudentsByStudentId(@PathVariable Integer studentId);

    @PostMapping("/classstudent")
    Result<Boolean> saveClassStudent(@RequestBody ClassStudent classStudent);

    @PutMapping("/classstudent")
    Result<Boolean> updateClassStudent(@RequestBody ClassStudent classStudent);

    @DeleteMapping("/classstudent/{id}")
    Result<Boolean> deleteClassStudent(@PathVariable Integer id);
} 