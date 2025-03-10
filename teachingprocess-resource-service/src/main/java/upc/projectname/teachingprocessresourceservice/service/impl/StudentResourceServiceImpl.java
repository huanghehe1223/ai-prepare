package upc.projectname.teachingprocessresourceservice.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.QuestionGroup;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.StudentResource;
import upc.projectname.upccommon.api.client.QuestionGroupClient;
import upc.projectname.teachingprocessresourceservice.mapper.StudentResourceMapper;
import upc.projectname.teachingprocessresourceservice.service.StudentResourceService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StudentResourceServiceImpl extends ServiceImpl<StudentResourceMapper, StudentResource> implements StudentResourceService {

    private final QuestionGroupClient questionGroupClient;

    @Override
    public StudentResource getStudentResourceById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveStudentResource(StudentResource studentResource) {
        return this.save(studentResource);
    }

    @Override
    public boolean updateStudentResource(StudentResource studentResource) {
        return this.updateById(studentResource);
    }

    @Override
    public boolean deleteStudentResource(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<StudentResource> getStudentResourceByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<StudentResource> getStudentResourcesByStudentId(Integer studentId) {
        LambdaQueryWrapper<StudentResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentResource::getStudentId, studentId);
        return this.list(wrapper);
    }
    
    @Override
    public List<StudentResource> getStudentResourcesByProjectId(Integer projectId) {
        LambdaQueryWrapper<StudentResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentResource::getProjectId, projectId);
        return this.list(wrapper);
    }
    
    @Override
    public List<StudentResource> getStudentResourcesByStudentIdAndProjectId(Integer studentId, Integer projectId) {
        LambdaQueryWrapper<StudentResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentResource::getStudentId, studentId)
              .eq(StudentResource::getProjectId, projectId);
        return this.list(wrapper);
    }

    @Override
    public List<Integer> getResourceIdsByStudentIdAndGroupId(Integer studentId, Integer groupId) {
        LambdaQueryWrapper<StudentResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentResource::getStudentId, studentId)
                .eq(StudentResource::getGroupId, groupId)
                .select(StudentResource::getResourceId);

        return this.baseMapper.selectList(queryWrapper)
                .stream()
                .map(StudentResource::getResourceId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getResourceIdsByStudentIdAndProjectId(Integer studentId, Integer projectId) {
        LambdaQueryWrapper<StudentResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentResource::getStudentId, studentId)
                .eq(StudentResource::getProjectId, projectId)
                .select(StudentResource::getResourceId);

        return this.baseMapper.selectList(queryWrapper)
                .stream()
                .map(StudentResource::getResourceId)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<StudentResource> getStudentResourcesByPage(Page<StudentResource> page,
                                                            Integer studentId,
                                                            Integer projectId,
                                                            Integer groupId,
                                                            String groupType) {
        // 构建基础查询条件
        LambdaQueryWrapper<StudentResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentResource::getStudentId, studentId);

        // 添加项目ID条件（如果有）
        if (projectId != null) {
            queryWrapper.eq(StudentResource::getProjectId, projectId);
        }

        // 处理groupId为空但有groupType的情况
        if (groupId == null && StringUtils.hasText(groupType) && projectId != null) {
            // 通过QuestionGroupClient查询符合条件的习题组
            Result<List<QuestionGroup>> groupsResult = questionGroupClient.getQuestionGroupsByProjectId(projectId);

            if (groupsResult.getCode()==1 && groupsResult.getData() != null && !groupsResult.getData().isEmpty()) {
                // 筛选出符合groupType的习题组
                List<QuestionGroup> filteredGroups = groupsResult.getData().stream()
                        .filter(group -> groupType.equals(group.getGroupType()))
                        .collect(Collectors.toList());

                if (!filteredGroups.isEmpty()) {
                    // 提取groupId列表
                    List<Integer> groupIds = filteredGroups.stream()
                            .map(QuestionGroup::getGroupId)
                            .collect(Collectors.toList());

                    // 添加IN条件：groupId在查询到的习题组ID列表中
                    queryWrapper.in(StudentResource::getGroupId, groupIds);
                } else {
                    // 如果没找到符合条件的习题组，返回空结果
                    return page.setRecords(Collections.emptyList());
                }
            } else {
                // 如果远程调用失败或未找到习题组，返回空结果
                return page.setRecords(Collections.emptyList());
            }
        } else if (groupId != null) {
            // 如果直接提供了groupId，就使用它作为条件
            queryWrapper.eq(StudentResource::getGroupId, groupId);
        }

        // 执行分页查询并返回结果
        return this.baseMapper.selectPage(page, queryWrapper);
    }
} 