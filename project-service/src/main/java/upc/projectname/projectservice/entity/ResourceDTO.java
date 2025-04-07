package upc.projectname.projectservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDTO {
    public String type;  //web,image,video
    public String url;   //如果是web，就是网页url，如果是image，就是图片url，如果是video，就是b站bv号
    public String introduction; //web:网页简介，image,video:无所谓
}
