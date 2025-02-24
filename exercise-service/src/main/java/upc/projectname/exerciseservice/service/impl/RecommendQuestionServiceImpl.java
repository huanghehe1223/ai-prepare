package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.RecommendQuestion;
import upc.projectname.exerciseservice.mapper.RecommendQuestionMapper;
import upc.projectname.exerciseservice.service.RecommendQuestionService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecommendQuestionServiceImpl extends ServiceImpl<RecommendQuestionMapper, RecommendQuestion> implements RecommendQuestionService {

    @Override
    public RecommendQuestion getRecommendQuestionById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveRecommendQuestion(RecommendQuestion question) {
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        return this.save(question);
    }

    @Override
    public boolean updateRecommendQuestion(RecommendQuestion question) {
        question.setUpdatedAt(LocalDateTime.now());
        return this.updateById(question);
    }

    @Override
    public boolean deleteRecommendQuestion(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<RecommendQuestion> getRecommendQuestionByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<RecommendQuestion> getRecommendQuestionsByGroupId(Integer groupId) {
        LambdaQueryWrapper<RecommendQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendQuestion::getGroupId, groupId);
        return this.list(wrapper);
    }
} 