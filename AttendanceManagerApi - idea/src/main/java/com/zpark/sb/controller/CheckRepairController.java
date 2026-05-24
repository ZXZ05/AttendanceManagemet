package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.entity.CheckRepair;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.CheckRepairService;
import com.zpark.sb.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/checkRepair")
public class CheckRepairController {

    @Autowired
    private CheckRepairService checkRepairService;
    @Autowired
    private AuthContextService authContextService;
    @Autowired
    private EmployeeService employeeService;

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result list(@RequestBody CheckRepair checkRepair, HttpServletRequest request) {
        if (!authContextService.isAdmin(request)) {
            checkRepair.setApplyNumber(authContextService.getCurrentUserNumber(request));
        }
        return Result.success(checkRepairService.findByEmployeeNumber(checkRepair.getApplyNumber()));
    }

    @ResponseBody
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Result insert(@RequestBody CheckRepair checkRepair, HttpServletRequest request) {
        String currentUserNumber = authContextService.getCurrentUserNumber(request);
        Employee currentUser = employeeService.findByNumber(currentUserNumber);
        if (currentUser == null) {
            return Result.failure(ResultCode.USER_NOT_EXIST);
        }
        checkRepair.setApplyNumber(currentUserNumber);
        checkRepair.setApplyName(currentUser.getName());
        int size = checkRepairService.insert(checkRepair);
        if (size == CheckRepairService.SUCCESS) {
            return Result.success();
        }
        if (size == CheckRepairService.INVALID_PARAM) {
            return Result.failure(ResultCode.PARAM_IS_INVALID);
        }
        if (size == CheckRepairService.USER_OR_LEADER_NOT_FOUND) {
            return Result.failure(ResultCode.USER_NOT_EXIST);
        }
        if (size == CheckRepairService.DUPLICATED_ACTIVE_REPAIR) {
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED);
        }
        return Result.failure(ResultCode.SYSTEM_INNER_ERROR);
    }

    @ResponseBody
    @RequestMapping(value = "/revoke", method = RequestMethod.POST)
    public Result revoke(@RequestBody CheckRepair checkRepair, HttpServletRequest request) {
        CheckRepair existed = checkRepairService.selectById(checkRepair.getId());
        if (existed == null || !authContextService.isSelfOrAdmin(request, existed.getApplyNumber())) {
            return Result.failure(ResultCode.PERMISSION_NO_ACCESS);
        }
        int size = checkRepairService.revoke(checkRepair);
        if (size == CheckRepairService.SUCCESS) {
            return Result.success();
        }
        return Result.failure(ResultCode.DATA_IS_WRONG);
    }

    @ResponseBody
    @RequestMapping(value = "/statistics", method = RequestMethod.POST)
    public Result statistics(@RequestBody(required = false) CheckRepair checkRepair, HttpServletRequest request) {
        if (!authContextService.canViewStatistics(request)) {
            return Result.failure(ResultCode.PERMISSION_NO_ACCESS);
        }
        String month = checkRepair == null ? null : checkRepair.getMonth();
        return Result.success(checkRepairService.getStatistics(month));
    }
}
