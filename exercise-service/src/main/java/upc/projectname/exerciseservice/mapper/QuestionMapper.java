package upc.projectname.exerciseservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import upc.projectname.upccommon.domain.po.Question;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
} 