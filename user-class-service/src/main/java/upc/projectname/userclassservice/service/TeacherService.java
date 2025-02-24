package upc.projectname.userclassservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.Teacher;

import java.util.List;

public interface TeacherService extends IService<Teacher> {

    Teacher getTeacherById(Integer id);

    boolean saveTeacher(Teacher teacher);

    boolean updateTeacher(Teacher teacher);

    boolean deleteTeacher(Integer id);
    
    List<Teacher> getTeacherByIds(List<Integer> ids);
} 