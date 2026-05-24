package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.config.AuthInterceptor;
import com.zpark.sb.entity.Chart;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private AuthContextService authContextService;

    @ResponseBody
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<Employee> get(HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return Collections.emptyList();
        }
        return employeeService.getAll();
    }

    @ResponseBody
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    public Result insert(@RequestBody Employee employee, HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return Result.failure(ResultCode.PERMISSION_NO_ACCESS);
        }
        int size = employeeService.insert(employee);
        if(size == 1){
            return Result.failure(ResultCode.USER_HAS_EXISTED);
        }else{
            return Result.success();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public int update(@RequestBody Employee employee, HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return 403;
        }
        return employeeService.update(employee);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteById",method = RequestMethod.POST)
    public int deleteById(@RequestBody Employee employee, HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return 403;
        }
        return employeeService.deleteById(employee.getId());
    }

    @ResponseBody
    @RequestMapping(value = "/findByNameAndDepartment",method = RequestMethod.POST)
    public List<Employee> findByNameAndDepartment(@RequestBody Employee employee, HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return Collections.emptyList();
        }
        return employeeService.findByNameAndDepartment(employee);
    }

    @ResponseBody
    @RequestMapping(value = "/updatePassword",method = RequestMethod.POST)
    public Result updatePassword(@RequestBody Employee employee, HttpServletRequest request){
        String currentUserNumber = (String) request.getAttribute(AuthInterceptor.CURRENT_USER_NUMBER_ATTR);
        if (currentUserNumber == null || currentUserNumber.trim().isEmpty()) {
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);
        }
        if (employee == null
                || employee.getPassword() == null
                || employee.getPassword().trim().isEmpty()
                || employee.getOldPassword() == null
                || employee.getOldPassword().trim().isEmpty()) {
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        if (employee.getPassword().trim().length() < 6) {
            return Result.failure(ResultCode.PARAM_IS_INVALID);
        }
        if (employee.getNumber() != null
                && !employee.getNumber().trim().isEmpty()
                && !currentUserNumber.equals(employee.getNumber().trim())) {
            return Result.failure(ResultCode.PERMISSION_NO_ACCESS);
        }

        Employee employee1 = employeeService.findByNumber(currentUserNumber);
        if (employee1 == null) {
            return Result.failure(ResultCode.USER_NOT_EXIST);
        }
        if (!employeeService.verifyPassword(employee.getOldPassword(), employee1.getPassword())) {
            return Result.failure(ResultCode.USER_LOGIN_ERROR);
        }
        employee1.setPassword(employeeService.encodePassword(employee.getPassword()));
        employeeService.update(employee1);
        return Result.success();
    }

    @ResponseBody
    @RequestMapping(value = "/findByNumber",method = RequestMethod.POST)
    public Employee findByNumber(@RequestBody Employee employee, HttpServletRequest request){
        if (employee == null || !authContextService.isSelfOrAdmin(request, employee.getNumber())) {
            return null;
        }
        return employeeService.findByNumber(employee.getNumber());
    }

    @ResponseBody
    @RequestMapping(value = "/getEducation",method = RequestMethod.GET)
    public List<Chart> getEducation(HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return Collections.emptyList();
        }
        return employeeService.getEducation();
    }

    @ResponseBody
    @RequestMapping(value = "/getAge",method = RequestMethod.GET)
    public List<Chart> getAge(HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return Collections.emptyList();
        }
        return employeeService.getAge();
    }

    @ResponseBody
    @RequestMapping(value = "/getNew",method = RequestMethod.GET)
    public List<Chart> getNew(HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return Collections.emptyList();
        }
        return employeeService.getNew();
    }
}
