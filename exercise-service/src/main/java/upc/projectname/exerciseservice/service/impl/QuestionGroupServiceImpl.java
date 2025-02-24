package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.QuestionGroup;
import upc.projectname.exerciseservice.mapper.QuestionGroupMapper;
import upc.projectname.exerciseservice.service.QuestionGroupService;

import java.util.List;

@Service
public class QuestionGroupServiceImpl extends ServiceImpl<QuestionGroupMapper, QuestionGroup> implements QuestionGroupService {

    @Override
    public QuestionGroup getQuestionGroupById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveQuestionGroup(QuestionGroup questionGroup) {
        return this.save(questionGroup);
    }

    @Override
    public boolean updateQuestionGroup(QuestionGroup questionGroup) {
        return this.updateById(questionGroup);
    }

    @Override
    public boolean deleteQuestionGroup(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<QuestionGroup> getQuestionGroupByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<QuestionGroup> getQuestionGroupsByProjectId(Integer projectId) {
        LambdaQueryWrapper<QuestionGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionGroup::getProjectId, projectId);
        return this.list(wrapper);
    }
} 