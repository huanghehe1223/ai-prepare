package upc.projectname.userclassservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import upc.projectname.upccommon.domain.po.Student;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生信息
     *
     * @param page        分页参数
     * @param studentName 学生姓名(可选)
     * @param classId     班级ID(可选)
     * @param status      状态(可选)
     * @return 分页结果
     */

    // todo 
    // DISTINCT 会不会影响pagesize
    //多表查询可能的问题，1 一对多重复 2 一对0缺失 3 distinct影响pagesize

    //todo
//    // 获取当前页数据列表
//    List<Student> records = page.getRecords();
//    // 获取总记录数
//    long total = page.getTotal();
//    // 获取当前页码
//    long current = page.getCurrent();
//    // 获取每页大小
//    long size = page.getSize();
//    // 获取总页数
//    long pages = page.getPages();
    @Select("""
            <script>
            SELECT DISTINCT
                s.student_id,
                s.student_name,
                s.user_name,
                s.image_url
            FROM student s
            LEFT JOIN class_student cs ON s.student_id = cs.student_id
            <where>
                <if test="classId != null">
                    cs.class_id = #{classId}
                </if>
                <if test="studentName != null and studentName != ''">
                    AND s.student_name LIKE CONCAT('%', #{studentName}, '%')
                </if>
                <if test="sex != null and sex != ''">
                    AND s.sex =#{sex}
                </if>
                <if test="status != null and status != ''">
                    AND cs.status = #{status}
                </if>
            </where>
            ORDER BY s.student_id DESC
            </script>
            """)
    IPage<Student> selectStudentPage(Page<Student> page,
                                     @Param("studentName") String studentName,
                                     @Param("sex") String sex,
                                     @Param("classId") Integer classId,
                                     @Param("status") String status);

    @Select("""
            <script>
            SELECT DISTINCT
                s.student_id,
                s.student_name,
                s.user_name,
                s.image_url
            FROM student s
            INNER JOIN class_student cs ON s.student_id = cs.student_id
            <where>
                cs.class_id = #{classId}
                <if test="studentName != null and studentName != ''">
                    AND s.student_name LIKE CONCAT('%', #{studentName}, '%')
                </if>
                <if test="sex != null and sex != ''">
                    AND s.sex = #{sex}
                </if>
                <if test="status != null and status != ''">
                    AND cs.status = #{status}
                </if>
            </where>
            ORDER BY s.student_id DESC
            </script>
            """)
    IPage<Student> selectStudentsByClassIdAndPage(
            Page<Student> page,
            @Param("studentName") String studentName,
            @Param("classId") Integer classId,
            @Param("sex") String sex,
            @Param("status") String status
    );
}