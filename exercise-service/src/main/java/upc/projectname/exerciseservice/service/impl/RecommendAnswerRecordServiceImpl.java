package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.RecommendAnswerRecord;
import upc.projectname.exerciseservice.mapper.RecommendAnswerRecordMapper;
import upc.projectname.exerciseservice.service.RecommendAnswerRecordService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecommendAnswerRecordServiceImpl extends ServiceImpl<RecommendAnswerRecordMapper, RecommendAnswerRecord> implements RecommendAnswerRecordService {

    @Override
    public RecommendAnswerRecord getRecommendAnswerRecordById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveRecommendAnswerRecord(RecommendAnswerRecord record) {
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return this.save(record);
    }

    @Override
    public boolean updateRecommendAnswerRecord(RecommendAnswerRecord record) {
        record.setUpdatedAt(LocalDateTime.now());
        return this.updateById(record);
    }

    @Override
    public boolean deleteRecommendAnswerRecord(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<RecommendAnswerRecord> getRecommendAnswerRecordByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<RecommendAnswerRecord> getRecommendAnswerRecordsByQuestionId(Integer questionId) {
        LambdaQueryWrapper<RecommendAnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendAnswerRecord::getQuestionId, questionId)
              .orderByDesc(RecommendAnswerRecord::getCreatedAt);
        return this.list(wrapper);
    }
} 