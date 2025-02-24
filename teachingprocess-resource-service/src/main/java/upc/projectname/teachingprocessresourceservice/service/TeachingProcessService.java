package upc.projectname.teachingprocessresourceservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.TeachingProcess;

import java.util.List;

public interface TeachingProcessService extends IService<TeachingProcess> {

    TeachingProcess getTeachingProcessById(Integer id);

    boolean saveTeachingProcess(TeachingProcess teachingProcess);

    boolean updateTeachingProcess(TeachingProcess teachingProcess);

    boolean deleteTeachingProcess(Integer id);
    
    List<TeachingProcess> getTeachingProcessByIds(List<Integer> ids);
    
    List<TeachingProcess> getTeachingProcessByProjectId(Integer projectId);
} 