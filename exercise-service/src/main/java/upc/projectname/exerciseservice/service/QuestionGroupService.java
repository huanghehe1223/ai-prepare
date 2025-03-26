package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.QuestionGroup;

import java.util.List;

public interface QuestionGroupService extends IService<QuestionGroup> {

    QuestionGroup getQuestionGroupById(Integer id);

    QuestionGroup saveQuestionGroup(QuestionGroup questionGroup);

    boolean updateQuestionGroup(QuestionGroup questionGroup);

    boolean deleteQuestionGroup(Integer id);
    
    List<QuestionGroup> getQuestionGroupByIds(List<Integer> ids);
    
    List<QuestionGroup> getQuestionGroupsByProjectId(Integer projectId);

    List<QuestionGroup> searchStudentGroup(Integer projectId, Integer studentId, String status, String groupType);

    List<QuestionGroup> searchQuestionGroupByPage(Integer projectId, Integer status,String type);

    boolean updateQuestionGroupStatus(Integer groupId, Integer status);
}