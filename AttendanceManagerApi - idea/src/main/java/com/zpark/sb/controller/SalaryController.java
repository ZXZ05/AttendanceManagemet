package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.entity.Check;
import com.zpark.sb.entity.Salary;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.SalaryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;
    @Autowired
    private AuthContextService authContextService;

    @ResponseBody
    @RequestMapping(value = "/payOff",method = RequestMethod.POST)
    public Result payOff(@RequestBody Check check, HttpServletRequest request) {
        if (!authContextService.canManageFinance(request)) {
            return Result.failure(ResultCode.PERMISSION_NO_ACCESS);
        }
        int size = salaryService.payOff(check);
        if(size == 0){
            return Result.success();
        }else if(size == 1){
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED);
        }else{
            return Result.failure(ResultCode.SERVER_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/findByNumberAndMonth",method = RequestMethod.POST)
    public Salary findByNumberAndMonth(@RequestBody Check check, HttpServletRequest request) {
        if (!authContextService.canManageFinance(request)) {
            check.setEmployeeID(authContextService.getCurrentUserNumber(request));
        }
        return salaryService.findByNumberAndMonth(check);
    }
}
