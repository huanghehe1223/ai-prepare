package upc.projectname.exerciseservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.RecommendAnswerRecord;

@Mapper
public interface RecommendAnswerRecordMapper extends BaseMapper<RecommendAnswerRecord> {

    @Select("""
        <script>
        SELECT 
            rq.question_type,
            rq.question_text,
            rq.correct_answer,
            rq.explanation,
            rq.difficulty,
            rq.option_a,
            rq.option_b,
            rq.option_c,
            rq.option_d,
            rq.knowledge_point,
            rar.answer_result,
            rar.student_answer,
            rar.created_at,
            rar.ai_analysis
        FROM recommend_answer_record rar
        INNER JOIN recommend_question rq ON rar.question_id = rq.question_id
        <where>
            rq.group_id = #{questionGroupId}
            <if test="questionType != null and questionType != ''">
                AND rq.question_type = #{questionType}
            </if>
        </where>
        ORDER BY rar.created_at DESC
        </script>
        """)
    IPage<StudentAnswerResult> searchRecommendAnswerRecord(Page<StudentAnswerResult> page,
                                                         @Param("questionGroupId") Integer questionGroupId,
                                                         @Param("questionType") String questionType);
} 