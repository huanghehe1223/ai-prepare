package upc.projectname.projectservice.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upc.projectname.projectservice.entity.ResourceDTO;
import upc.projectname.projectservice.utils.PromptUtils;
import upc.projectname.projectservice.utils.ResourcesBuildUtils;
import upc.projectname.projectservice.utils.TextBookUtils;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.projectservice.service.ProjectService;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "项目管理接口")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final PromptUtils promptUtils;
    private final ResourcesBuildUtils resourcesBuildUtils;



    @Operation(summary = "测试docker-compose")
    @GetMapping("/testdocker")
    public Result<String> testDocker() {
        return Result.success("docker-compose测试成功,version2");
    }



    @Operation(summary = "根据classId分页模糊查询项目（id、名称）（业务）")
    @GetMapping("/class/page")
    public Result<IPage<Project>> getProjectsByClassIdAndPage(@RequestParam Integer classId,
                                                             @RequestParam(defaultValue = "1") Integer current,
                                                             @RequestParam(defaultValue = "10") Integer size,
                                                             @RequestParam(required = false) String name) {
        Page<Project> page = new Page<>(current, size);
        IPage<Project> projects = projectService.getProjectsByClassIdAndPage(classId, page, name);
        return Result.success(projects);
    }


    @Operation(summary = "根据ID查询项目")
    @GetMapping("/{id}")
    public Result<Project> getProject(@PathVariable Integer id) {
        Project project = projectService.getProjectById(id);
        return project != null ? Result.success(project) : Result.error("项目不存在");
    }

    @Operation(summary = "根据ID批量查询项目")
    @PostMapping("/batch")
    public Result<List<Project>> getProjectByIds(@RequestBody List<Integer> ids) {
        List<Project> projects = projectService.getProjectByIds(ids);
        return projects != null && !projects.isEmpty() ? 
                Result.success(projects) : 
                Result.error("未找到项目信息");
    }
    
    @Operation(summary = "根据班级ID查询项目")
    @GetMapping("/class/{classId}")
    public Result<List<Project>> getProjectsByClassId(@PathVariable Integer classId) {
        List<Project> projects = projectService.getProjectsByClassId(classId);
        return projects != null && !projects.isEmpty() ? 
                Result.success(projects) : 
                Result.error("未找到该班级的项目信息");
    }

    @Operation(summary = "新增项目")
    @PostMapping
    public Result<Project> saveProject(@RequestBody Project project) {
        return projectService.saveProject(project) != null ?
                Result.success(project, "新增成功") :
                Result.error("新增失败");
    }

    @Operation(summary = "更新项目信息")
    @PutMapping
    public Result<Boolean> updateProject(@RequestBody Project project) {
        return projectService.updateProject(project) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProject(@PathVariable Integer id) {
        return projectService.deleteProject(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }

    @Operation(summary = "条件修改项目(业务,token)")
    @PostMapping("/changeproject")
    public Result<Boolean> changeProject(@RequestParam("projectId") Integer projectId,
                                         @RequestParam(value = "classId",required = false) Integer classId,
                                         @RequestParam(value = "teachingAims",required = false) String teachingAims,
                                         @RequestParam(value = "studentAnalysis",required = false) String studentAnalysis,
                                         @RequestParam(value = "knowledgePoints",required = false) String knowledgePoints,
                                         @RequestParam(value = "teachingContent",required = false) String teachingContent,
                                         @RequestParam(value = "teachingDuration",required = false) Integer teachingDuration,
                                         @RequestParam(value = "teachingTheme",required = false) String teachingTheme,
                                         @RequestParam(value = "teachingObject",required = false) String teachingObject,
                                         @RequestParam(value = "extraReq",required = false) String extraReq,
                                         @RequestParam(value = "currentStage",required = false) Integer currentStage,
                                         @RequestParam(value = "textbookContent",required = false) String textbookContent,
                                         @RequestParam(value = "preexerceseResult",required = false) String preexerceseResult,
                                         @RequestParam(value = "teachingProcessOutline",required = false) String teachingProcessOutline,
                                         @RequestParam(value = "teachingProcess",required = false) String teachingProcess,
                                         @RequestParam(value = "knowledgePointsTitle",required = false) String knowledgePointsTitle) {
        // 判断除了projectId外的所有参数是否都为空
        if (classId == null
                && teachingAims == null
                && studentAnalysis == null
                && knowledgePoints == null
                && teachingContent == null
                && teachingDuration == null
                && teachingTheme == null
                && teachingObject == null
                && extraReq == null
                && currentStage == null
                && textbookContent == null
                && preexerceseResult == null
                && teachingProcessOutline == null
                && teachingProcess == null
                && knowledgePointsTitle == null) {
            return Result.error("更新失败：至少需要一个更新参数");
        }

        return projectService.changeProject(projectId, classId, teachingAims, studentAnalysis,
                knowledgePoints, teachingContent, teachingDuration, teachingTheme,
                teachingObject, extraReq,currentStage,textbookContent,preexerceseResult,teachingProcessOutline,teachingProcess,knowledgePointsTitle) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }
    @Operation(summary = "从教材图数据库中检索节点与关系(业务)")
    @PostMapping("/search")
    public Result<String> searchFromTextBook(@RequestParam String query,@RequestParam String databaseName,@RequestParam Integer projectId,@RequestParam String textbookName) {
        String results = TextBookUtils.getVectorResults(query,databaseName);
        Project project = new Project();
        project.setProjectId(projectId);
        project.setTextbookContent(results);
        project.setTextbookName(textbookName);
        projectService.updateProject(project);
        JSONArray jsonArray = JSON.parseArray(results);
        String content1 = jsonArray.stream().map(item -> ((JSONObject) item).getString("index")+"\n"+((JSONObject) item).getString("content")).collect(Collectors.joining("\n"));
        return Result.success(content1);
    }






    @Operation(summary = "导出markdown教学设计文件")
    @GetMapping("/export/{projectId}")
    public ResponseEntity<Object> exportMarkdown(@PathVariable Integer projectId) {
        String rawMarkdown = projectService.exportMarkdown(projectId);
        String markdown = promptUtils.formatTeachingDesign(rawMarkdown);
//        String markdown = projectService.exportMarkdown(projectId);

        // 获取项目信息用于文件命名
        Project project = projectService.getProjectById(projectId);
        String filename = (project != null && project.getTeachingTheme() != null)
                ? project.getTeachingTheme()
                : "教学设计_" + projectId;

        // 确保文件名有效并添加后缀
        filename = filename.replaceAll("[\\\\/:*?\"<>|]", "_") + ".md";

        // 设置HTTP头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/markdown"));

        try {
            // 使用 URLEncoder 对文件名进行编码，并指定编码方式为 UTF-8
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
            // 替换空格编码，使其更友好
            encodedFilename = encodedFilename.replace("+", "%20");

            // 同时提供编码版本和原始版本（用引号括起来）
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + encodedFilename + "\"; " +
                            "filename*=UTF-8''" + encodedFilename);
        } catch (UnsupportedEncodingException e) {
            // 如果编码失败，使用默认ASCII安全的文件名
            headers.setContentDispositionFormData("attachment", "teaching_design.md");
        }

        // 返回带有Markdown内容的响应实体
        return new ResponseEntity<>(markdown, headers, HttpStatus.OK);
    }

    @Operation(summary = "增加教学资源列表")
    @PostMapping("/addResourceList")
    public Result<Boolean> addResourceList(@RequestParam Integer projectId,@RequestBody List<ResourceDTO> resourceDTOList) {
        Project projectById = projectService.getProjectById(projectId);
        if (projectById == null) {
            return Result.error("项目不存在");
        }
        String resourceString = resourcesBuildUtils.generateFormattedResourcesDocument(resourceDTOList);
        StringBuilder markdown = new StringBuilder();
        markdown.append(projectById.getFinalTeachingDesign());
        markdown.append("\n\n");
        markdown.append(resourceString);
        Project project =new Project();
        project.setProjectId(projectId);
        project.setFinalTeachingDesign(markdown.toString());
        projectService.updateProject(project);
        return Result.success(true, "修改成功");
    }








} 