package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.entity.Apply;
import com.zpark.sb.entity.Task;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private AuthContextService authContextService;

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public List<Task> get(@RequestBody Task task, HttpServletRequest request) {
        if (!authContextService.isAdmin(request)) {
            task.setReceiveNumber(authContextService.getCurrentUserNumber(request));
        }
        return taskService.getAll(task.getReceiveNumber());
    }

    @ResponseBody
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Result insert(@RequestBody Task task, HttpServletRequest request) {
        if (!authContextService.isAdmin(request)) {
            return Result.failure(ResultCode.PERMISSION_NO_ACCESS);
        }
        int size = taskService.insert(task);
        if (size == 1) {
            return Result.failure(ResultCode.USER_HAS_EXISTED);
        } else {
            return Result.success();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public int update(@RequestBody Task task, HttpServletRequest request) {
        if (!authContextService.isAdmin(request)) {
            return 403;
        }
        return taskService.update(task);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public int deleteById(@RequestBody Task task, HttpServletRequest request) {
        if (!authContextService.isAdmin(request)) {
            return 403;
        }
        return taskService.deleteById(task.getId());
    }

    @ResponseBody
    @RequestMapping(value = "/getAllTodo", method = RequestMethod.POST)
    public List<Task> getAllTodo(@RequestBody Task task, HttpServletRequest request) {
        if (!authContextService.isAdmin(request)) {
            task.setReceiveNumber(authContextService.getCurrentUserNumber(request));
        }
        return taskService.getAllTodo(task.getReceiveNumber());
    }

    @ResponseBody
    @RequestMapping(value = "/getAllHistoric", method = RequestMethod.POST)
    public List<Task> getAllHistoric(@RequestBody Task task, HttpServletRequest request) {
        if (!authContextService.isAdmin(request)) {
            task.setReceiveNumber(authContextService.getCurrentUserNumber(request));
        }
        return taskService.getAllHistoric(task.getReceiveNumber());
    }

    @ResponseBody
    @RequestMapping(value = "/findByApplyID", method = RequestMethod.POST)
    public Apply findByApplyID(@RequestBody Task task, HttpServletRequest request) {
        Task existed = taskService.selectById(task.getId());
        if (existed == null
                || (!authContextService.isAdmin(request)
                && !authContextService.getCurrentUserNumber(request).equals(existed.getReceiveNumber())
                && !authContextService.getCurrentUserNumber(request).equals(existed.getApplyNumber()))) {
            return null;
        }
        return taskService.findByApplyID(task);
    }

    @ResponseBody
    @RequestMapping(value = "/approval", method = RequestMethod.POST)
    public Result approval(@RequestBody Task task, HttpServletRequest request) throws ParseException {
        Task existed = taskService.selectById(task.getId());
        if (existed == null || !authContextService.getCurrentUserNumber(request).equals(existed.getReceiveNumber())) {
            return Result.failure(ResultCode.PERMISSION_NO_ACCESS);
        }
        task.setApprovalNumber(authContextService.getCurrentUserNumber(request));
        task.setReceiveNumber(existed.getReceiveNumber());
        int size = taskService.approval(task);
        if (size == 0) {
            return Result.success();
        } else {
            return Result.failure(ResultCode.SYSTEM_INNER_ERROR);
        }
    }
}
