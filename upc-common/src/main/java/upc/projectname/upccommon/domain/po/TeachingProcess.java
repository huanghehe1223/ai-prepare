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
@TableName("teaching_process")
public class TeachingProcess implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @TableId(value = "step_id", type = IdType.AUTO)
    private Integer stepId;
    
    private Integer projectId;
    
    private Integer stepNumber;
    
    private String briefContent;
    
    private String detailedContent;
} 