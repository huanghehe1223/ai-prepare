package upc.projectname.exerciseservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import upc.projectname.exerciseservice.service.QuestionService;
import upc.projectname.upccommon.domain.po.Question;

@SpringBootTest
class ExerciseServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private QuestionService questionService;

	@Test
	void testDatabaseString() {
		Question questionById = questionService.getQuestionById(134);
		System.out.println(questionById.getOptionA());

	}



}
