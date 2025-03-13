package upc.projectname.userclassservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.ClassTeacher;
import upc.projectname.userclassservice.mapper.ClassTeacherMapper;
import upc.projectname.userclassservice.service.ClassService;
import upc.projectname.userclassservice.service.ClassTeacherService;

import java.util.List;

//@RequiredArgsConstructor
@Service
public class ClassTeacherServiceImpl extends ServiceImpl<ClassTeacherMapper, ClassTeacher> implements ClassTeacherService {

    @Autowired
    private ClassService classService;

    @Override
    public ClassTeacher getClassTeacherById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveClassTeacher(ClassTeacher classTeacher) {
        return this.save(classTeacher);
    }

    @Override
    public boolean updateClassTeacher(ClassTeacher classTeacher) {
        return this.updateById(classTeacher);
    }

    @Override
    public boolean deleteClassTeacher(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<ClassTeacher> getClassTeacherByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<ClassTeacher> getClassTeachersByClassId(Integer classId) {
        LambdaQueryWrapper<ClassTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassTeacher::getClassId, classId);
        return this.list(wrapper);
    }
    
    @Override
    public List<ClassTeacher> getClassTeachersByTeacherId(Integer teacherId) {
        LambdaQueryWrapper<ClassTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassTeacher::getTeacherId, teacherId);
        return this.list(wrapper);
    }
    
    @Override
    public boolean updateTeacherStatus(Integer teacherId, Integer classId, String status) {
        LambdaUpdateWrapper<ClassTeacher> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ClassTeacher::getTeacherId, teacherId)
               .eq(ClassTeacher::getClassId, classId)
               .set(ClassTeacher::getStatus, status);
        return this.update(wrapper);
    }

    @Override
    public boolean applyClassTeacher(Integer teacherId, String classCode) {
        Integer classId = classService.getClassByCode(classCode).getClassId();
        ClassTeacher classTeacher = new ClassTeacher();
        classTeacher.setTeacherId(teacherId);
        classTeacher.setClassId(classId);
        classTeacher.setStatus("Apply");
        return this.save(classTeacher);
    }
} 