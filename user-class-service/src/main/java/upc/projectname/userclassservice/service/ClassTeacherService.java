package upc.projectname.userclassservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.ClassTeacher;
import java.util.List;

public interface ClassTeacherService extends IService<ClassTeacher> {

    ClassTeacher getClassTeacherById(Integer id);

    boolean saveClassTeacher(ClassTeacher classTeacher);

    boolean updateClassTeacher(ClassTeacher classTeacher);

    boolean deleteClassTeacher(Integer id);
    
    List<ClassTeacher> getClassTeacherByIds(List<Integer> ids);
    
    List<ClassTeacher> getClassTeachersByClassId(Integer classId);
    
    List<ClassTeacher> getClassTeachersByTeacherId(Integer teacherId);
    
    boolean updateTeacherStatus(Integer teacherId, Integer classId, String status);
} 