package upc.projectname.userclassservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.ClassTeacher;
import upc.projectname.userclassservice.service.ClassTeacherService;

import java.util.List;

@Tag(name = "班级教师关系管理接口")
@RestController
@RequestMapping("/classteacher")
@RequiredArgsConstructor
public class ClassTeacherController {

    private final ClassTeacherService classTeacherService;

    @Operation(summary = "根据ID查询班级教师关系")
    @GetMapping("/{id}")
    public Result<ClassTeacher> getClassTeacher(@PathVariable Integer id) {
        ClassTeacher classTeacher = classTeacherService.getClassTeacherById(id);
        return classTeacher != null ? Result.success(classTeacher) : Result.error("关系记录不存在");
    }

    @Operation(summary = "根据ID批量查询班级教师关系")
    @PostMapping("/batch")
    public Result<List<ClassTeacher>> getClassTeacherByIds(@RequestBody List<Integer> ids) {
        List<ClassTeacher> classTeachers = classTeacherService.getClassTeacherByIds(ids);
        return classTeachers != null && !classTeachers.isEmpty() ? 
                Result.success(classTeachers) : 
                Result.error("未找到关系记录");
    }
    
    @Operation(summary = "根据班级ID查询教师关系")
    @GetMapping("/class/{classId}")
    public Result<List<ClassTeacher>> getClassTeachersByClassId(@PathVariable Integer classId) {
        List<ClassTeacher> classTeachers = classTeacherService.getClassTeachersByClassId(classId);
        return classTeachers != null && !classTeachers.isEmpty() ? 
                Result.success(classTeachers) : 
                Result.error("未找到该班级的教师关系记录");
    }
    
    @Operation(summary = "根据教师ID查询班级关系")
    @GetMapping("/teacher/{teacherId}")
    public Result<List<ClassTeacher>> getClassTeachersByTeacherId(@PathVariable Integer teacherId) {
        List<ClassTeacher> classTeachers = classTeacherService.getClassTeachersByTeacherId(teacherId);
        return classTeachers != null && !classTeachers.isEmpty() ? 
                Result.success(classTeachers) : 
                Result.error("未找到该教师的班级关系记录");
    }

    @Operation(summary = "新增班级教师关系")
    @PostMapping
    public Result<Boolean> saveClassTeacher(@RequestBody ClassTeacher classTeacher) {
        return classTeacherService.saveClassTeacher(classTeacher) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新班级教师关系")
    @PutMapping
    public Result<Boolean> updateClassTeacher(@RequestBody ClassTeacher classTeacher) {
        return classTeacherService.updateClassTeacher(classTeacher) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除班级教师关系")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteClassTeacher(@PathVariable Integer id) {
        return classTeacherService.deleteClassTeacher(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }
    
    @Operation(summary = "更新教师状态")
    @PutMapping("/status")
    public Result<Boolean> updateTeacherStatus(
            @RequestParam Integer teacherId,
            @RequestParam Integer classId,
            @RequestParam String status) {
        return classTeacherService.updateTeacherStatus(teacherId, classId, status) ?
                Result.success(true, "状态更新成功") :
                Result.error("状态更新失败");
    }
} 