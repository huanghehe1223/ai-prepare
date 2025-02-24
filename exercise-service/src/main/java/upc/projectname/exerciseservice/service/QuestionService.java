package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.Question;

import java.util.List;

public interface QuestionService extends IService<Question> {

    Question getQuestionById(Integer id);

    boolean saveQuestion(Question question);

    boolean updateQuestion(Question question);

    boolean deleteQuestion(Integer id);
    
    List<Question> getQuestionByIds(List<Integer> ids);
    
    List<Question> getQuestionsByGroupId(Integer groupId);
} 