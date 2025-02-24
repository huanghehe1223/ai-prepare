package upc.projectname.userclassservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.ClassStudent;
import upc.projectname.userclassservice.service.ClassStudentService;

import java.util.List;

@Tag(name = "班级学生关系管理接口")
@RestController
@RequestMapping("/classstudent")
@RequiredArgsConstructor
public class ClassStudentController {

    private final ClassStudentService classStudentService;

    @Operation(summary = "根据ID查询班级学生关系")
    @GetMapping("/{id}")
    public Result<ClassStudent> getClassStudent(@PathVariable Integer id) {
        ClassStudent classStudent = classStudentService.getClassStudentById(id);
        return classStudent != null ? Result.success(classStudent) : Result.error("关系记录不存在");
    }

    @Operation(summary = "根据ID批量查询班级学生关系")
    @PostMapping("/batch")
    public Result<List<ClassStudent>> getClassStudentByIds(@RequestBody List<Integer> ids) {
        List<ClassStudent> classStudents = classStudentService.getClassStudentByIds(ids);
        return classStudents != null && !classStudents.isEmpty() ? 
                Result.success(classStudents) : 
                Result.error("未找到关系记录");
    }
    
    @Operation(summary = "根据班级ID查询学生关系")
    @GetMapping("/class/{classId}")
    public Result<List<ClassStudent>> getClassStudentsByClassId(@PathVariable Integer classId) {
        List<ClassStudent> classStudents = classStudentService.getClassStudentsByClassId(classId);
        return classStudents != null && !classStudents.isEmpty() ? 
                Result.success(classStudents) : 
                Result.error("未找到该班级的学生关系记录");
    }
    
    @Operation(summary = "根据学生ID查询班级关系")
    @GetMapping("/student/{studentId}")
    public Result<List<ClassStudent>> getClassStudentsByStudentId(@PathVariable Integer studentId) {
        List<ClassStudent> classStudents = classStudentService.getClassStudentsByStudentId(studentId);
        return classStudents != null && !classStudents.isEmpty() ? 
                Result.success(classStudents) : 
                Result.error("未找到该学生的班级关系记录");
    }

    @Operation(summary = "新增班级学生关系")
    @PostMapping
    public Result<Boolean> saveClassStudent(@RequestBody ClassStudent classStudent) {
        return classStudentService.saveClassStudent(classStudent) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新班级学生关系")
    @PutMapping
    public Result<Boolean> updateClassStudent(@RequestBody ClassStudent classStudent) {
        return classStudentService.updateClassStudent(classStudent) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除班级学生关系")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteClassStudent(@PathVariable Integer id) {
        return classStudentService.deleteClassStudent(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }
    
    @Operation(summary = "更新学生状态")
    @PutMapping("/status")
    public Result<Boolean> updateStudentStatus(
            @RequestParam Integer studentId,
            @RequestParam Integer classId,
            @RequestParam String status) {
        return classStudentService.updateStudentStatus(studentId, classId, status) ?
                Result.success(true, "状态更新成功") :
                Result.error("状态更新失败");
    }
} 