package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    IPage<Question> getQuestionsByGroupIdAndPage(Page<Question> page, Integer groupId,String questionType);

    Boolean saveQuestions(List<Question> questions);

    boolean deleteQuestionsByGroupId(Integer groupId);
} 