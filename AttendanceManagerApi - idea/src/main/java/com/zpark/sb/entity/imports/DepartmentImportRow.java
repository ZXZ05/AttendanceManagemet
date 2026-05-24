package com.zpark.sb.entity.imports;

import com.alibaba.excel.annotation.ExcelProperty;

public class DepartmentImportRow {

    @ExcelProperty(index = 0)
    private String number;

    @ExcelProperty(index = 1)
    private String name;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

