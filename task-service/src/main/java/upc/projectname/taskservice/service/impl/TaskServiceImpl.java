package upc.projectname.taskservice.service.impl;



//https://upc.2011914.xyz/ssl/alipay/pay?money=200
//https://upc.2011914.xyz/hh/alipay/pay

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import upc.projectname.taskservice.mapper.TaskMapper;
import upc.projectname.taskservice.service.ITaskService;
import upc.projectname.upccommon.api.client.TaskClient;
import upc.projectname.upccommon.api.client.UserClient;
import upc.projectname.upccommon.domain.po.Task;
import upc.projectname.upccommon.domain.po.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lww
 * @since 2024-07-20
 */
@Service
@Slf4j
public class TaskServiceImpl implements ITaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    UserClient userClient;
    @Autowired
    private TaskClient taskClient;


    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean addTask(Task task) {
        //数据库中字段为datetime对应Java中的数据类型
        //设置当前系统时间为当前时间
        task.setCreateTime(LocalDateTime.now());
        int success=taskMapper.insert(task);
        double userBalance=userClient.getUserById(task.getPublishUserId()).getData().getBalance();

        if(success>0&&userBalance>=task.getReward()+0.5){
            userClient.updateUserBalance(task.getReward()+0.5,task.getPublishUserId());
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean deleteTaskById(Integer id) {
        return taskMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateTask(Task task) {
        return taskMapper.updateById(task) > 0;
    }

    @Override
    public Task getTaskById(Integer id) {
//        TODO
        //根据id查询task
//        Task task= taskMapper.selectById(id);
//        int publishedUserId = task.getPublishUserId();
        Task task = taskMapper.selectById(id);
        if (task == null) {
            return null;
        }
        if (task.getAcceptUserId() != null) {
            User acceptUser = userClient.getUserById(task.getAcceptUserId()).getData();
            task.setAcceptUsername(acceptUser.getUsername());
            task.setAcceptUserPhone(acceptUser.getPhone());
//            task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
//            task.setAcceptUserPhone(userMapper.selectById(task.getAcceptUserId()).getPhone());//这一句放进里面了
        }
        else {
            task.setAcceptUsername(" ");
            task.setAcceptUserPhone(" ");
        }

        if (task.getPublishUserId()!=null) {
            task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());
//            task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
        }
        else task.setPublishUsername(" ");


        return  task;

    }

    @Override
    public List<Task> getAllTask(Integer pageNum, Integer pageSize) {
        Page<Task> page = new Page<>(pageNum, pageSize);
//        page.addOrder(OrderItem.desc("endTime"));
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.orderByAsc(
//                wrapper -> "CASE WHEN acceptUserId IS NULL THEN 1 ELSE 0 END, acceptUserId"
//        );
        queryWrapper.orderByAsc(Task::getAcceptUserId);
        Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
        List<Task> taskList = taskPage.getRecords();
        Set<Integer> userIdSet =taskList.stream()
                .flatMap(task-> Stream.of(task.getPublishUserId(),task.getAcceptUserId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<String,User> userMap=userClient.getUsersByIds(userIdSet)
                .getData()
                .stream()
                .collect(Collectors.toMap(user->String.valueOf(user.getUserid()),user->user));

//        Map<String,User> userMap=userMapper.selectBatchIds(userIdSet).stream()
//                .collect(Collectors.toMap(user->String.valueOf(user.getUserid()),user->user));
        User defaultUser=new User();
        defaultUser.setUsername(" ");
        defaultUser.setPhone(" ");
        System.out.println(userMap.getOrDefault(null,defaultUser));
        for (Task task : taskList) {
            User user=userMap.getOrDefault(String.valueOf(task.getAcceptUserId()),defaultUser);
            task.setAcceptUserPhone(user.getPhone());
            task.setAcceptUsername(user.getUsername());
            user=userMap.getOrDefault(String.valueOf(task.getPublishUserId()),defaultUser);
            task.setPublishUsername(user.getUsername());
        }
        return taskList;
    }

    @Override
    public List<Task> getPublishTask(Integer pageNum, Integer pageSize,Integer publishUserId) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<Task>();
        queryWrapper.eq(Task::getPublishUserId, publishUserId);
        Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
        List<Task> taskList = taskPage.getRecords();


//        List<Task> taskList = taskMapper.selectList(queryWrapper);
        for (Task task : taskList) {
            if (task.getAcceptUserId() != null) {
                task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());
//                task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
            }
            else task.setAcceptUsername(" ");
            if (task.getPublishUserId()!=null) {
                task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());
//                task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
            }
            else task.setPublishUsername(" ");
        }
        return taskList;
    }

    @Override
    public List<Task> getAcceptTask(Integer pageNum, Integer pageSize,Integer acceptUserId) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getAcceptUserId, acceptUserId);
        Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
        List<Task> taskList = taskPage.getRecords();


//        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Task::getAcceptUserId, acceptUserId);
//        List<Task> taskList = taskMapper.selectList(queryWrapper);
        for (Task task : taskList) {
            if (task.getAcceptUserId() != null) {
                task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());
//                task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
            }
            else task.setAcceptUsername(" ");
            if (task.getPublishUserId()!=null) {
                task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());
//                task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
            }
            else task.setPublishUsername(" ");
        }
        return taskList;
    }

    @Override
    public List<Task> getAllTaskUndo(Integer pageNum, Integer pageSize) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getState, 0);
        Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
        List<Task> taskList = taskPage.getRecords();

        for (Task task : taskList) {
            if (task.getAcceptUserId() != null) {
                task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());
//                task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
            }
            else task.setAcceptUsername(" ");
            if (task.getPublishUserId()!=null) {
                task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());
//                task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
            }
            else task.setPublishUsername(" ");
        }
        return taskList;
    }



    @Override
    public boolean updateTaskState1(Integer acceptUserId,Integer taskId) {

        LocalDateTime nowtime = LocalDateTime.now();
        return taskMapper.updateTaskState1(acceptUserId,taskId,nowtime)>0;
    }


    @Override
    public boolean updateTaskState3(int taskId) {
        return taskMapper.updateTaskState3(taskId)>0;
    }

    @Override
    public List<Task> getAcceptTaskDiverse(Integer pageNum, Integer pageSize, Integer acceptUserId, Integer state) {
        if(state==1){
            Page<Task> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Task::getAcceptUserId, acceptUserId)
                        .eq(Task::getState, 1);
            Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
            List<Task> taskList = taskPage.getRecords();
            for (Task task : taskList) {
                if (task.getAcceptUserId() != null) {
                    task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());
//                    task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
                }
                else task.setAcceptUsername(" ");
                if (task.getPublishUserId()!=null) {
                    task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());
//                    task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
                }
                else task.setPublishUsername(" ");
            }
            return taskList;
//            return taskMapper.getAcceptTaskDiverse1(acceptUserId);
        }else if(state==2){
            Page<Task> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Task::getAcceptUserId, acceptUserId)
                    .and(wrapper -> wrapper.eq(Task::getState, 2)
                            .or()
                            .eq(Task::getState, 3));
            Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
            List<Task> taskList = taskPage.getRecords();
            for (Task task : taskList) {
                if (task.getAcceptUserId() != null) {
                    task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());
//                    task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
                }
                else task.setAcceptUsername(" ");
                if (task.getPublishUserId()!=null) {
                    task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());
//                    task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
                }
                else task.setPublishUsername(" ");
            }
            return taskList;
//            return taskMapper.getAcceptTaskDiverse2(acceptUserId);
        }else{
            Page<Task> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Task::getAcceptUserId, acceptUserId)
                    .ne(Task::getState, 0);
            Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
            List<Task> taskList = taskPage.getRecords();
            for (Task task : taskList) {
                if (task.getAcceptUserId() != null) {
                    task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());
//                    task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
                }
                else task.setAcceptUsername(" ");
                if (task.getPublishUserId()!=null) {
                    task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());
//                    task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
                }
                else task.setPublishUsername(" ");
            }
            return taskList;
//            return taskMapper.getAcceptTaskDiverse3(acceptUserId);
        }
    }

    @Override
    public List<Task> getPublishTaskDiverse(Integer pageNum, Integer pageSize, Integer publishUserId, Integer state) {
        if(state==1){
            Page<Task> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Task::getPublishUserId, publishUserId)
                    .eq(Task::getState, 1);
            Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
            List<Task> taskList = taskPage.getRecords();
            for (Task task : taskList) {
                if (task.getAcceptUserId() != null) {
                    task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());
//                    task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
                }
                else task.setAcceptUsername(" ");
                if (task.getPublishUserId()!=null) {
                    task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());

//                    task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
                }
                else task.setPublishUsername(" ");
            }
            return taskList;


        }else if(state==2){

            Page<Task> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Task::getPublishUserId, publishUserId)
                    .eq(Task::getState, 2);
            Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
            List<Task> taskList = taskPage.getRecords();
            for (Task task : taskList) {
                if (task.getAcceptUserId() != null) {
                    task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());

//                    task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
                }
                else task.setAcceptUsername(" ");
                if (task.getPublishUserId()!=null) {
                    task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());

//                    task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
                }
                else task.setPublishUsername(" ");
            }
            return taskList;
        }else if(state==3){
            Page<Task> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Task::getPublishUserId, publishUserId)
                    .eq(Task::getState, 3);
            Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
            List<Task> taskList = taskPage.getRecords();
            for (Task task : taskList) {
                if (task.getAcceptUserId() != null) {
                    task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());

//                    task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
                }
                else task.setAcceptUsername(" ");
                if (task.getPublishUserId()!=null) {
                    task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());

//                    task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
                }
                else task.setPublishUsername(" ");
            }
            return taskList;
//            return taskMapper.getPublishTaskDiverse3(publishUserId);
        }else{
            Page<Task> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Task::getPublishUserId, publishUserId)
                    .eq(Task::getState, 0);
            Page<Task> taskPage= taskMapper.selectPage(page, queryWrapper);
            List<Task> taskList = taskPage.getRecords();
            for (Task task : taskList) {
                if (task.getAcceptUserId() != null) {
                    task.setAcceptUsername(userClient.getUserById(task.getAcceptUserId()).getData().getUsername());

//                    task.setAcceptUsername(userMapper.selectById(task.getAcceptUserId()).getUsername());
                }
                else task.setAcceptUsername(" ");
                if (task.getPublishUserId()!=null) {
                    task.setPublishUsername(userClient.getUserById(task.getPublishUserId()).getData().getUsername());

//                    task.setPublishUsername(userMapper.selectById(task.getPublishUserId()).getUsername());
                }
                else task.setPublishUsername(" ");
            }
            return taskList;
//            return taskMapper.getPublishTaskDiverse4(publishUserId);
        }
    }

    @Override
    public Integer getAcceptTaskTotal(Integer acceptUserId) {
        return  taskMapper.getAcceptTaskTotal(acceptUserId);
    }

    @Override
    public Integer getAllTaskUndoTotal() {
        return  taskMapper.getAllTaskUndoTotal();
    }

    @Override
    public Integer getPublishTaskTotal(Integer publishUserId) {
        return  taskMapper.getPublishTaskTotal(publishUserId);
    }


}
