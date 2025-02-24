package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.Question;
import upc.projectname.exerciseservice.mapper.QuestionMapper;
import upc.projectname.exerciseservice.service.QuestionService;

import java.util.List;

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
} 