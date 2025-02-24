package upc.projectname.userclassservice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import upc.projectname.upccommon.api.client.StudentClient;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Student;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class UserClassServiceApplicationTests {

    @Autowired
    private StudentClient studentClient;

    private Student testStudent;

//    @BeforeEach
//    void setUp() {
//        // 准备测试数据
//        testStudent = new Student();
//        testStudent.setStudentName("测试学生");
//        testStudent.setUserName("testuser");
//        testStudent.setPassword("123456");
//        testStudent.setImageUrl("http://example.com/image.jpg");
//    }

    @Test
    void testSaveStudent() {
//        testStudent = new Student();
//        testStudent.setStudentName("测试学生");
//        testStudent.setUserName("testuser");
//        testStudent.setPassword("123456");
//        testStudent.setImageUrl("http://example.com/image.jpg");
//
//        // 测试保存学生
//        Result<Boolean> saveResult = studentClient.saveStudent(testStudent);
//        assertTrue(saveResult.getCode() == 1);
//        assertTrue(saveResult.getData());
        Result<Student> studentResult = studentClient.getStudent(2);
        System.out.println("student: " + studentResult.getData());


    }

    @Test
    void testGetStudent() {
        // 先保存一个学生
        studentClient.saveStudent(testStudent);
        Integer studentId = testStudent.getStudentId();

        // 测试查询学生
        Result<Student> getResult = studentClient.getStudent(studentId);
        assertTrue(getResult.getCode() == 1);
        assertNotNull(getResult.getData());
        assertEquals(testStudent.getStudentName(), getResult.getData().getStudentName());

        // 测试查询不存在的学生
        Result<Student> notFoundResult = studentClient.getStudent(-1);
        assertTrue(notFoundResult.getCode() == 0);
        assertNull(notFoundResult.getData());
    }

    @Test
    void testGetStudentByIds() {
        // 先保存两个学生
//        Student student1 = new Student(null, "学生1", "user1", "pass1", "url1");
//        Student student2 = new Student(null, "学生2", "user2", "pass2", "url2");
//        studentClient.saveStudent(student1);
//        studentClient.saveStudent(student2);

        // 测试批量查询
        List<Integer> ids = Arrays.asList(1,2);
        Result<List<Student>> batchResult = studentClient.getStudentByIds(ids);
        assertTrue(batchResult.getCode() == 1);
        assertEquals(2, batchResult.getData().size());
        System.out.println("batchResult: " + batchResult.getData());

        // 测试查询不存在的ID
        List<Integer> notFoundIds = Arrays.asList(-1, -2);
        Result<List<Student>> notFoundResult = studentClient.getStudentByIds(notFoundIds);
        assertTrue(notFoundResult.getCode() == 0);
    }



    @Test
    void testDeleteStudent() {


        // 测试删除学生
        Result<Boolean> deleteResult = studentClient.deleteStudent(2);
        assertTrue(deleteResult.getCode() == 1);
        assertTrue(deleteResult.getData());

        // 验证已删除
        Result<Student> getResult = studentClient.getStudent(2);
        assertTrue(getResult.getCode() == 0);
        assertNull(getResult.getData());
    }


}