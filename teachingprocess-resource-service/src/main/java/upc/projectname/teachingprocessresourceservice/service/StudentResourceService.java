package upc.projectname.teachingprocessresourceservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 根据学生ID和习题组ID查询资源ID列表
     * @param studentId 学生ID
     * @param groupId 习题组ID
     * @return 资源ID列表
     */
    List<Integer> getResourceIdsByStudentIdAndGroupId(Integer studentId, Integer groupId);

    /**
     * 根据学生ID和项目ID查询资源ID列表
     * @param studentId 学生ID
     * @param projectId 项目ID
     * @return 资源ID列表
     */
    List<Integer> getResourceIdsByStudentIdAndProjectId(Integer studentId, Integer projectId);

    /**
     * 分页查询学生资源
     * @param page 分页参数
     * @param studentId 学生ID
     * @param projectId 项目ID
     * @param groupId 习题组ID
     * @param groupType 习题组类型
     * @return 分页结果
     */
    IPage<StudentResource> getStudentResourcesByPage(Page<StudentResource> page,
                                                     Integer studentId,
                                                     Integer projectId,
                                                     Integer groupId,
                                                     String groupType);
} 