package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.AnswerRecord;
import upc.projectname.upccommon.domain.dto.AccuracyRateDTO;
import upc.projectname.upccommon.domain.dto.AverageScoreDTO;
import upc.projectname.upccommon.domain.dto.AverageTimeDTO;
import upc.projectname.upccommon.domain.dto.ExtremeAnswerTimeDTO;
import upc.projectname.upccommon.domain.dto.KnowledgePointScoreDTO;

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

    AccuracyRateDTO getAccuracyRate(Integer studentId, Integer groupId);

    AverageScoreDTO getAverageScore(Integer studentId, Integer groupId);

    AverageTimeDTO getAverageTime(Integer studentId, Integer groupId);

    ExtremeAnswerTimeDTO getExtremeTimeRecords(Integer studentId, Integer groupId);

    List<KnowledgePointScoreDTO> getKnowledgePointScores(Integer studentId, Integer groupId);
}