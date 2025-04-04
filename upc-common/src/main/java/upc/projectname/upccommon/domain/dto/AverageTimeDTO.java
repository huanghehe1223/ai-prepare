package upc.projectname.upccommon.domain.dto;

import lombok.Data;

@Data
public class AverageTimeDTO {
    private Double totalAvgTime;      // 整体平均时间
    private Double singleAvgTime;     // 单选题平均时间
    private Double multipleAvgTime;   // 多选题平均时间
    private Double fillAvgTime;       // 填空题平均时间
    private Double shortAvgTime;      // 简答题平均时间
} 