package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.entity.Check;
import com.zpark.sb.entity.Salary;
import com.zpark.sb.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @ResponseBody
    @RequestMapping(value = "/payOff",method = RequestMethod.POST)
    public Result payOff(@RequestBody Check check) {
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
    public Salary findByNumberAndMonth(@RequestBody Check check) {
        return salaryService.findByNumberAndMonth(check);
    }
}
