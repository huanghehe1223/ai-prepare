package upc.projectname.userclassservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.Class;

import java.util.List;

public interface ClassService extends IService<Class> {

    Class getClassById(Integer id);

    boolean saveClass(Class clazz);

    boolean updateClass(Class clazz);

    boolean deleteClass(Integer id);
    
    List<Class> getClassByIds(List<Integer> ids);

    Class getClassByCode(String classCode);
}