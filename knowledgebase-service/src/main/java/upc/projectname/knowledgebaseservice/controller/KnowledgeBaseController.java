package upc.projectname.knowledgebaseservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.KnowledgeBase;
import upc.projectname.knowledgebaseservice.service.KnowledgeBaseService;

import java.util.List;

@Tag(name = "知识库管理接口")
@RestController
@RequestMapping("/knowledgebase")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @Operation(summary = "根据ID查询知识库")
    @GetMapping("/{id}")
    public Result<KnowledgeBase> getKnowledgeBase(@PathVariable Integer id) {
        KnowledgeBase knowledgeBase = knowledgeBaseService.getKnowledgeBaseById(id);
        return knowledgeBase != null ? Result.success(knowledgeBase) : Result.error("知识库不存在");
    }

    @Operation(summary = "根据ID批量查询知识库")
    @PostMapping("/batch")
    public Result<List<KnowledgeBase>> getKnowledgeBaseByIds(@RequestBody List<Integer> ids) {
        List<KnowledgeBase> knowledgeBases = knowledgeBaseService.getKnowledgeBaseByIds(ids);
        return knowledgeBases != null && !knowledgeBases.isEmpty() ? 
                Result.success(knowledgeBases) : 
                Result.error("未找到知识库信息");
    }

    @Operation(summary = "新增知识库")
    @PostMapping
    public Result<Boolean> saveKnowledgeBase(@RequestBody KnowledgeBase knowledgeBase) {
        return knowledgeBaseService.saveKnowledgeBase(knowledgeBase) ?
                Result.success(true, "添加成功") :
                Result.error("添加失败");
    }

    @Operation(summary = "更新知识库")
    @PutMapping
    public Result<Boolean> updateKnowledgeBase(@RequestBody KnowledgeBase knowledgeBase) {
        return knowledgeBaseService.updateKnowledgeBase(knowledgeBase) ?
                Result.success(true, "更新成功") :
                Result.error("更新失败");
    }

    @Operation(summary = "删除知识库")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteKnowledgeBase(@PathVariable Integer id) {
        return knowledgeBaseService.deleteKnowledgeBase(id) ?
                Result.success(true, "删除成功") :
                Result.error("删除失败");
    }

    @Operation(summary = "分页条件查询知识库（业务）")
    @PostMapping("/page")
    public Result<IPage<KnowledgeBase>> getKnowledgeBasePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String phase,
            @RequestParam(required = false) String edition) {
        Page<KnowledgeBase> page = new Page<>(current, size);
        IPage<KnowledgeBase> knowledgeBasePage = knowledgeBaseService.getKnowledgeBasePage(page, subject, phase,edition);
        return Result.success(knowledgeBasePage);
    }

    @Operation(summary = "根据阶段查询学科(业务)")
    @PostMapping("/phase")
    public Result<List<String>> getSubjectByPhase(@RequestParam String phase) {
        List<String> subjects = knowledgeBaseService.getSubjectByPhase(phase);
        return subjects != null && !subjects.isEmpty() ?
                Result.success(subjects) :
                Result.error("未找到学科信息");
    }

    @Operation(summary = "根据阶段,学科查询版本(业务)")
    @PostMapping("/phase/subject")
    public Result<List<String>> getEditionByPhaseAndSubject(@RequestParam String phase, @RequestParam String subject) {
        List<String> editions = knowledgeBaseService.getEditionByPhaseAndSubject(phase, subject);
        return editions != null && !editions.isEmpty() ?
                Result.success(editions) :
                Result.error("未找到版本信息");
    }




} 