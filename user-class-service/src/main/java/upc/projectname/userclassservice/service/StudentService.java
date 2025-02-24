package upc.projectname.userclassservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import upc.projectname.upccommon.domain.po.Student;
import java.util.List;

public interface StudentService extends IService<Student> {

    Student getStudentById(Integer id);

    boolean saveStudent(Student student);

    boolean updateStudent(Student student);

    boolean deleteStudent(Integer id);
    
    List<Student> getStudentByIds(List<Integer> ids);

    IPage<Student> getStudentPage(Page<Student> page, String studentName, Integer classId, String status, String sex);



    List<Student> getStudentsByClassId(Integer classId);

    boolean studentApply(Integer studentId, String classCode);
}