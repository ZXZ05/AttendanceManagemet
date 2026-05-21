package com.zpark.sb.controller;

import com.zpark.sb.entity.EmployeeType;
import com.zpark.sb.service.EmployeeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/employeeType")
public class EmployeeTypeController {

    @Autowired
    private EmployeeTypeService employeeTypeService;

    @ResponseBody
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<EmployeeType> get(){
        return employeeTypeService.getAll();
    }
}
