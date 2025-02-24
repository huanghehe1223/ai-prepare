package upc.projectname.projectservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.Project;

import java.util.List;

public interface ProjectService extends IService<Project> {

    Project getProjectById(Integer id);

    boolean saveProject(Project project);

    boolean updateProject(Project project);

    boolean deleteProject(Integer id);
    
    List<Project> getProjectByIds(List<Integer> ids);
    
    List<Project> getProjectsByClassId(Integer classId);
} 