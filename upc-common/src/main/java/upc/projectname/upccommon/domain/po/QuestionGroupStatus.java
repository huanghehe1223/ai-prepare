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
@TableName("questiongroup_status")
public class QuestionGroupStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "status_id", type = IdType.AUTO)
    private Integer statusId;

    private Integer studentId;

    private Integer groupId;

    private String status;

    private Integer duration;
}