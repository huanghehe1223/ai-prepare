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
import upc.projectname.upccommon.domain.po.Student;
import upc.projectname.userclassservice.mapper.ClassMapper;
import upc.projectname.userclassservice.mapper.StudentMapper;
import upc.projectname.userclassservice.service.ClassService;
import upc.projectname.userclassservice.service.ClassStudentService;
import upc.projectname.userclassservice.service.StudentService;

import java.util.List;

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

        return false;
    }


}