package upc.projectname.exerciseservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.AnswerRecord;
import upc.projectname.upccommon.domain.po.Student;

import java.util.List;

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
            ar.ai_analysis
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

//    List<StudentAnswerResult> statisticsStudentAnswerAccuracy(Integer studentId, Integer questionGroupId);


} 