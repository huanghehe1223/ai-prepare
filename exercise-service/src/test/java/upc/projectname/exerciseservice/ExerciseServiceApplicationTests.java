package upc.projectname.exerciseservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import upc.projectname.exerciseservice.service.QuestionService;
import upc.projectname.upccommon.api.client.StudentClient;
import upc.projectname.upccommon.domain.po.Question;
import upc.projectname.upccommon.domain.po.Student;

@SpringBootTest
class ExerciseServiceApplicationTests {
	@Autowired
	StudentClient studentClient;

	@Test
	void contextLoads() {
		Student student = studentClient.getStudent(3).getData();
	}

	@Autowired
	private QuestionService questionService;

	@Test
	void testDatabaseString() {
		Question questionById = questionService.getQuestionById(134);
		System.out.println(questionById.getOptionA());

	}



}
