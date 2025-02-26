package upc.projectname.userclassservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import upc.projectname.upccommon.domain.po.Class;

import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<Class> {

    @Select("""
    <script>
    SELECT DISTINCT
        c.*
    FROM class c
    INNER JOIN class_student cs ON c.class_id = cs.class_id
    <where>
        cs.student_id = #{studentId}
        AND cs.status IS NOT NULL
        <if test="className != null and className != ''">
            AND c.class_name LIKE CONCAT('%', #{className}, '%')
        </if>
        <if test="courseName != null and courseName != ''">
            AND c.course_name LIKE CONCAT('%', #{courseName}, '%')
        </if>
    </where>
    ORDER BY c.class_id DESC
    </script>
    """)
    IPage<Class> getClassesByStudentId(Page<Class> page,
                                       @Param("studentId") Integer studentId,
                                       @Param("className") String className,
                                       @Param("courseName") String courseName);
} 