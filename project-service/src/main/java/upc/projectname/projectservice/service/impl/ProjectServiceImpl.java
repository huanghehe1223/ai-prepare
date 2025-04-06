package upc.projectname.projectservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.Project;
import upc.projectname.projectservice.mapper.ProjectMapper;
import upc.projectname.projectservice.service.ProjectService;

import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Override
    public Project getProjectById(Integer id) {
        return this.getById(id);
    }

    @Override
    public Project saveProject(Project project) {
        if (this.save(project)) {
            return project;
        }
        return null;
    }

    @Override
    public boolean updateProject(Project project) {
        return this.updateById(project);
    }

    @Override
    public boolean deleteProject(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<Project> getProjectByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<Project> getProjectsByClassId(Integer classId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getClassId, classId);
        return this.list(wrapper);
    }

    @Override
    public IPage<Project> getProjectsByClassIdAndPage(Integer classId, Page<Project> page, String name) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getClassId, classId);
        wrapper.like(name != null && !name.trim().isEmpty(), Project::getProjectName, name);
        return this.page(page, wrapper);
    }

    @Override
    public boolean changeProject(Integer projectId,
                                 Integer classId,
                                 String teachingAims,
                                 String studentAnalysis,
                                 String knowledgePoints,
                                 String teachingContent,
                                 Integer teachingDuration,
                                 String teachingTheme,
                                 String teachingObject,
                                 String extraReq,
                                 Integer currentStage,
                                 String textbookContent,
                                 String preexerceseResult,
                                 String teachingProcessOutline,
                                 String teachingProcess,
                                 String knowledgePointsTitle) {
        return this.baseMapper.updateProjectSelective(
                projectId, classId, teachingAims, studentAnalysis,
                knowledgePoints, teachingContent, teachingDuration,
                teachingTheme, teachingObject, extraReq,currentStage,textbookContent,preexerceseResult,teachingProcessOutline,teachingProcess,knowledgePointsTitle) > 0;
    }

    @Override
    public String exportMarkdown(Integer projectId) {
        Project project = this.getById(projectId);
        if (project == null) {
            return null;
        }
        //教学主题
        String teachingTheme = project.getTeachingTheme();
        //授课对象
        String teachingObject = project.getTeachingObject();
        //额外要求
        String extraReq = project.getExtraReq();
        //教学时长
        Integer teachingDuration = project.getTeachingDuration();
        //参考教科书
        String textbookName = project.getTextbookName();
        //学生预备知识掌握情况
        String studentAnalysis = project.getStudentAnalysis();
        //教学目标
        String teachingAims = project.getTeachingAims();
        //知识点总结
        String knowledgePointSummary = project.getKnowledgePoints();
        //教学过程大纲
        String teachingProcessOutline = project.getTeachingProcessOutline();
        //教学过程
        String teachingProcess = project.getTeachingProcess();
        //把上面的内容拼接成一个完整的字符串
        String markdown = """
                # 教学设计
                教学主题：%s，授课对象：%s，额外要求：%s，教学时长：%d分钟，参考教科书：%s
                学生预备知识掌握情况：
                %s
                教学目标：
                %s
                知识点总结：
                %s
                教学过程大纲：
                %s
                教学过程：
                %s
                """.formatted(teachingTheme, teachingObject, extraReq, teachingDuration, textbookName,studentAnalysis, teachingAims, knowledgePointSummary, teachingProcessOutline, teachingProcess);

        return markdown;
    }
}