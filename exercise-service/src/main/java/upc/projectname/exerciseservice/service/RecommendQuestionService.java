package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.RecommendQuestion;

import java.util.List;

public interface RecommendQuestionService extends IService<RecommendQuestion> {

    RecommendQuestion getRecommendQuestionById(Integer id);

    boolean saveRecommendQuestion(RecommendQuestion question);

    boolean updateRecommendQuestion(RecommendQuestion question);

    boolean deleteRecommendQuestion(Integer id);
    
    List<RecommendQuestion> getRecommendQuestionByIds(List<Integer> ids);
    
    List<RecommendQuestion> getRecommendQuestionsByGroupId(Integer groupId);
} 