package com.zpark.sb.entity.imports;

import com.alibaba.excel.annotation.ExcelProperty;

public class CustomerImportRow {

    @ExcelProperty(index = 0)
    private String number;

    @ExcelProperty(index = 1)
    private String name;

    @ExcelProperty(index = 2)
    private String type;

    @ExcelProperty(index = 3)
    private String phone;

    @ExcelProperty(index = 4)
    private String address;

    @ExcelProperty(index = 5)
    private String remarks;

    @ExcelProperty(index = 6)
    private String applyNumber;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getApplyNumber() {
        return applyNumber;
    }

    public void setApplyNumber(String applyNumber) {
        this.applyNumber = applyNumber;
    }
}

