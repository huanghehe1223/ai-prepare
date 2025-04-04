package upc.projectname.exerciseservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import upc.projectname.upccommon.domain.dto.*;
import upc.projectname.upccommon.domain.po.AnswerRecord;
import upc.projectname.upccommon.domain.po.Student;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnswerRecordMapper extends BaseMapper<AnswerRecord> {

    // todo
    // 在最后面(where标签之后)加上 ORDER BY ar.created_at DESC

    @Select("""
        <script>
        SELECT 
            q.question_type,
            q.question_text,
            q.correct_answer,
            q.explanation,
            q.difficulty,
            q.option_a,
            q.option_b,
            q.option_c,
            q.option_d,
            q.knowledge_point,
            ar.answer_result,
            ar.student_answer,
            ar.created_at,
            ar.ai_analysis,
            ar.duration,
            ar.score
        FROM answer_record ar
        INNER JOIN question q ON ar.question_id = q.question_id
        <where>
            ar.student_id = #{studentId}
            AND q.group_id = #{questionGroupId}
            <if test="questionType != null and questionType != ''">
                AND q.question_type = #{questionType}
            </if>
        </where>
        </script>
        """)
    IPage<StudentAnswerResult> searchAnswerRecord(Page<StudentAnswerResult> page,
                                                @Param("studentId") Integer studentId,
                                                @Param("questionGroupId") Integer questionGroupId,
                                                @Param("questionType") String questionType);

    @Select("""
        <script>
        SELECT AVG(ar.duration) 
        FROM answer_record ar 
        INNER JOIN question q ON ar.question_id = q.question_id 
        WHERE ar.student_id = #{studentId} 
        AND q.group_id = #{groupId}
        <if test="questionType != null and questionType != ''">
            AND q.question_type = #{questionType}
        </if>
        </script>
        """)
    Double getAverageAnswerTime(
        @Param("studentId") Integer studentId, 
        @Param("groupId") Integer groupId,
        @Param("questionType") String questionType);

    @Select("""
        SELECT 
            COUNT(CASE WHEN ar.answer_result = 'Right' THEN 1 END) * 100.0 / 
                NULLIF(COUNT(CASE WHEN q.question_type IN ('单选', '多选', '填空') THEN 1 END), 0) as total_rate,
            COUNT(CASE WHEN q.question_type = '单选' AND ar.answer_result = 'Right' THEN 1 END) * 100.0 / 
                NULLIF(COUNT(CASE WHEN q.question_type = '单选' THEN 1 END), 0) as single_rate,
            COUNT(CASE WHEN q.question_type = '多选' AND ar.answer_result = 'Right' THEN 1 END) * 100.0 / 
                NULLIF(COUNT(CASE WHEN q.question_type = '多选' THEN 1 END), 0) as multiple_rate,
            COUNT(CASE WHEN q.question_type = '填空' AND ar.answer_result = 'Right' THEN 1 END) * 100.0 / 
                NULLIF(COUNT(CASE WHEN q.question_type = '填空' THEN 1 END), 0) as fill_rate
        FROM answer_record ar 
        INNER JOIN question q ON ar.question_id = q.question_id 
        WHERE ar.student_id = #{studentId} 
        AND q.group_id = #{groupId}
        AND ar.answer_result IS NOT NULL
        """)
    AccuracyRateDTO getAccuracyRates(
        @Param("studentId") Integer studentId, 
        @Param("groupId") Integer groupId);

    @Select("""
        SELECT 
            AVG(ar.score) as total_avg_score,
            AVG(CASE WHEN q.question_type = '单选' THEN ar.score END) as single_avg_score,
            AVG(CASE WHEN q.question_type = '多选' THEN ar.score END) as multiple_avg_score,
            AVG(CASE WHEN q.question_type = '填空' THEN ar.score END) as fill_avg_score,
            AVG(CASE WHEN q.question_type = '简答' THEN ar.score END) as short_avg_score
        FROM answer_record ar 
        INNER JOIN question q ON ar.question_id = q.question_id 
        WHERE ar.student_id = #{studentId} 
        AND q.group_id = #{groupId}
        AND ar.score IS NOT NULL
        """)
    AverageScoreDTO getAverageScores(
        @Param("studentId") Integer studentId, 
        @Param("groupId") Integer groupId);

    @Select("""
        SELECT 
            AVG(ar.duration) as total_avg_time,
            AVG(CASE WHEN q.question_type = '单选' THEN ar.duration END) as single_avg_time,
            AVG(CASE WHEN q.question_type = '多选' THEN ar.duration END) as multiple_avg_time,
            AVG(CASE WHEN q.question_type = '填空' THEN ar.duration END) as fill_avg_time,
            AVG(CASE WHEN q.question_type = '简答' THEN ar.duration END) as short_avg_time
        FROM answer_record ar 
        INNER JOIN question q ON ar.question_id = q.question_id 
        WHERE ar.student_id = #{studentId} 
        AND q.group_id = #{groupId}
        AND ar.duration IS NOT NULL
        """)
    AverageTimeDTO getAverageTimes(
        @Param("studentId") Integer studentId, 
        @Param("groupId") Integer groupId);

    @Select("""
    SELECT * FROM (
        (
            SELECT 
                q.question_type,
                q.question_text,
                q.correct_answer,
                q.explanation,
                q.difficulty,
                q.option_a,
                q.option_b,
                q.option_c,
                q.option_d,
                q.knowledge_point,
                ar.answer_result,
                ar.student_answer,
                ar.created_at,
                ar.ai_analysis,
                ar.duration,
                ar.score,
                'rightMaxTime' as result_type
            FROM answer_record ar
            INNER JOIN question q ON ar.question_id = q.question_id
            WHERE ar.student_id = #{studentId} 
            AND q.group_id = #{groupId}
            AND ar.answer_result = 'Right'
            ORDER BY ar.duration DESC
            LIMIT 1
        )
        
        UNION ALL
        
        (
            SELECT 
                q.question_type,
                q.question_text,
                q.correct_answer,
                q.explanation,
                q.difficulty,
                q.option_a,
                q.option_b,
                q.option_c,
                q.option_d,
                q.knowledge_point,
                ar.answer_result,
                ar.student_answer,
                ar.created_at,
                ar.ai_analysis,
                ar.duration,
                ar.score,
                'rightMinTime' as result_type
            FROM answer_record ar
            INNER JOIN question q ON ar.question_id = q.question_id
            WHERE ar.student_id = #{studentId} 
            AND q.group_id = #{groupId}
            AND ar.answer_result = 'Right'
            ORDER BY ar.duration ASC
            LIMIT 1
        )
        
        UNION ALL
        
        (
            SELECT 
                q.question_type,
                q.question_text,
                q.correct_answer,
                q.explanation,
                q.difficulty,
                q.option_a,
                q.option_b,
                q.option_c,
                q.option_d,
                q.knowledge_point,
                ar.answer_result,
                ar.student_answer,
                ar.created_at,
                ar.ai_analysis,
                ar.duration,
                ar.score,
                'wrongMaxTime' as result_type
            FROM answer_record ar
            INNER JOIN question q ON ar.question_id = q.question_id
            WHERE ar.student_id = #{studentId} 
            AND q.group_id = #{groupId}
            AND ar.answer_result = 'Wrong'
            ORDER BY ar.duration DESC
            LIMIT 1
        )
        
        UNION ALL
        
        (
            SELECT 
                q.question_type,
                q.question_text,
                q.correct_answer,
                q.explanation,
                q.difficulty,
                q.option_a,
                q.option_b,
                q.option_c,
                q.option_d,
                q.knowledge_point,
                ar.answer_result,
                ar.student_answer,
                ar.created_at,
                ar.ai_analysis,
                ar.duration,
                ar.score,
                'wrongMinTime' as result_type
            FROM answer_record ar
            INNER JOIN question q ON ar.question_id = q.question_id
            WHERE ar.student_id = #{studentId} 
            AND q.group_id = #{groupId}
            AND ar.answer_result = 'Wrong'
            ORDER BY ar.duration ASC
            LIMIT 1
        )
    ) results
    """)

    List<StudentAnswerResult> getExtremeTimeRecords(
        @Param("studentId") Integer studentId,
        @Param("groupId") Integer groupId);

    @Select("""
        SELECT 
            q.knowledge_point as knowledge_point,
            AVG(ar.score) as average_score
        FROM answer_record ar
        INNER JOIN question q ON ar.question_id = q.question_id
        WHERE ar.student_id = #{studentId} 
        AND q.group_id = #{groupId}
        AND q.knowledge_point IS NOT NULL
        AND q.knowledge_point != ''
        GROUP BY q.knowledge_point
        """)
    List<KnowledgePointScoreDTO> getKnowledgePointScores(
        @Param("studentId") Integer studentId,
        @Param("groupId") Integer groupId);
} 