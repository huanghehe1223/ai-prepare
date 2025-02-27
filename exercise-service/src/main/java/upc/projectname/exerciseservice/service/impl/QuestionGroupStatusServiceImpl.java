package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.QuestionGroupStatus;
import upc.projectname.exerciseservice.mapper.QuestionGroupStatusMapper;
import upc.projectname.exerciseservice.service.QuestionGroupStatusService;

import java.util.List;

@Service
public class QuestionGroupStatusServiceImpl extends ServiceImpl<QuestionGroupStatusMapper, QuestionGroupStatus> implements QuestionGroupStatusService {

    @Override
    public QuestionGroupStatus getQuestionGroupStatusById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveQuestionGroupStatus(QuestionGroupStatus status) {
        return this.save(status);
    }

    @Override
    public boolean updateQuestionGroupStatus(QuestionGroupStatus status) {
        return this.updateById(status);
    }

    @Override
    public boolean deleteQuestionGroupStatus(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<QuestionGroupStatus> getQuestionGroupStatusByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<QuestionGroupStatus> getStatusByStudentId(Integer studentId) {
        LambdaQueryWrapper<QuestionGroupStatus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionGroupStatus::getStudentId, studentId);
        return this.list(wrapper);
    }
    
    @Override
    public List<QuestionGroupStatus> getStatusByGroupId(Integer groupId) {
        LambdaQueryWrapper<QuestionGroupStatus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionGroupStatus::getGroupId, groupId);
        return this.list(wrapper);
    }

    @Override
    public List<QuestionGroupStatus> getStatusByGroupIdAndStudentId(Integer groupId, Integer studentId) {
        LambdaQueryWrapper<QuestionGroupStatus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionGroupStatus::getGroupId, groupId)
              .eq(QuestionGroupStatus::getStudentId, studentId);
        return this.list(wrapper);
    }
} 