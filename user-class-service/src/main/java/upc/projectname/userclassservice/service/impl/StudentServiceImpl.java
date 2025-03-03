package upc.projectname.userclassservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.api.client.QuestionClient;
import upc.projectname.upccommon.domain.po.Class;
import upc.projectname.upccommon.domain.po.ClassStudent;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Student;
import upc.projectname.userclassservice.mapper.ClassMapper;
import upc.projectname.userclassservice.mapper.StudentMapper;
import upc.projectname.userclassservice.service.ClassService;
import upc.projectname.userclassservice.service.ClassStudentService;
import upc.projectname.userclassservice.service.StudentService;
import upc.projectname.userclassservice.utils.JwtUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    private final ClassService classService;
    private final ClassStudentService classStudentService;


    @Override
    public Student getStudentById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveStudent(Student student) {
        return this.save(student);
    }

    @Override
    public boolean updateStudent(Student student) {
        return this.updateById(student);
    }

    @Override
    public boolean deleteStudent(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<Student> getStudentByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }

    @Override
    public IPage<Student> getStudentPage(Page<Student> page, String studentName, Integer classId, String status, String sex) {
        IPage<Student> studentIPage = this.baseMapper.selectStudentPage(page, studentName, sex, classId, status);
        return studentIPage;
    }



    @Override
    public List<Student> getStudentsByClassId(Integer classId) {

        return null;
    }

    @Override
    public boolean studentApply(Integer studentId, String classCode) {
        Class classByCode = classService.getClassByCode(classCode);
        if (classByCode == null) {
            return false;
        }
        ClassStudent classStudent = new ClassStudent();
        classStudent.setStudentId(studentId);
        classStudent.setClassId(classByCode.getClassId());
        classStudent.setStatus("Apply");
        return classStudentService.saveClassStudent(classStudent);

    }

    @Override
    public boolean agreeStudentApply(Integer studentId, Integer classId) {
         return classStudentService.updateStudentStatus(studentId, classId, "Agree");
    }

    @Override
    public Result<Boolean> studentRegister(Student student) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserName, student.getUserName());
        Student selectOne = this.baseMapper.selectOne(wrapper);
        if (selectOne != null) {
            return Result.error("用户名已存在");
        }
        boolean save = this.save(student);
        if (save) {
            return Result.success(true, "注册成功");
        }
        return Result.error("注册失败");
    }

    @Override
    public Result<String> studentLogin(String userName, String password) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserName, userName);
        wrapper.eq(Student::getPassword, password);
        Student selectOne = this.baseMapper.selectOne(wrapper);
        if (selectOne == null) {
            return Result.error("用户名或密码错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("studentId", selectOne.getStudentId());
        String jwt = JwtUtils.createJwt(map);
        return Result.success(jwt);

    }
}