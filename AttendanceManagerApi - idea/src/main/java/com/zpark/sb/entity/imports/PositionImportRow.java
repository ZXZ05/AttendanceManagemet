package com.zpark.sb.entity.imports;

import com.alibaba.excel.annotation.ExcelProperty;

public class PositionImportRow {

    @ExcelProperty(index = 0)
    private String number;

    @ExcelProperty(index = 1)
    private String name;

    @ExcelProperty(index = 2)
    private Integer monthlySalary;

    @ExcelProperty(index = 3)
    private String departmentNumber;

    @ExcelProperty(index = 4)
    private String employeeTypeNumber;

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

    public Integer getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(Integer monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    public String getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(String departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    public String getEmployeeTypeNumber() {
        return employeeTypeNumber;
    }

    public void setEmployeeTypeNumber(String employeeTypeNumber) {
        this.employeeTypeNumber = employeeTypeNumber;
    }
}

