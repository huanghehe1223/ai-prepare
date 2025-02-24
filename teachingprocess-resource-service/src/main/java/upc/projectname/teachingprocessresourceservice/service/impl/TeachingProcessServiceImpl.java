package upc.projectname.teachingprocessresourceservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.TeachingProcess;
import upc.projectname.teachingprocessresourceservice.mapper.TeachingProcessMapper;
import upc.projectname.teachingprocessresourceservice.service.TeachingProcessService;

import java.util.List;

@Service
public class TeachingProcessServiceImpl extends ServiceImpl<TeachingProcessMapper, TeachingProcess> implements TeachingProcessService {

    @Override
    public TeachingProcess getTeachingProcessById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveTeachingProcess(TeachingProcess teachingProcess) {
        return this.save(teachingProcess);
    }

    @Override
    public boolean updateTeachingProcess(TeachingProcess teachingProcess) {
        return this.updateById(teachingProcess);
    }

    @Override
    public boolean deleteTeachingProcess(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<TeachingProcess> getTeachingProcessByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    //todo 
    // order by stepNumber的用法
    @Override
    public List<TeachingProcess> getTeachingProcessByProjectId(Integer projectId) {
        LambdaQueryWrapper<TeachingProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachingProcess::getProjectId, projectId)
              .orderByAsc(TeachingProcess::getStepNumber);
        return this.list(wrapper);
    }
} 