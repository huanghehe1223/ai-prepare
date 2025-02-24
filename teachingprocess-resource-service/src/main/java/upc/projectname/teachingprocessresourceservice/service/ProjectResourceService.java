package upc.projectname.teachingprocessresourceservice.service;

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
} 