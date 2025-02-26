package upc.projectname.userclassservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.Class;
import upc.projectname.userclassservice.mapper.ClassMapper;
import upc.projectname.userclassservice.service.ClassService;
import upc.projectname.userclassservice.utils.ConvertIdToStringUtils;

import java.util.List;

@Slf4j
@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class> implements ClassService {

    @Override
    public IPage<Class> getClassesByStudentId(Page<Class> page, Integer studentId, String className, String courseName) {
        return this.baseMapper.getClassesByStudentId(page, studentId, className, courseName);
    }

    @Override
    public Class getClassById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveClass(Class clazz) {
        if(this.save(clazz)){
            Integer classId = clazz.getClassId();
            String classCode = ConvertIdToStringUtils.convertIdToStringUtils(classId);
            clazz.setClassCode(classCode);
            return this.updateById(clazz);
        }
        return false;
    }

    @Override
    public boolean updateClass(Class clazz) {
        return this.updateById(clazz);
    }

    @Override
    public boolean deleteClass(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<Class> getClassByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }

    @Override
    public Class getClassByCode(String classCode) {
        LambdaQueryWrapper<Class> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Class::getClassCode, classCode);
        return this.getOne(queryWrapper);
    }
}