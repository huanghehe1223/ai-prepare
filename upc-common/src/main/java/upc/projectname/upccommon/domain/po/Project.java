package upc.projectname.upccommon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("project")
public class Project implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @TableId(value = "project_id", type = IdType.AUTO)
    private Integer projectId;
    
    private Integer classId;
    
    private String projectName;
    
    private String teachingAims;
    
    private String studentAnalysis;
    
    private String knowledgePoints;
    
    private String teachingContent;
    
    private Integer teachingDuration;

    private String  teachingTheme; //教学主题

    private String  teachingObject; //教学对象

    private String  extraReq; //额外要求

    private Integer currentStage; //当前阶段

    private String textbookContent; //相关教材内容

    private String preexerceseResult; //预备知识检测结果

    private String teachingProcessOutline; //教学过程大纲

    private String teachingProcess; //教学过程

    private String knowledgePointsTitle; //知识点标题


}