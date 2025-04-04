package upc.projectname.upccommon.domain.dto;

import lombok.Data;

@Data
public class AverageScoreDTO {
    private Double totalAvgScore;      // 整体平均分
    private Double singleAvgScore;     // 单选题平均分
    private Double multipleAvgScore;   // 多选题平均分
    private Double fillAvgScore;       // 填空题平均分
    private Double shortAvgScore;      // 简答题平均分
} 