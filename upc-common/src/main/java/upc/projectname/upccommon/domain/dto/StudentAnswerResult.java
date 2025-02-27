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
    //单选题、多选题、简答，填空
    private String questionType;

    private String questionText; //题目内容

    private String correctAnswer;

    private String explanation;

    private String difficulty;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;


    //以下字段来自answer_record表
    private String answerResult;  //正确，错误

    private String studentAnswer; //学生的答案

    private LocalDateTime createdAt;  //学生做完这道题的时刻

    private String aiAnalysis;  //ai解析（感觉用不到）

    private String knowledgePoint; //关联知识点


}
