package upc.projectname.upccommon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerResult {

    //以下字段来自question表
    //单选题、多选题、填空，简答
    private String questionType;

    private String questionText; //题目内容

    private String correctAnswer;

    private String explanation;  //感觉这个也没有内容

    private String difficulty;   //没有内容

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String knowledgePoint; //关联知识点




    //以下字段来自answer_record表
    private String answerResult;  //正确，错误

    private String studentAnswer; //学生的答案

    private LocalDateTime createdAt;  //学生做完这道题的时刻

    private String aiAnalysis;  //ai解析（感觉没有内容，主要针对简答题，依据标准答案来分析学生的答案）

    private Integer duration;  //单位：秒，学生做题时长

    private Integer score;  //得分，每道题都是10分

    private String resultType;    // 用于标识查询结果类型




}
