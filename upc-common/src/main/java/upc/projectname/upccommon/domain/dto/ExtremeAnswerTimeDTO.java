package upc.projectname.upccommon.domain.dto;

import lombok.Data;

@Data
public class ExtremeAnswerTimeDTO {
    private StudentAnswerResult rightMaxTime;    // 正确题目中耗时最长的
    private StudentAnswerResult rightMinTime;    // 正确题目中耗时最短的
    private StudentAnswerResult wrongMaxTime;    // 错误题目中耗时最长的
    private StudentAnswerResult wrongMinTime;    // 错误题目中耗时最短的
} 