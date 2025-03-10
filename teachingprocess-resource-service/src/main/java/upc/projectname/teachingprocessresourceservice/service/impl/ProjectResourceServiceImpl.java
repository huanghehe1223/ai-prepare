package upc.projectname.teachingprocessresourceservice.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upc.projectname.teachingprocessresourceservice.service.StudentResourceService;
import upc.projectname.upccommon.api.client.QuestionGroupClient;
import upc.projectname.upccommon.domain.po.ProjectResource;
import upc.projectname.teachingprocessresourceservice.mapper.ProjectResourceMapper;
import upc.projectname.teachingprocessresourceservice.service.ProjectResourceService;
import upc.projectname.upccommon.domain.po.QuestionGroup;
import upc.projectname.upccommon.domain.po.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectResourceServiceImpl extends ServiceImpl<ProjectResourceMapper, ProjectResource> implements ProjectResourceService {

    private final StudentResourceService studentResourceService;
    private final QuestionGroupClient questionGroupClient;

    @Override
    public ProjectResource getProjectResourceById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveProjectResource(ProjectResource projectResource) {
        return this.save(projectResource);
    }

    @Override
    public boolean updateProjectResource(ProjectResource projectResource) {
        return this.updateById(projectResource);
    }

    @Override
    public boolean deleteProjectResource(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<ProjectResource> getProjectResourceByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<ProjectResource> getProjectResourcesByProjectId(Integer projectId) {
        LambdaQueryWrapper<ProjectResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectResource::getProjectId, projectId);
        return this.list(wrapper);
    }

    @Override
    public IPage<ProjectResource> getProjectResourcesByPage(Page<ProjectResource> page,
                                                            Integer studentId,
                                                            Integer projectId,
                                                            String type,
                                                            Integer groupId,
                                                            String groupType) {
        // 存储要查询的资源ID列表
        List<Integer> resourceIds = new ArrayList<>();

        // 处理groupId为空的情况
        if (groupId == null && studentId != null && projectId != null && groupType != null) {
            // 通过QuestionGroupClient查询符合条件的习题组
            Result<List<QuestionGroup>> groupsResult = questionGroupClient.getQuestionGroupsByProjectId(projectId);

            if (groupsResult.getCode()==1 && groupsResult.getData() != null) {
                List<QuestionGroup> groups = groupsResult.getData().stream()
                        .filter(group -> groupType.equals(group.getGroupType()))
                        .collect(Collectors.toList());

                if (!groups.isEmpty()) {
                    // 获取到符合条件的groupId列表
                    List<Integer> groupIds = groups.stream()
                            .map(QuestionGroup::getGroupId)
                            .collect(Collectors.toList());

                    // 遍历每个groupId查询studentResource
                    for (Integer gId : groupIds) {
                        List<Integer> ids = studentResourceService.getResourceIdsByStudentIdAndGroupId(studentId, gId);
                        if (ids != null && !ids.isEmpty()) {
                            resourceIds.addAll(ids);
                        }
                    }
                }
            }
        } else if (groupId != null) {
            // 如果groupId不为空，直接查询
            List<Integer> ids = studentResourceService.getResourceIdsByStudentIdAndGroupId(studentId, groupId);
            if (ids != null && !ids.isEmpty()) {
                resourceIds.addAll(ids);
            }
        } else {
            // 如果groupId为空且不需要特殊处理，则根据studentId和projectId查询
            List<Integer> ids = studentResourceService.getResourceIdsByStudentIdAndProjectId(studentId, projectId);
            if (ids != null && !ids.isEmpty()) {
                resourceIds.addAll(ids);
            }
        }

        // 如果没有找到资源ID，返回空页
        if (resourceIds.isEmpty()) {
            return page.setRecords(Collections.emptyList());
        }

        // 构建查询条件
        LambdaQueryWrapper<ProjectResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ProjectResource::getResourceId, resourceIds);

        // 添加项目ID条件
        queryWrapper.eq(ProjectResource::getProjectId, projectId);

        // 添加类型条件（如果有）
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(ProjectResource::getType, type);
        }

        // 执行分页查询并返回结果
        return this.baseMapper.selectPage(page, queryWrapper);
    }
} 