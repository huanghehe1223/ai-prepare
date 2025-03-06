package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.AnswerRecord;

import java.util.List;

public interface AnswerRecordService extends IService<AnswerRecord> {

    AnswerRecord getAnswerRecordById(Integer id);

    boolean saveAnswerRecord(AnswerRecord record);

    boolean updateAnswerRecord(AnswerRecord record);

    boolean deleteAnswerRecord(Integer id);
    
    List<AnswerRecord> getAnswerRecordByIds(List<Integer> ids);
    
    List<AnswerRecord> getAnswerRecordsByStudentId(Integer studentId);
    
    List<AnswerRecord> getAnswerRecordsByQuestionId(Integer questionId);
    
    List<AnswerRecord> getAnswerRecordsByStudentAndQuestion(Integer studentId, Integer questionId);

    boolean saveOrUpdateBatchAnswerRecords(List<AnswerRecord> records, Integer studentId);
} 