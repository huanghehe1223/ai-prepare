package upc.projectname.teachingprocessresourceservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.StudentResource;
import upc.projectname.teachingprocessresourceservice.mapper.StudentResourceMapper;
import upc.projectname.teachingprocessresourceservice.service.StudentResourceService;

import java.util.List;

@Service
public class StudentResourceServiceImpl extends ServiceImpl<StudentResourceMapper, StudentResource> implements StudentResourceService {

    @Override
    public StudentResource getStudentResourceById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveStudentResource(StudentResource studentResource) {
        return this.save(studentResource);
    }

    @Override
    public boolean updateStudentResource(StudentResource studentResource) {
        return this.updateById(studentResource);
    }

    @Override
    public boolean deleteStudentResource(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<StudentResource> getStudentResourceByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<StudentResource> getStudentResourcesByStudentId(Integer studentId) {
        LambdaQueryWrapper<StudentResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentResource::getStudentId, studentId);
        return this.list(wrapper);
    }
    
    @Override
    public List<StudentResource> getStudentResourcesByProjectId(Integer projectId) {
        LambdaQueryWrapper<StudentResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentResource::getProjectId, projectId);
        return this.list(wrapper);
    }
    
    @Override
    public List<StudentResource> getStudentResourcesByStudentIdAndProjectId(Integer studentId, Integer projectId) {
        LambdaQueryWrapper<StudentResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentResource::getStudentId, studentId)
              .eq(StudentResource::getProjectId, projectId);
        return this.list(wrapper);
    }
} 