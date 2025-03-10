package upc.projectname.exerciseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.tools.jconsole.JConsoleContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upc.projectname.exerciseservice.service.QuestionService;
import upc.projectname.upccommon.domain.dto.StudentAnswerResult;
import upc.projectname.upccommon.domain.po.AnswerRecord;
import upc.projectname.exerciseservice.mapper.AnswerRecordMapper;
import upc.projectname.exerciseservice.service.AnswerRecordService;

import java.io.Console;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AnswerRecordServiceImpl extends ServiceImpl<AnswerRecordMapper, AnswerRecord> implements AnswerRecordService {

    private final QuestionService questionService;


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


    @Override
    public boolean saveOrUpdateBatchAnswerRecords(List<AnswerRecord> records, Integer studentId) {
        if (records == null || records.isEmpty() || studentId == null) {
            return false;
        }

        for (AnswerRecord record : records) {
            record.setStudentId(studentId);
            LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AnswerRecord::getQuestionId, record.getQuestionId())
                    .eq(AnswerRecord::getStudentId, studentId);

            AnswerRecord existingRecord = this.getOne(wrapper);
            if (existingRecord != null) {
                record.setRecordId(existingRecord.getRecordId());
                record.setUpdatedAt(LocalDateTime.now());
                this.updateById(record);
            } else {
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());
                this.save(record);
            }
        }
        return true;
    }

    @Override
    public List<AnswerRecord> searchByQuestionGroupId(Integer questionGroupId, Integer studentId) {
        // 1. 通过QuestionService获取问题组下的所有questionId
        List<Integer> questionIds = questionService.getQuestionIdsByGroupId(questionGroupId);

        // 如果没有找到相关问题，直接返回空列表
        if (questionIds == null || questionIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 构建查询条件
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();

        // 使用in条件查询所有关联问题的答题记录
        wrapper.in(AnswerRecord::getQuestionId, questionIds);

        // 3. 当studentId不为空时，添加学生ID条件
        if (studentId != null) {
            wrapper.eq(AnswerRecord::getStudentId, studentId);
        }

        // 4. 选择性获取字段（根据需要可以自定义）
        wrapper.select(
//                AnswerRecord::getRecordId,
                AnswerRecord::getQuestionId,
//                AnswerRecord::getStudentId,
//                AnswerRecord::getAnswerResult,
//                AnswerRecord::getCreatedAt,
                AnswerRecord::getScore
                // 可根据需要添加或移除字段
        );

        // 5. 执行查询并返回结果
        return this.list(wrapper);
    }

    @Override
    public IPage<AnswerRecord> searchByStudentIdAndQuestionGroupId(Integer studentId, Integer questionGroupId, Page<AnswerRecord> page, String sortType, String answerResult) {
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();

        // 设置学生ID查询条件（必须）
        wrapper.eq(AnswerRecord::getStudentId, studentId);

        // 如果提供了习题组ID，通过QuestionService获取问题ID列表进行过滤
        if (questionGroupId != null) {
            List<Integer> questionIds = questionService.getQuestionIdsByGroupId(questionGroupId);
            if (questionIds != null && !questionIds.isEmpty()) {
                wrapper.in(AnswerRecord::getQuestionId, questionIds);
            } else {
                return new Page<>();
            }
        }

        // 根据answerResult参数筛选正确或错误的答案
        if (answerResult != null && !answerResult.isEmpty()) {
            wrapper.eq(AnswerRecord::getAnswerResult, answerResult);
        }

        // 根据传入的排序类型决定排序方式
        if ("asc".equals(sortType)) {
            // 最短时间在前
            wrapper.orderByAsc(AnswerRecord::getDuration);
        } else {
            // 最短时间在前
            wrapper.orderByDesc(AnswerRecord::getDuration);
        }

        // 选择需要的字段
        wrapper.select(
                AnswerRecord::getRecordId,
                AnswerRecord::getQuestionId,
                AnswerRecord::getStudentId,
                AnswerRecord::getStudentAnswer,
                AnswerRecord::getAnswerResult,
                AnswerRecord::getScore,
                AnswerRecord::getDuration,
                AnswerRecord::getCreatedAt,
                AnswerRecord::getUpdatedAt,
                AnswerRecord::getAiAnalysis
        );

        // 执行分页查询并返回结果
        return this.page(page, wrapper);
    }

    @Override
    public Double getAverageTimeByGroupId(Integer questionGroupId) {
        // 1. 获取习题组下的所有问题ID
        List<Integer> questionIds = questionService.getQuestionIdsByGroupId(questionGroupId);

        if (questionIds == null || questionIds.isEmpty()) {
            // 如果习题组没有问题，返回0
            return 0.0;
        }

        // 2. 构建查询条件
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AnswerRecord::getQuestionId, questionIds)

                // 确保duration不为空
                .isNotNull(AnswerRecord::getDuration);

        // 3. 查询符合条件的所有答题记录
        List<AnswerRecord> records = this.list(wrapper);

        if (records == null || records.isEmpty()) {

            // 如果没有答题记录，返回0
            return 0.0;
        }

        // 4. 计算平均做题时间
        double totalDuration = 0;
        for (AnswerRecord record : records) {
            totalDuration += record.getDuration();
        }

        return totalDuration / records.size();
    }

//    @Override
//    public List<StudentAnswerResult> statisticsStudentAnswerAccuracy(Integer studentId, Integer questionGroupId) {
//        return this.baseMapper.statisticsStudentAnswerAccuracy(studentId, questionGroupId);
//    }
}