package upc.projectname.projectservice.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.projectservice.utils.TextBookUtils;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.projectservice.service.ProjectService;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "项目管理接口")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;



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
                                         @RequestParam(value = "preexerceseResult",required = false) String preexerceseResult) {
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
                && preexerceseResult == null) {
            return Result.error("更新失败：至少需要一个更新参数");
        }

        return projectService.changeProject(projectId, classId, teachingAims, studentAnalysis,
                knowledgePoints, teachingContent, teachingDuration, teachingTheme,
                teachingObject, extraReq,currentStage,textbookContent,preexerceseResult) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }
    @Operation(summary = "从教材图数据库中检索节点与关系(业务)")
    @PostMapping("/search")
    public Result<String> searchFromTextBook(@RequestParam String query,@RequestParam String databaseName,@RequestParam Integer projectId) {
        String results = TextBookUtils.getVectorResults(query,databaseName);
        projectService.changeProject(projectId,null,null,null,null,null,null,null,null,null,null,results,null);
        JSONArray jsonArray = JSON.parseArray(results);
        String content1 = jsonArray.stream().map(item -> ((JSONObject) item).getString("index")+"\n"+((JSONObject) item).getString("content")).collect(Collectors.joining("\n"));
        return Result.success(content1);
    }




} 