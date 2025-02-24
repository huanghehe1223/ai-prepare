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
} 