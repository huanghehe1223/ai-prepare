package upc.projectname.upccommon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("resources")
public class Resources implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @TableId(value = "resource_id", type = IdType.AUTO)
    private Integer resourceId;
    
    private String title;
    
    private String description;
    
    private String category;
    
    private String url;
    
    private String tags;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 