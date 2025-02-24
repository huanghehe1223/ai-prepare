package upc.projectname.userclassservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.ClassStudent;
import java.util.List;

public interface ClassStudentService extends IService<ClassStudent> {

    ClassStudent getClassStudentById(Integer id);

    boolean saveClassStudent(ClassStudent classStudent);

    boolean updateClassStudent(ClassStudent classStudent);

    boolean deleteClassStudent(Integer id);
    
    List<ClassStudent> getClassStudentByIds(List<Integer> ids);
    
    List<ClassStudent> getClassStudentsByClassId(Integer classId);
    
    List<ClassStudent> getClassStudentsByStudentId(Integer studentId);
    
    boolean updateStudentStatus(Integer studentId, Integer classId, String status);
} 