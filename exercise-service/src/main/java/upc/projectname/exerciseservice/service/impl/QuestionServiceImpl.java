package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.Question;
import upc.projectname.exerciseservice.mapper.QuestionMapper;
import upc.projectname.exerciseservice.service.QuestionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Override
    public Question getQuestionById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveQuestion(Question question) {
        return this.save(question);
    }

    @Override
    public boolean updateQuestion(Question question) {
        return this.updateById(question);
    }

    @Override
    public boolean deleteQuestion(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<Question> getQuestionByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<Question> getQuestionsByGroupId(Integer groupId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getGroupId, groupId);
        return this.list(wrapper);
    }

    @Override
    public IPage<Question> getQuestionsByGroupIdAndPage(Page<Question> page, Integer groupId,String questionType){
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getGroupId, groupId)
                .eq(questionType != null && !questionType.isEmpty(), Question::getQuestionType, questionType);
        return this.page(page, wrapper);
    }

    @Override
    public Boolean saveQuestions(List<Question> questions) {
        return this.saveBatch(questions);
    }

    @Override
    public List<Integer> getQuestionIdsByGroupId(Integer questionGroupId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getGroupId, questionGroupId);
        List<Question> questions = this.list(wrapper);
        return questions.stream().map(Question::getQuestionId).collect(Collectors.toList());
    }
} 