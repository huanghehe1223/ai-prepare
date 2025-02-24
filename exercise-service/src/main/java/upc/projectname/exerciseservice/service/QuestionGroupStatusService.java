package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.QuestionGroupStatus;

import java.util.List;

public interface QuestionGroupStatusService extends IService<QuestionGroupStatus> {

    QuestionGroupStatus getQuestionGroupStatusById(Integer id);

    boolean saveQuestionGroupStatus(QuestionGroupStatus status);

    boolean updateQuestionGroupStatus(QuestionGroupStatus status);

    boolean deleteQuestionGroupStatus(Integer id);
    
    List<QuestionGroupStatus> getQuestionGroupStatusByIds(List<Integer> ids);
    
    List<QuestionGroupStatus> getStatusByStudentId(Integer studentId);
    
    List<QuestionGroupStatus> getStatusByGroupId(Integer groupId);
} 