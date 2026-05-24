package com.zpark.sb.entity.imports;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

public class FixedassetImportRow {

    @ExcelProperty(index = 0)
    private String number;

    @ExcelProperty(index = 1)
    private String name;

    @ExcelProperty(index = 2)
    private String typeNumber;

    @ExcelProperty(index = 3)
    private BigDecimal price;

    @ExcelProperty(index = 4)
    private String employeeNumber;

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

    public String getTypeNumber() {
        return typeNumber;
    }

    public void setTypeNumber(String typeNumber) {
        this.typeNumber = typeNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}

