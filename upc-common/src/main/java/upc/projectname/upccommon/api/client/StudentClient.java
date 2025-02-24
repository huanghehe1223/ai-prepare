package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Student;

import java.util.List;

@FeignClient(name = "user-class-service", contextId = "studentClient")
public interface StudentClient {

    @GetMapping("/student/{id}")
    Result<Student> getStudent(@PathVariable Integer id);

    @PostMapping("/student/batch")
    Result<List<Student>> getStudentByIds(@RequestBody List<Integer> ids);

    @PostMapping("/student")
    Result<Boolean> saveStudent(@RequestBody Student student);

    @PutMapping("/student")
    Result<Boolean> updateStudent(@RequestBody Student student);

    @DeleteMapping("/student/{id}")
    Result<Boolean> deleteStudent(@PathVariable Integer id);

}
