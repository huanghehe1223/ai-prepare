package upc.projectname.userclassservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Student;
import upc.projectname.userclassservice.service.StudentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

@Tag(name = "学生管理接口")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "根据ID查询学生")
    @GetMapping("/{id}")
    public Result<Student> getStudent(@PathVariable Integer id) {
        Student student = studentService.getStudentById(id);
        return student != null ? Result.success(student) : Result.error("学生不存在");
    }

    @Operation(summary = "根据ID批量查询学生")
    @PostMapping("/batch")
    public Result<List<Student>> getStudentByIds(@RequestBody List<Integer> ids) {
        List<Student> students = studentService.getStudentByIds(ids);
        return students != null && !students.isEmpty() ? 
                Result.success(students) : 
                Result.error("未找到学生信息");
    }

    @Operation(summary = "新增学生")
    @PostMapping
    public Result<Boolean> saveStudent(@RequestBody Student student) {
        return studentService.saveStudent(student) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新学生信息")
    @PutMapping
    public Result<Boolean> updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteStudent(@PathVariable Integer id) {
        return studentService.deleteStudent(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }


    @Operation(summary = "学生申请入班")
    @PostMapping("studentApply")
    public Result<Boolean> studentApply(@RequestParam("studentId") Integer studentId, @RequestParam("classCode") String classCode) {
        return studentService.studentApply(studentId,classCode) ?
                Result.success(true, "申请成功") :
                Result.error("申请失败");
    }

    @Operation(summary = "分页条件查询学生(业务)")
    @GetMapping("/page")
    public Result<IPage<Student>> getStudentPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) String status) {
        Page<Student> page = new Page<>(current, size);
        IPage<Student> studentPage = studentService.getStudentPage(page, studentName, classId, status,sex);
        return Result.success(studentPage);
    }




    @Operation(summary = "同意学生入班（业务）")
    @PutMapping("/agree")
    public Result<Boolean> agreeStudentApply(@RequestParam("studentId") Integer studentId, @RequestParam("classId") Integer classId) {
        return studentService.agreeStudentApply(studentId,classId) ?
                Result.success(true, "同意成功") :
                Result.error("同意失败");
    }









    @Operation(summary = "获取班级学生")
    @GetMapping("/class/{classId}")
    public Result<List<Student>> getStudentsByClassId(@PathVariable Integer classId) {
        List<Student> students = studentService.getStudentsByClassId(classId);
        return students != null && !students.isEmpty() ?
                Result.success(students) :
                Result.error("未找到该班级的学生");
    }



}