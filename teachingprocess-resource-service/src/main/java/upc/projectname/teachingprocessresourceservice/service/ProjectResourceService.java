package upc.projectname.teachingprocessresourceservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.ProjectResource;

import java.util.List;

public interface ProjectResourceService extends IService<ProjectResource> {

    ProjectResource getProjectResourceById(Integer id);

    boolean saveProjectResource(ProjectResource projectResource);

    boolean updateProjectResource(ProjectResource projectResource);

    boolean deleteProjectResource(Integer id);
    
    List<ProjectResource> getProjectResourceByIds(List<Integer> ids);
    
    List<ProjectResource> getProjectResourcesByProjectId(Integer projectId);

    /**
     * 分页查询项目资源
     * @param page 分页参数
     * @param studentId 学生ID
     * @param projectId 项目ID
     * @param type 资源类型
     * @param groupId 习题组ID
     * @param groupType 习题组类型
     * @return 分页结果
     */
    IPage<ProjectResource> getProjectResourcesByPage(Page<ProjectResource> page,
                                                     Integer studentId,
                                                     Integer projectId,
                                                     String type,
                                                     Integer groupId,
                                                     String groupType);
} 