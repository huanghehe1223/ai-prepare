package upc.projectname.teachingprocessresourceservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.StudentResource;

import java.util.List;

public interface StudentResourceService extends IService<StudentResource> {

    StudentResource getStudentResourceById(Integer id);

    boolean saveStudentResource(StudentResource studentResource);

    boolean updateStudentResource(StudentResource studentResource);

    boolean deleteStudentResource(Integer id);
    
    List<StudentResource> getStudentResourceByIds(List<Integer> ids);
    
    List<StudentResource> getStudentResourcesByStudentId(Integer studentId);
    
    List<StudentResource> getStudentResourcesByProjectId(Integer projectId);
    
    List<StudentResource> getStudentResourcesByStudentIdAndProjectId(Integer studentId, Integer projectId);
} 