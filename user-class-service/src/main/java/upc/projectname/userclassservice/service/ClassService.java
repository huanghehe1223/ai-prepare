package upc.projectname.userclassservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.Class;

import java.util.List;

public interface ClassService extends IService<Class> {

    IPage<Class> getClassesByStudentId(Page<Class> page, Integer studentId, String className, String courseName,String status);

    Class getClassById(Integer id);

    boolean saveClass(Class clazz, Integer teacherId);

    boolean updateClass(Class clazz);

    boolean deleteClass(Integer id);
    
    List<Class> getClassByIds(List<Integer> ids);

    Class getClassByCode(String classCode);

    IPage<Class> getClassByTeacherIdAndStatusAndClassnameAndPage(Page<Class> page, Integer teacherId, String className, String status);
}