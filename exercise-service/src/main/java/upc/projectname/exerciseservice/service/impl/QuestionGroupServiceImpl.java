package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
        if (status.equals("Incomplete")){
            LambdaQueryWrapper<QuestionGroup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(QuestionGroup::getProjectId, projectId);
            wrapper.eq(groupType!=null&&!groupType.isEmpty(),QuestionGroup::getGroupType, groupType);
            wrapper.eq(QuestionGroup::getGroupStatus, 1); //查找已经发布的习题组
            List<QuestionGroup> questionGroups = this.list(wrapper);
            if (questionGroups.isEmpty()){
                return questionGroups;
            }
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


    @Override
    public List<QuestionGroup> searchQuestionGroupByPage(Integer projectId, Integer status, String type) {
        LambdaQueryWrapper<QuestionGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, QuestionGroup::getProjectId, projectId)
                .eq(status != null, QuestionGroup::getGroupStatus, status)
                .eq(type != null, QuestionGroup::getGroupType, type);
        return this.list(wrapper);
    }

    @Override
    public boolean updateQuestionGroupStatus(Integer groupId, Integer status) {
        LambdaUpdateWrapper<QuestionGroup> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(QuestionGroup::getGroupId, groupId).set(QuestionGroup::getGroupStatus, status);
        return this.update(wrapper);
    }
}