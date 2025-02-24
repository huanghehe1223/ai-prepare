package upc.projectname.exerciseservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.RecommendAnswerRecord;

import java.util.List;

public interface RecommendAnswerRecordService extends IService<RecommendAnswerRecord> {

    RecommendAnswerRecord getRecommendAnswerRecordById(Integer id);

    boolean saveRecommendAnswerRecord(RecommendAnswerRecord record);

    boolean updateRecommendAnswerRecord(RecommendAnswerRecord record);

    boolean deleteRecommendAnswerRecord(Integer id);
    
    List<RecommendAnswerRecord> getRecommendAnswerRecordByIds(List<Integer> ids);
    
    List<RecommendAnswerRecord> getRecommendAnswerRecordsByQuestionId(Integer questionId);
} 