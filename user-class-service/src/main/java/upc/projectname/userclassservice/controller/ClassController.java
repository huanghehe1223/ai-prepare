package upc.projectname.userclassservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Class;
import upc.projectname.userclassservice.service.ClassService;

import java.util.List;

@Tag(name = "班级管理接口")
@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;


    @Operation(summary = "根据学生ID、课程名称，状态，分页模糊查询班级（业务，token）")
    @PostMapping("/student")
    public Result<IPage<Class>> getClassesByStudentId(
            @RequestParam Integer studentId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String status) {
        Page<Class> page = new Page<>(current, size);
        IPage<Class> classPage = classService.getClassesByStudentId(page, studentId, className, courseName,status);
        return Result.success(classPage);
    }

    @Operation(summary = "根据ID查询班级")
    @GetMapping("/{id}")
    public Result<Class> getClass(@PathVariable Integer id) {
        Class clazz = classService.getClassById(id);
        return clazz != null ? Result.success(clazz) : Result.error("班级不存在了");
    }

    @Operation(summary = "根据ID批量查询班级")
    @PostMapping("/batch")
    public Result<List<Class>> getClassByIds(@RequestBody List<Integer> ids) {
        List<Class> classes = classService.getClassByIds(ids);
        return classes != null && !classes.isEmpty() ? 
                Result.success(classes) : 
                Result.error("未找到班级信息");
    }

    @Operation(summary = "教师新增班级(业务，token)")
    @PostMapping
    public Result<Boolean> saveClass(@RequestBody Class clazz, @RequestParam Integer teacherId) {
        return classService.saveClass(clazz,teacherId) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败了");
    }

    @Operation(summary = "更新班级信息")
    @PutMapping
    public Result<Boolean> updateClass(@RequestBody Class clazz) {
        return classService.updateClass(clazz) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除班级")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteClass(@PathVariable Integer id) {
        return classService.deleteClass(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }

    @Operation(summary = "根据classCode查询班级")
    @GetMapping("/code/{classCode}")
    public Result<Class> getClassByCode(@PathVariable String classCode) {
        Class clazz = classService.getClassByCode(classCode);
        return clazz != null ? Result.success(clazz) : Result.error("班级不存在");
    }

//    @Operation(summary = "教师id查询班级")
//    @GetMapping("/teacher/{teacherId}")
//    public Result<List<Class>> getClassByTeacherId(@PathVariable Integer teacherId) {
//        List<Class> classes = classService.getClassByTeacherId(teacherId);
//        return classes != null && !classes.isEmpty() ?
//                Result.success(classes) :
//                Result.error("未找到班级信息");
//    }

    @Operation(summary = "教师查询与自己相关的班级，分页条件模糊查询（业务，token）")
    @PostMapping("/teacher")
    public Result<IPage<Class>> getClassByTeacherIdAndStatusAndClassnameAndPage(
            @RequestParam Integer teacherId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String status) {
        Page<Class> page = new Page<>(current, size);
        IPage<Class> classPage = classService.getClassByTeacherIdAndStatusAndClassnameAndPage(page, teacherId, className, status);
        return Result.success(classPage);
    }
} 