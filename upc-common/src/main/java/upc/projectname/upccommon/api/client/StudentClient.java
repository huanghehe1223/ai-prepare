package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Student;

import java.util.List;



@FeignClient(name = "user-class-service", contextId = "studentClient")
public interface StudentClient {


    //根据学生ID获取学生信息
    @GetMapping("/student/{id}")
    Result<Student> getStudent(@PathVariable Integer id);

    //批量获取多个学生信息
    @PostMapping("/student/batch")
    Result<List<Student>> getStudentByIds(@RequestBody List<Integer> ids);

    //保存新的学生信息
    @PostMapping("/student")
    Result<Boolean> saveStudent(@RequestBody Student student);

    //更新已有的学生信息
    @PutMapping("/student")
    Result<Boolean> updateStudent(@RequestBody Student student);


    //根据ID删除学生信息
    @DeleteMapping("/student/{id}")
    Result<Boolean> deleteStudent(@PathVariable Integer id);

}
