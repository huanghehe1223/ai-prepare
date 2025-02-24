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
@TableName("teacher")
public class Teacher implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @TableId(value = "teacher_id", type = IdType.AUTO)
    private Integer teacherId;
    
    private String teacherName;
    
    private String userName;
    
    private String password;
    
    private String imageUrl;
} 