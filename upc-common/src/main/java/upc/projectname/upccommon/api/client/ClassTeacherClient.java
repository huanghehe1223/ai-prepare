package upc.projectname.upccommon.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.ClassTeacher;

import java.util.List;

@FeignClient(name = "user-class-service", contextId = "classTeacherClient")
public interface ClassTeacherClient {

    @GetMapping("/classteacher/{id}")
    Result<ClassTeacher> getClassTeacher(@PathVariable Integer id);

    @PostMapping("/classteacher/batch")
    Result<List<ClassTeacher>> getClassTeacherByIds(@RequestBody List<Integer> ids);
    
    @GetMapping("/classteacher/class/{classId}")
    Result<List<ClassTeacher>> getClassTeachersByClassId(@PathVariable Integer classId);
    
    @GetMapping("/classteacher/teacher/{teacherId}")
    Result<List<ClassTeacher>> getClassTeachersByTeacherId(@PathVariable Integer teacherId);

    @PostMapping("/classteacher")
    Result<Boolean> saveClassTeacher(@RequestBody ClassTeacher classTeacher);

    @PutMapping("/classteacher")
    Result<Boolean> updateClassTeacher(@RequestBody ClassTeacher classTeacher);

    @DeleteMapping("/classteacher/{id}")
    Result<Boolean> deleteClassTeacher(@PathVariable Integer id);
    
    @PutMapping("/classteacher/status")
    Result<Boolean> updateTeacherStatus(
            @RequestParam Integer teacherId,
            @RequestParam Integer classId,
            @RequestParam String status);
} 