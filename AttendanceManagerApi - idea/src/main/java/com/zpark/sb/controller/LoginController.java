package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private EmployeeService employeeService;

    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Result login(@RequestBody Employee employee, HttpServletResponse response){
        if (employee == null
                || isBlank(employee.getNumber())
                || isBlank(employee.getPassword())) {
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        int s;
        Employee employee1 = employeeService.findByNumber(employee.getNumber());
        if(employee1 != null){
            if(employee.getPassword().equals(employee1.getPassword())){
                s = 1;
            }else {
                s = 0;
            }
        }else {
            s = -1;
        }
        if(s == 1){
            return Result.success(buildLoginUser(employee1));
        }else if(s == 0){
            return Result.failure(ResultCode.USER_LOGIN_ERROR);
        }else if(s == -1){
            return Result.failure(ResultCode.USER_NOT_EXIST);
        }else {
            return Result.failure(ResultCode.SERVER_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result register(@RequestBody Employee employee) {
        if (employee == null
                || isBlank(employee.getNumber())
                || isBlank(employee.getPassword())
                || isBlank(employee.getName())
                || isBlank(employee.getPhone())) {
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        if (employee.getPassword().length() < 6) {
            return Result.failure(ResultCode.PARAM_IS_INVALID);
        }
        int size = employeeService.register(employee);
        if (size == 1) {
            return Result.failure(ResultCode.USER_HAS_EXISTED);
        }
        Employee saved = employeeService.findByNumber(employee.getNumber());
        return Result.success(buildLoginUser(saved));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Map<String, Object> buildLoginUser(Employee employee) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", employee.getId());
        user.put("number", employee.getNumber());
        user.put("name", employee.getName());
        user.put("phone", employee.getPhone());
        user.put("type", employee.getType());
        user.put("departmentID", employee.getDepartmentID());
        return user;
    }
}
