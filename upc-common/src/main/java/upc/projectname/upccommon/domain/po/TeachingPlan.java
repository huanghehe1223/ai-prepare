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
@TableName("teaching_plan")
public class TeachingPlan implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @TableId(value = "plan_id", type = IdType.AUTO)
    private Integer planId;
    
    private String content;
} 