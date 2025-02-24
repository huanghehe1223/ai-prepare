package upc.projectname.userclassservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.Teacher;
import upc.projectname.userclassservice.mapper.TeacherMapper;
import upc.projectname.userclassservice.service.TeacherService;

import java.util.List;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Override
    public Teacher getTeacherById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveTeacher(Teacher teacher) {
        return this.save(teacher);
    }

    @Override
    public boolean updateTeacher(Teacher teacher) {
        return this.updateById(teacher);
    }

    @Override
    public boolean deleteTeacher(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<Teacher> getTeacherByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
} 