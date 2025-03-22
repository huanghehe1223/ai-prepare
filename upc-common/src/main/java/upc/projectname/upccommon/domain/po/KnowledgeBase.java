package upc.projectname.upccommon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("knowledge_base")
public class KnowledgeBase {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String originalName; //原始名称

    private String databaseName; //对应的图数据库名称

    private String url; //教材url

    private String phase; //阶段(初中，高中)

    private String subject; //学科

    private String edition; //教材版本

    private String imageUrl; //教材封面图片url

    private String mdUrl; //教材md文件url



}
