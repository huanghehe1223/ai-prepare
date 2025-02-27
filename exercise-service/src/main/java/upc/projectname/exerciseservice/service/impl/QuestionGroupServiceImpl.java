package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upc.projectname.exerciseservice.service.QuestionGroupStatusService;
import upc.projectname.upccommon.domain.po.QuestionGroup;
import upc.projectname.exerciseservice.mapper.QuestionGroupMapper;
import upc.projectname.exerciseservice.service.QuestionGroupService;
import upc.projectname.upccommon.domain.po.QuestionGroupStatus;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionGroupServiceImpl extends ServiceImpl<QuestionGroupMapper, QuestionGroup> implements QuestionGroupService {
    private final QuestionGroupStatusService questionGroupStatusService;


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

    @Override
    public List<QuestionGroup> searchStudentGroup(Integer projectId, Integer studentId, String status, String groupType) {
        LambdaQueryWrapper<QuestionGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionGroup::getProjectId, projectId);
        wrapper.eq(QuestionGroup::getGroupType, groupType);
        wrapper.eq(QuestionGroup::getGroupStatus, 1); //查找已经发布的习题组
        List<QuestionGroup> questionGroups = this.list(wrapper);
        if (questionGroups.isEmpty()){
            return questionGroups;
        }
        if (status.equals("Incomplete")){
            Iterator<QuestionGroup> iterator = questionGroups.iterator();
            while (iterator.hasNext()) {
                QuestionGroup group = iterator.next();
                List<QuestionGroupStatus> statusByGroupId = questionGroupStatusService.getStatusByGroupIdAndStudentId(group.getGroupId(), studentId);
                if (!statusByGroupId.isEmpty()){
                    iterator.remove();
                }
            }
            return questionGroups;
        }
        return this.baseMapper.searchStudentGroup(projectId, studentId, status, groupType);
    }
}