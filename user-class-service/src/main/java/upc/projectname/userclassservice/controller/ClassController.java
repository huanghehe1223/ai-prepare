package upc.projectname.userclassservice.controller;

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

    @Operation(summary = "根据ID查询班级")
    @GetMapping("/{id}")
    public Result<Class> getClass(@PathVariable Integer id) {
        Class clazz = classService.getClassById(id);
        return clazz != null ? Result.success(clazz) : Result.error("班级不存在");
    }

    @Operation(summary = "根据ID批量查询班级")
    @PostMapping("/batch")
    public Result<List<Class>> getClassByIds(@RequestBody List<Integer> ids) {
        List<Class> classes = classService.getClassByIds(ids);
        return classes != null && !classes.isEmpty() ? 
                Result.success(classes) : 
                Result.error("未找到班级信息");
    }

    @Operation(summary = "新增班级")
    @PostMapping
    public Result<Boolean> saveClass(@RequestBody Class clazz) {
        return classService.saveClass(clazz) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
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
} 