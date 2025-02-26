package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.RecommendQuestionGroup;
import upc.projectname.exerciseservice.mapper.RecommendQuestionGroupMapper;
import upc.projectname.exerciseservice.service.RecommendQuestionGroupService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecommendQuestionGroupServiceImpl extends ServiceImpl<RecommendQuestionGroupMapper, RecommendQuestionGroup> implements RecommendQuestionGroupService {

    @Override
    public RecommendQuestionGroup getRecommendQuestionGroupById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveRecommendQuestionGroup(RecommendQuestionGroup group) {
        group.setCreateTime(LocalDateTime.now());
        return this.save(group);
    }

    @Override
    public boolean updateRecommendQuestionGroup(RecommendQuestionGroup group) {
        group.setCreateTime(LocalDateTime.now());
        return this.updateById(group);
    }

    @Override
    public boolean deleteRecommendQuestionGroup(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<RecommendQuestionGroup> getRecommendQuestionGroupByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<RecommendQuestionGroup> getRecommendQuestionGroupsByStudentId(Integer studentId) {
        LambdaQueryWrapper<RecommendQuestionGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendQuestionGroup::getStudentId, studentId)
              .orderByDesc(RecommendQuestionGroup::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Boolean submitRecommendQuestionGroup(Integer groupId) {
        LambdaUpdateWrapper<RecommendQuestionGroup> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RecommendQuestionGroup::getGroupId, groupId)
                .set(RecommendQuestionGroup::getStatus, "Done");

        return this.update(updateWrapper);
    }
} 