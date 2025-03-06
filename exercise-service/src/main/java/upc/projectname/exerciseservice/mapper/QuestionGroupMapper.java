package upc.projectname.exerciseservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import upc.projectname.upccommon.domain.po.QuestionGroup;

import java.util.List;

@Mapper
public interface QuestionGroupMapper extends BaseMapper<QuestionGroup> {
    
    @Select("SELECT DISTINCT qg.group_id, qg.project_id, qg.create_time, qg.deadline, qg.group_type " +
           "FROM question_group qg " +
           "LEFT JOIN questiongroup_status qs ON qg.group_id = qs.group_id " +
           "WHERE qg.project_id = #{projectId} " +
           "AND qs.student_id = #{studentId} " +
           "AND qs.status = #{status} " +
           "AND (#{groupType} IS NULL OR #{groupType} = '' OR qg.group_type = #{groupType})")
    List<QuestionGroup> searchStudentGroup(@Param("projectId") Integer projectId,
                                         @Param("studentId") Integer studentId,
                                         @Param("status") String status, 
                                         @Param("groupType") String groupType);
} 