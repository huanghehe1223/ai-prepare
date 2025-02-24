package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Teacher;

import java.util.List;

@FeignClient(name = "user-class-service", contextId = "teacherClient")
public interface TeacherClient {

    @GetMapping("/teacher/{id}")
    Result<Teacher> getTeacher(@PathVariable Integer id);

    @PostMapping("/teacher/batch")
    Result<List<Teacher>> getTeacherByIds(@RequestBody List<Integer> ids);

    @PostMapping("/teacher")
    Result<Boolean> saveTeacher(@RequestBody Teacher teacher);

    @PutMapping("/teacher")
    Result<Boolean> updateTeacher(@RequestBody Teacher teacher);

    @DeleteMapping("/teacher/{id}")
    Result<Boolean> deleteTeacher(@PathVariable Integer id);
} 