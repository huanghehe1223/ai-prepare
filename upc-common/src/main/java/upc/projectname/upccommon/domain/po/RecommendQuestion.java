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
@TableName("recommend_question")
public class RecommendQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "question_id", type = IdType.AUTO)
    private Integer questionId;

    private Integer groupId;

    private String questionType;

    private String questionText;

    private String correctAnswer;

    private String explanation;

    private String difficulty;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;
}