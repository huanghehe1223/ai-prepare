package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.QuestionGroup;

import java.util.List;

public interface QuestionGroupService extends IService<QuestionGroup> {

    QuestionGroup getQuestionGroupById(Integer id);

    boolean saveQuestionGroup(QuestionGroup questionGroup);

    boolean updateQuestionGroup(QuestionGroup questionGroup);

    boolean deleteQuestionGroup(Integer id);
    
    List<QuestionGroup> getQuestionGroupByIds(List<Integer> ids);
    
    List<QuestionGroup> getQuestionGroupsByProjectId(Integer projectId);
} 