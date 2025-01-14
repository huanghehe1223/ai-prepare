package upc.projectname.taskservice.service;


import upc.projectname.upccommon.domain.po.Task;

import java.util.List;


public interface ITaskService {
    boolean addTask(Task task);
    boolean deleteTaskById(Integer id);
    boolean updateTask(Task task);
    Task getTaskById(Integer id);
    List<Task> getAllTask(Integer pageNum, Integer pageSize);

    List<Task> getPublishTask(Integer pageNum, Integer pageSize,Integer publishUserId);

    List<Task> getAcceptTask(Integer pageNum, Integer pageSize,Integer acceptUserId);

    List<Task> getAllTaskUndo(Integer pageNum, Integer pageSize);



    boolean updateTaskState1(Integer acceptUserId,Integer taskId);
    boolean updateTaskState3(int taskId);

    List<Task> getAcceptTaskDiverse(Integer pageNum, Integer pageSize,Integer acceptUserId, Integer state);

    List<Task> getPublishTaskDiverse(Integer pageNum, Integer pageSize,Integer publishUserId, Integer state);

    Integer getAcceptTaskTotal(Integer acceptUserId);

   Integer getAllTaskUndoTotal();

    Integer getPublishTaskTotal(Integer publishUserId);
}
