package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.AnswerRecord;
import upc.projectname.exerciseservice.mapper.AnswerRecordMapper;
import upc.projectname.exerciseservice.service.AnswerRecordService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnswerRecordServiceImpl extends ServiceImpl<AnswerRecordMapper, AnswerRecord> implements AnswerRecordService {

    @Override
    public AnswerRecord getAnswerRecordById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveAnswerRecord(AnswerRecord record) {
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return this.save(record);
    }

    @Override
    public boolean updateAnswerRecord(AnswerRecord record) {
        record.setUpdatedAt(LocalDateTime.now());
        return this.updateById(record);
    }

    @Override
    public boolean deleteAnswerRecord(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<AnswerRecord> getAnswerRecordByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<AnswerRecord> getAnswerRecordsByStudentId(Integer studentId) {
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getStudentId, studentId)
              .orderByDesc(AnswerRecord::getCreatedAt);
        return this.list(wrapper);
    }
    
    @Override
    public List<AnswerRecord> getAnswerRecordsByQuestionId(Integer questionId) {
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getQuestionId, questionId);
        return this.list(wrapper);
    }
    
    @Override
    public List<AnswerRecord> getAnswerRecordsByStudentAndQuestion(Integer studentId, Integer questionId) {
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getStudentId, studentId)
              .eq(AnswerRecord::getQuestionId, questionId);
        return this.list(wrapper);
    }

    @Override
    public IPage<StudentAnswerResult> searchAnswerRecord(Integer current, Integer size, Integer studentId, Integer questionGroupId, String questionType) {
        Page<StudentAnswerResult> page = new Page<>(current, size);
        return this.baseMapper.searchAnswerRecord(page, studentId, questionGroupId, questionType);
    }
}