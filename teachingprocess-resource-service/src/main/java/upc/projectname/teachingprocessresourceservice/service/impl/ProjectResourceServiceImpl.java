package upc.projectname.teachingprocessresourceservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.ProjectResource;
import upc.projectname.teachingprocessresourceservice.mapper.ProjectResourceMapper;
import upc.projectname.teachingprocessresourceservice.service.ProjectResourceService;

import java.util.List;

@Service
public class ProjectResourceServiceImpl extends ServiceImpl<ProjectResourceMapper, ProjectResource> implements ProjectResourceService {

    @Override
    public ProjectResource getProjectResourceById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveProjectResource(ProjectResource projectResource) {
        return this.save(projectResource);
    }

    @Override
    public boolean updateProjectResource(ProjectResource projectResource) {
        return this.updateById(projectResource);
    }

    @Override
    public boolean deleteProjectResource(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<ProjectResource> getProjectResourceByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<ProjectResource> getProjectResourcesByProjectId(Integer projectId) {
        LambdaQueryWrapper<ProjectResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectResource::getProjectId, projectId);
        return this.list(wrapper);
    }
} 