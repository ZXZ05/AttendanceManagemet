package com.zpark.sb.controller;

import com.zpark.sb.entity.EmployeeType;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.EmployeeTypeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/employeeType")
public class EmployeeTypeController {

    @Autowired
    private EmployeeTypeService employeeTypeService;
    @Autowired
    private AuthContextService authContextService;

    @ResponseBody
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<EmployeeType> get(HttpServletRequest request){
        if (!authContextService.isAdmin(request)) {
            return Collections.emptyList();
        }
        return employeeTypeService.getAll();
    }
}
