package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.RecommendQuestionGroup;

import java.util.List;

public interface RecommendQuestionGroupService extends IService<RecommendQuestionGroup> {

    //todo 
    // studentId,projectId,一起获取recommendQuestionGroup

    RecommendQuestionGroup getRecommendQuestionGroupById(Integer id);

    boolean saveRecommendQuestionGroup(RecommendQuestionGroup group);

    boolean updateRecommendQuestionGroup(RecommendQuestionGroup group);

    boolean deleteRecommendQuestionGroup(Integer id);
    
    List<RecommendQuestionGroup> getRecommendQuestionGroupByIds(List<Integer> ids);
    
    List<RecommendQuestionGroup> getRecommendQuestionGroupsByStudentId(Integer studentId);
} 