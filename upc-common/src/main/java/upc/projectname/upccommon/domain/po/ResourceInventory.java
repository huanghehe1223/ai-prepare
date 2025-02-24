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
@TableName("resource_inventory")
public class ResourceInventory implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @TableId(value = "inventory_id", type = IdType.AUTO)
    private Integer inventoryId;
    
    private Integer projectId;
    
    private Integer userId;
    
    private Integer resourceId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 