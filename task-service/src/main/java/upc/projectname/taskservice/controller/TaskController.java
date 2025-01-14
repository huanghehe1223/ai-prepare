package upc.projectname.taskservice.controller;


import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import upc.projectname.taskservice.mapper.TaskMapper;
import upc.projectname.taskservice.service.ITaskService;
import upc.projectname.taskservice.utils.JwtUtils;
import upc.projectname.upccommon.domain.po.Result;
import upc.projectname.upccommon.domain.po.Task;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lww
 * @since 2024-07-20
 */
@RestController
//@RequestMapping("/task")
public class TaskController {
    @Autowired
    private ITaskService taskService;
    @Autowired
    private TaskMapper taskMapper;



    @PostMapping("/add")
    public Result addTask(@RequestBody Task task, @RequestHeader("token") String jwt) {


        Claims claims = JwtUtils.parseJWT(jwt);
        assert claims != null;
        Integer userid = claims.get("userid", Integer.class);
        task.setPublishUserId(userid);
        System.out.println(task);
        if (task.getImageUrl()==null||"".equals(task.getImageUrl()))
        {
            String before= "https://ui-avatars.com/api/?name=";
            String after="&background=455a64&color=ffffff";
            if (task.getTitle()!=null&&!task.getTitle().equals(""))
             task.setImageUrl(before+task.getTitle()+after);
            else task.setImageUrl(before+"订单"+after);
        }
        boolean success = taskService.addTask(task);

        if (success) {
            return Result.success();
        } else {
            return Result.error("Failed to add task，您的余额不足");
        }
    }

    @DeleteMapping("/delete")
    public Result deleteTask(Integer id) {
        boolean success = taskService.deleteTaskById(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error( "Failed to delete task");
        }
    }

    @PutMapping("/update")

    public Result updateTask(@RequestBody Task task) {
        boolean success = taskService.updateTask(task);
        if (success) {
            return Result.success(task);
        } else {
            return Result.error("Failed to update task");
        }
    }

    @GetMapping("/get/{id}")
    public Result getTaskById(@PathVariable   int id) {
        Task task= taskService.getTaskById(id);
        if (task!=null) {
            return Result.success(task);
        } else {
            return Result.error("Failed to find a task");
        }
    }

    @GetMapping("/list")
    public Result  getAllTask(Integer pageNum, Integer pageSize) {
        List<Task> tasks= taskService.getAllTask(pageNum,pageSize);
         return Result.success(tasks);
    }
    @GetMapping("/listUndo")
    public Result  getAllTaskUndo(Integer pageNum, Integer pageSize) {
        List<Task> tasks= taskService.getAllTaskUndo(pageNum,pageSize);
        return Result.success(tasks);
    }

    @GetMapping("/listUndoTotal")
    public Result  getAllTaskUndoTotal() {
        Integer tasks= taskService.getAllTaskUndoTotal();
        return Result.success(tasks);
    }

    @GetMapping("/getAllTask1")
    public Result  getAllTask1() {
        Integer tasks= taskMapper.getAllTask1();
        return Result.success(tasks);
    }
    @GetMapping("/getAllTask2")
    public Result  getAllTask2() {
        Integer tasks= taskMapper.getAllTask2();
        return Result.success(tasks);
    }
    @GetMapping("/getAllTask3")
    public Result  getAllTask3() {
        Integer tasks= taskMapper.getAllTask3();
        return Result.success(tasks);
    }


    //我发布的任务

    @GetMapping("/getPublishTask")
    public Result getPublishTask(Integer pageNum, Integer pageSize,Integer publishUserId,@RequestHeader("token") String jwt)
    {
        Map<String,Object> chaims= JwtUtils.parseJWT(jwt);
        Integer userid=(Integer) chaims.get("userid");
        publishUserId=userid;
        List<Task> taskList=taskService.getPublishTask(pageNum,pageSize,publishUserId);


        return Result.success(taskList);
    }
    @GetMapping("/getPublishTaskTotal")
    public Result getPublishTaskTotal(Integer publishUserId,@RequestHeader("token") String jwt)
    {
        Map<String,Object> chaims= JwtUtils.parseJWT(jwt);
        Integer userid=(Integer) chaims.get("userid");
        publishUserId=userid;
        Integer sum=taskService.getPublishTaskTotal(publishUserId);


        return Result.success(sum);
    }
    @GetMapping("/getPublishTaskDiverse")
    public Result getPublishTaskDiverse(Integer pageNum, Integer pageSize,Integer publishUserId,Integer state,@RequestHeader("token") String jwt)
    {
        Map<String,Object> chaims= JwtUtils.parseJWT(jwt);
        Integer userid=(Integer) chaims.get("userid");
        publishUserId=userid;
        List<Task> taskList=taskService.getPublishTaskDiverse(pageNum,pageSize,publishUserId,state);
        return Result.success(taskList);
    }

    //我接受的任务
    @GetMapping("/getAcceptTask")
    public Result getAcceptTask(Integer pageNum, Integer pageSize,Integer acceptUserId,@RequestHeader("token") String jwt)
    {
        Map<String,Object> chaims= JwtUtils.parseJWT(jwt);
        Integer userid=(Integer) chaims.get("userid");
        acceptUserId=userid;
        List<Task> taskList=taskService.getAcceptTask(pageNum,pageSize,acceptUserId);
        return Result.success(taskList);
    }

    @GetMapping("/getAcceptTaskTotal")
    public Result getAcceptTaskTotal(Integer acceptUserId,@RequestHeader("token") String jwt)
    {
        Integer sum=taskService.getAcceptTaskTotal(acceptUserId);
        return Result.success(sum);
    }

    @GetMapping("/getAcceptTaskDiverse")
    public Result getAcceptTaskDiverse(Integer pageNum, Integer pageSize,Integer acceptUserId,Integer state,@RequestHeader("token") String jwt)
    {
        Map<String,Object> chaims= JwtUtils.parseJWT(jwt);
        Integer userid=(Integer) chaims.get("userid");
        acceptUserId=userid;
        List<Task> taskList=taskService.getAcceptTaskDiverse(pageNum,pageSize,acceptUserId,state);
        return Result.success(taskList);
    }












}
