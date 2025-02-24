package upc.projectname.projectservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import upc.projectname.upccommon.domain.po.Project;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
} 