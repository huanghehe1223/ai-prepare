package upc.projectname.projectservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import upc.projectname.upccommon.domain.po.Project;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    
    @Update("""
            <script>
            UPDATE project
            <set>
                <if test="classId != null">
                    class_id = #{classId},
                </if>
                <if test="teachingAims != null and teachingAims != ''">
                    teaching_aims = #{teachingAims},
                </if>
                <if test="studentAnalysis != null and studentAnalysis != ''">
                    student_analysis = #{studentAnalysis},
                </if>
                <if test="knowledgePoints != null and knowledgePoints != ''">
                    knowledge_points = #{knowledgePoints},
                </if>
                <if test="teachingContent != null and teachingContent != ''">
                    teaching_content = #{teachingContent},
                </if>
                <if test="teachingDuration != null">
                    teaching_duration = #{teachingDuration},
                </if>
                <if test="teachingTheme != null and teachingTheme != ''">
                    teaching_theme = #{teachingTheme},
                </if>
                <if test="teachingObject != null and teachingObject != ''">
                    teaching_object = #{teachingObject},
                </if>
                <if test="extraReq != null and extraReq != ''">
                    extra_req = #{extraReq},
                </if>
                <if test="currentStage != null">
                    current_stage = #{currentStage},
                </if>
                 
                 <if test="textbookContent != null and textbookContent != ''">
                    textbook_content = #{textbookContent},
                </if>
                 
                 <if test="preexerceseResult != null and preexerceseResult != ''">
                    preexercese_result = #{preexerceseResult},
                </if>
     
            </set>
            WHERE project_id = #{projectId}
            </script>
            """)
    int updateProjectSelective(@Param("projectId") Integer projectId,
                               @Param("classId") Integer classId,
                               @Param("teachingAims") String teachingAims,
                               @Param("studentAnalysis") String studentAnalysis,
                               @Param("knowledgePoints") String knowledgePoints,
                               @Param("teachingContent") String teachingContent,
                               @Param("teachingDuration") Integer teachingDuration,
                               @Param("teachingTheme") String teachingTheme,
                               @Param("teachingObject") String teachingObject,
                               @Param("extraReq") String extraReq,
                               @Param("currentStage") Integer currentStage,
                               @Param("textbookContent") String textbookContent,
                               @Param("preexerceseResult") String preexerceseResult);
} 