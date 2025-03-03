package upc.projectname.userclassservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.Teacher;
import upc.projectname.upccommon.domain.po.Result;

import java.util.List;

public interface TeacherService extends IService<Teacher> {

    Teacher getTeacherById(Integer id);

    boolean saveTeacher(Teacher teacher);

    boolean updateTeacher(Teacher teacher);

    boolean deleteTeacher(Integer id);
    
    List<Teacher> getTeacherByIds(List<Integer> ids);

    Result<Boolean> teacherRegister(Teacher teacher);
    
    Result<String> teacherLogin(String userName, String password);
} 