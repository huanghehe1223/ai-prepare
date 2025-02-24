package upc.projectname.projectservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.projectservice.mapper.ProjectMapper;
import upc.projectname.projectservice.service.ProjectService;

import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Override
    public Project getProjectById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveProject(Project project) {
        return this.save(project);
    }

    @Override
    public boolean updateProject(Project project) {
        return this.updateById(project);
    }

    @Override
    public boolean deleteProject(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<Project> getProjectByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<Project> getProjectsByClassId(Integer classId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getClassId, classId);
        return this.list(wrapper);
    }
} 