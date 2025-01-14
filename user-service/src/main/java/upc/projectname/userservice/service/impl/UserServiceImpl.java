package upc.projectname.userservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.User;
import upc.projectname.userservice.mapper.UserMapper;
import upc.projectname.userservice.service.IUserService;

import java.util.List;




@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public boolean deleteUserById(Integer userid) {
        return userMapper.deleteById(userid) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    public User getUserById(Integer userid) {
        return userMapper.selectById(userid);
    }

    @Override
    public List<User> getAllUsers(Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> userPage= userMapper.selectPage(page, null);
        List<User> comments = userPage.getRecords();
        return comments;
    }

    @Override
    public List<User> getAllDeliveryUsers(Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("score", 0); // 添加查询条件 score=0
        Page<User> userPage= userMapper.selectPage(page, queryWrapper);
        List<User> users = userPage.getRecords();
        return users;

//
//        return userMapper.selectList(queryWrapper);
    }

    @Override
    public boolean updateUserBalance(Double money,Integer id) {
        boolean success=userMapper.updateUserBalance(money,id)>0;
        return success;
    }

    @Override
    public List<User> getUsersByIds(List<Integer> ids) {
        return userMapper.selectBatchIds(ids);
    }


    @Override
public boolean register(User user) {

    LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
    lambdaQueryWrapper.eq(User::getEmail,user.getEmail());
    User user1=userMapper.selectOne(lambdaQueryWrapper);
    if (user1!=null)
    {
        return false;
    }
    else
    {
        userMapper.insert(user);
        return true;
    }
}

@Override
public User login(User user) {
    LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
    lambdaQueryWrapper.eq(User::getEmail,user.getEmail());
    lambdaQueryWrapper.eq(User::getPassword,user.getPassword());
    User user1=userMapper.selectOne(lambdaQueryWrapper);
    return user1;
}




}


