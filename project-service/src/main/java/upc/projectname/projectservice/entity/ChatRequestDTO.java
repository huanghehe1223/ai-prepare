package upc.projectname.projectservice.entity;


import com.openai.models.ChatCompletionMessageParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    List<ChatCompletionMessageParam> messages;
    String model;
    String testContent;

}
