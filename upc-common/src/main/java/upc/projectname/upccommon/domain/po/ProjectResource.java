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
@TableName("project_resource")
public class ProjectResource implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @TableId(value = "resource_id", type = IdType.AUTO)
    private Integer resourceId;
    
    private String content;
    
    private Integer projectId;

    private String type;
} 