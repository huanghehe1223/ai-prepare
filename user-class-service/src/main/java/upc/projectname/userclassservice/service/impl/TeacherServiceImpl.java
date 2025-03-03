package upc.projectname.userclassservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Teacher;
import upc.projectname.userclassservice.mapper.TeacherMapper;
import upc.projectname.userclassservice.service.TeacherService;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.userclassservice.utils.JwtUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Result<Boolean> teacherRegister(Teacher teacher) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getUserName, teacher.getUserName());
        Teacher selectOne = this.baseMapper.selectOne(wrapper);
        if (selectOne != null) {
            return Result.error("用户名已存在");
        }
        boolean save = this.save(teacher);
        if (save) {
            return Result.success(true, "注册成功");
        }
        return Result.error("注册失败");
    }

    @Override
    public Result<String> teacherLogin(String userName, String password) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getUserName, userName);
        wrapper.eq(Teacher::getPassword, password);
        Teacher selectOne = this.baseMapper.selectOne(wrapper);
        if (selectOne == null) {
            return Result.error("用户名或密码错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("teacherId", selectOne.getTeacherId());
        String jwt = JwtUtils.createJwt(map);
        return Result.success(jwt);
    }
} 