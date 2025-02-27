package upc.projectname.userclassservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.ClassStudent;
import upc.projectname.userclassservice.mapper.ClassStudentMapper;
import upc.projectname.userclassservice.service.ClassStudentService;

import java.util.List;

@Service
public class ClassStudentServiceImpl extends ServiceImpl<ClassStudentMapper, ClassStudent> implements ClassStudentService {

    @Override
    public ClassStudent getClassStudentById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveClassStudent(ClassStudent classStudent) {
        return this.save(classStudent);
    }

    @Override
    public boolean updateClassStudent(ClassStudent classStudent) {
        return this.updateById(classStudent);
    }

    @Override
    public boolean deleteClassStudent(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<ClassStudent> getClassStudentByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public List<ClassStudent> getClassStudentsByClassId(Integer classId) {
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId);
        return this.list(wrapper);
    }

    
    @Override
    public List<ClassStudent> getClassStudentsByStudentId(Integer studentId) {
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getStudentId, studentId);
        return this.list(wrapper);
    }


    @Override
    public boolean updateStudentStatus(Integer studentId, Integer classId, String status) {
        LambdaUpdateWrapper<ClassStudent> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ClassStudent::getStudentId, studentId)
               .eq(ClassStudent::getClassId, classId)
               .set(ClassStudent::getStatus, status);
        return this.update(wrapper);
    }
} 