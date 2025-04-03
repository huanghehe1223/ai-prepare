package upc.projectname.upccommon.domain.dto;

import lombok.Data;

@Data
public class AccuracyRateDTO {
    private Double totalRate;      // 整体正确率
    private Double singleRate;     // 单选题正确率
    private Double multipleRate;   // 多选题正确率
    private Double fillRate;       // 填空题正确率
} 