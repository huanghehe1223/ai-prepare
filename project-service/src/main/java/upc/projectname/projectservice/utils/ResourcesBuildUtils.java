package upc.projectname.projectservice.utils;


import org.springframework.stereotype.Component;
import upc.projectname.projectservice.entity.ResourceDTO;

import java.util.List;

@Component
public class ResourcesBuildUtils {

    /**
     * 根据ResourceDTO生成HTML图片标签
     * @param resourceDTO 资源数据传输对象
     * @return 格式化的HTML图片标签字符串
     */
    public String generateHtmlImgTag(ResourceDTO resourceDTO) {
        if (resourceDTO == null || resourceDTO.url == null || !resourceDTO.type.equalsIgnoreCase("image")) {
            return "";
        }

        return String.format("<img src=\"%s\" width=\"500px\" />", resourceDTO.url);

    }


    /**
     * 根据ResourceDTO生成B站视频嵌入的iframe HTML标签
     * @param resourceDTO 资源数据传输对象
     * @return 格式化的B站视频iframe HTML标签字符串
     */
    public String generateBilibiliVideoTag(ResourceDTO resourceDTO) {
        if (resourceDTO == null || resourceDTO.url == null || !resourceDTO.type.equalsIgnoreCase("video")) {
            return "";
        }

        // 假设resourceDTO.url包含BV号
        String bvid = resourceDTO.url;

        return "<iframe \n" +
                "  src=\"//player.bilibili.com/player.html?isOutside=true&bvid=" + bvid + "\" \n" +
                "  width=\"800\" \n" +
                "  height=\"450\" \n" +
                "  scrolling=\"no\" \n" +
                "  border=\"0\" \n" +
                "  frameborder=\"no\" \n" +
                "  framespacing=\"0\" \n" +
                "  allowfullscreen=\"true\">\n" +
                "</iframe>";
    }


    /**
     * 根据ResourceDTO生成Markdown格式的网页链接
     * @param resourceDTO 资源数据传输对象
     * @return 格式化的Markdown链接字符串
     */
    public String generateWebLinkMarkdown(ResourceDTO resourceDTO) {
        if (resourceDTO == null || resourceDTO.url == null || !resourceDTO.type.equalsIgnoreCase("web")) {
            return "";
        }

        // 使用introduction作为链接文本，url作为链接地址
        String linkText = resourceDTO.introduction;
        String linkUrl = resourceDTO.url;

        // 如果introduction为空，提供一个默认值
        if (linkText == null || linkText.trim().isEmpty()) {
            linkText = "网页教学资源";
        }

        // 返回Markdown格式的链接
        return String.format("[%s](%s)", linkText, linkUrl);
    }


    /**
     * 根据ResourceDTO列表生成所有图片的HTML标签
     * @param resourceList 资源数据传输对象列表
     * @return 包含所有图片HTML标签的字符串，以换行符分隔
     */
    public String generateAllImagesHtml(List<ResourceDTO> resourceList) {
        if (resourceList == null || resourceList.isEmpty()) {
            return "";
        }

        StringBuilder imageTagsBuilder = new StringBuilder();
        boolean isFirst = true;

        for (ResourceDTO resource : resourceList) {
            if (resource != null && "image".equalsIgnoreCase(resource.getType())) {
                if (!isFirst) {
                    imageTagsBuilder.append("\n\n");
                } else {
                    isFirst = false;
                }
                imageTagsBuilder.append(generateHtmlImgTag(resource));
            }
        }

        return imageTagsBuilder.toString();
    }


    /**
     * 根据ResourceDTO列表生成所有网页链接的Markdown格式
     * @param resourceList 资源数据传输对象列表
     * @return 包含所有网页链接的Markdown格式字符串，以换行符分隔
     */
    public String generateAllWebLinksMarkdown(List<ResourceDTO> resourceList) {
        if (resourceList == null || resourceList.isEmpty()) {
            return "";
        }

        StringBuilder linkMarkdownBuilder = new StringBuilder();
        boolean isFirst = true;

        for (ResourceDTO resource : resourceList) {
            if (resource != null && "web".equalsIgnoreCase(resource.getType())) {
                if (!isFirst) {
                    linkMarkdownBuilder.append("\n\n");
                } else {
                    isFirst = false;
                }
                linkMarkdownBuilder.append(generateWebLinkMarkdown(resource));
            }
        }

        return linkMarkdownBuilder.toString();
    }

    /**
     * 根据ResourceDTO列表生成所有B站视频的iframe HTML标签
     * @param resourceList 资源数据传输对象列表
     * @return 包含所有B站视频iframe HTML标签的字符串，以换行符分隔
     */
    public String generateAllBilibiliVideosHtml(List<ResourceDTO> resourceList) {
        if (resourceList == null || resourceList.isEmpty()) {
            return "";
        }

        StringBuilder videoTagsBuilder = new StringBuilder();
        boolean isFirst = true;

        for (ResourceDTO resource : resourceList) {
            if (resource != null && "video".equalsIgnoreCase(resource.getType())) {
                if (!isFirst) {
                    videoTagsBuilder.append("\n\n");
                } else {
                    isFirst = false;
                }
                videoTagsBuilder.append(generateBilibiliVideoTag(resource));
            }
        }

        return videoTagsBuilder.toString();
    }


    /**
     * 根据ResourceDTO列表生成按类型组织的完整资源列表
     * 按照网页、图片、视频的顺序组织，每种类型前添加一级标题
     * @param resourceList 资源数据传输对象列表
     * @return 组织好的包含所有资源的带标题字符串
     */
    public String generateFormattedResourcesDocument(List<ResourceDTO> resourceList) {
        if (resourceList == null || resourceList.isEmpty()) {
            return "";
        }

        StringBuilder documentBuilder = new StringBuilder();
        documentBuilder.append("# 教学资源列表\n\n");

        // 添加网页资源部分
        String webLinks = generateAllWebLinksMarkdown(resourceList);
        if (!webLinks.isEmpty()) {
            documentBuilder.append("## 网页资源\n");
            documentBuilder.append(webLinks);
            documentBuilder.append("\n\n");
        }

        // 添加图片资源部分
        String imageHtml = generateAllImagesHtml(resourceList);
        if (!imageHtml.isEmpty()) {
            documentBuilder.append("## 图片资源\n");
            documentBuilder.append(imageHtml);
            documentBuilder.append("\n\n");
        }

        // 添加视频资源部分
        String videoHtml = generateAllBilibiliVideosHtml(resourceList);
        if (!videoHtml.isEmpty()) {
            documentBuilder.append("## 视频资源\n");
            documentBuilder.append(videoHtml);
        }

        return documentBuilder.toString().trim();
    }

    








}
