package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
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

    IPage<StudentAnswerResult> searchAnswerRecord(Integer current, Integer size, Integer studentId, Integer questionGroupId, String questionType);

    boolean saveOrUpdateBatchAnswerRecords(List<AnswerRecord> records, Integer studentId);

    List<AnswerRecord> searchByQuestionGroupId(Integer questionGroupId, Integer studentId);

    IPage<AnswerRecord> searchByStudentIdAndQuestionGroupId(Integer studentId, Integer questionGroupId, Page<AnswerRecord> page, String sortType, String answerResult);

    /**
     * 获取某个习题组的平均做题时间
     * @param questionGroupId 习题组ID
     * @return 平均做题时间（秒）
     */
    Double getAverageTimeByGroupId(Integer questionGroupId);

//    List<StudentAnswerResult> statisticsStudentAnswerAccuracy(Integer studentId, Integer questionGroupId);
}