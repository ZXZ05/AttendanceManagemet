package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.Leave;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.EmployeeService;
import com.zpark.sb.service.LeaveService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;
    @Autowired
    private AuthContextService authContextService;
    @Autowired
    private EmployeeService employeeService;

    @ResponseBody
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public List<Leave> get(@RequestBody Leave leave, HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            leave.setApplyNumber(authContextService.getCurrentUserNumber(request));
        }
        return leaveService.findByEmployeeNumber(leave.getApplyNumber());
    }

    @ResponseBody
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    public Result insert(@RequestBody Leave leave, HttpServletRequest request){
        String currentUserNumber = authContextService.getCurrentUserNumber(request);
        Employee currentUser = employeeService.findByNumber(currentUserNumber);
        if (currentUser == null) {
            return Result.failure(ResultCode.USER_NOT_EXIST);
        }
        leave.setApplyNumber(currentUserNumber);
        leave.setApplyName(currentUser.getName());
        int size = leaveService.insert(leave);
        if(size == 1){
            return Result.failure(ResultCode.USER_HAS_EXISTED);
        }else{
            return Result.success();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public int update(@RequestBody Leave leave, HttpServletRequest request){
        Leave existed = leaveService.selectById(leave.getId());
        if (existed == null || !authContextService.isSelfOrAdmin(request, existed.getApplyNumber())) {
            return 403;
        }
        leave.setApplyNumber(existed.getApplyNumber());
        return leaveService.update(leave);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteById",method = RequestMethod.POST)
    public int deleteById(@RequestBody Leave leave, HttpServletRequest request){
        Leave existed = leaveService.selectById(leave.getId());
        if (existed == null || !authContextService.isSelfOrAdmin(request, existed.getApplyNumber())) {
            return 403;
        }
        return leaveService.deleteById(leave.getId());
    }
}
