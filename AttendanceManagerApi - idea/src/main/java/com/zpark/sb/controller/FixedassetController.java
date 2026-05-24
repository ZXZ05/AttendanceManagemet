package com.zpark.sb.controller;

import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.Fixedassets;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.EmployeeService;
import com.zpark.sb.service.FixedassetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/fixedasset")
public class FixedassetController {

    @Autowired
    FixedassetService fixedassetService;
    @Autowired
    private AuthContextService authContextService;
    @Autowired
    private EmployeeService employeeService;

    @ResponseBody
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<Fixedassets> get(HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return fixedassetService.findByEmployeeNumber(authContextService.getCurrentUserNumber(request));
        }
        return fixedassetService.getAll();
    }

    @ResponseBody
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    public Result insert(@RequestBody Fixedassets fixedassets, HttpServletRequest request){
        String currentUserNumber = authContextService.getCurrentUserNumber(request);
        Employee currentUser = employeeService.findByNumber(currentUserNumber);
        if (currentUser == null) {
            return Result.failure(ResultCode.USER_NOT_EXIST);
        }
        fixedassets.setEmployeeNumber(currentUserNumber);
        fixedassets.setEmployeeName(currentUser.getName());
        int size = fixedassetService.insert(fixedassets);
        if(size == 1){
            return Result.failure(ResultCode.USER_HAS_EXISTED);
        }else{
            return Result.success();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public int update(@RequestBody Fixedassets fixedassets, HttpServletRequest request){
        Fixedassets existed = fixedassetService.selectById(fixedassets.getId());
        if (existed == null || !authContextService.isSelfOrAdmin(request, existed.getEmployeeNumber())) {
            return 403;
        }
        fixedassets.setEmployeeNumber(existed.getEmployeeNumber());
        return fixedassetService.update(fixedassets);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteById",method = RequestMethod.POST)
    public int deleteById(@RequestBody Fixedassets fixedassets, HttpServletRequest request){
        Fixedassets existed = fixedassetService.selectById(fixedassets.getId());
        if (existed == null || !authContextService.isSelfOrAdmin(request, existed.getEmployeeNumber())) {
            return 403;
        }
        return fixedassetService.deleteById(fixedassets.getId());
    }

    @ResponseBody
    @RequestMapping(value = "/findByEmployeeNumber",method = RequestMethod.POST)
    public List<Fixedassets> findByEmployeeNumber(@RequestBody Fixedassets fixedassets, HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            fixedassets.setEmployeeNumber(authContextService.getCurrentUserNumber(request));
        }
        return fixedassetService.findByEmployeeNumber(fixedassets.getEmployeeNumber());
    }

    @ResponseBody
    @RequestMapping(value = "/getRoomList",method = RequestMethod.GET)
    public List<Fixedassets> getRoomList(){
        return fixedassetService.getRoomList();
    }
}
