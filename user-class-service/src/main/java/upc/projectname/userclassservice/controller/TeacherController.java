package upc.projectname.userclassservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Teacher;
import upc.projectname.userclassservice.service.TeacherService;

import java.util.List;

@Tag(name = "教师管理接口")
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "根据ID查询教师")
    @GetMapping("/{id}")
    public Result<Teacher> getTeacher(@PathVariable Integer id) {
        Teacher teacher = teacherService.getTeacherById(id);
        return teacher != null ? Result.success(teacher) : Result.error("教师不存在");
    }

    @Operation(summary = "根据ID批量查询教师")
    @PostMapping("/batch")
    public Result<List<Teacher>> getTeacherByIds(@RequestBody List<Integer> ids) {
        List<Teacher> teachers = teacherService.getTeacherByIds(ids);
        return teachers != null && !teachers.isEmpty() ? 
                Result.success(teachers) : 
                Result.error("未找到教师信息");
    }

    @Operation(summary = "新增教师")
    @PostMapping
    public Result<Boolean> saveTeacher(@RequestBody Teacher teacher) {
        return teacherService.saveTeacher(teacher) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新教师信息")
    @PutMapping
    public Result<Boolean> updateTeacher(@RequestBody Teacher teacher) {
        return teacherService.updateTeacher(teacher) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除教师")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTeacher(@PathVariable Integer id) {
        return teacherService.deleteTeacher(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }
} 