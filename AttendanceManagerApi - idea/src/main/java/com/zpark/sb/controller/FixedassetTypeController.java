package com.zpark.sb.controller;

import com.zpark.sb.entity.FixedassetType;
import com.zpark.sb.service.FixedassetTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/fixedassetType")
public class FixedassetTypeController {

    @Autowired
    private FixedassetTypeService fixedassetTypeService;

    @ResponseBody
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<FixedassetType> get(){
        return fixedassetTypeService.getAll();
    }
}
