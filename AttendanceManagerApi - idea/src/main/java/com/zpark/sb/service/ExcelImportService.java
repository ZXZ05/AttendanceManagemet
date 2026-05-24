package com.zpark.sb.service;

import com.alibaba.excel.EasyExcel;
import com.zpark.sb.entity.Customer;
import com.zpark.sb.entity.Department;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.EmployeeType;
import com.zpark.sb.entity.FixedassetType;
import com.zpark.sb.entity.Fixedassets;
import com.zpark.sb.entity.Position;
import com.zpark.sb.entity.imports.CustomerImportRow;
import com.zpark.sb.entity.imports.DepartmentImportRow;
import com.zpark.sb.entity.imports.EmployeeImportRow;
import com.zpark.sb.entity.imports.ExcelImportResult;
import com.zpark.sb.entity.imports.FixedassetImportRow;
import com.zpark.sb.entity.imports.PositionImportRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelImportService {

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeTypeService employeeTypeService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private FixedassetService fixedassetService;
    @Autowired
    private FixedassetTypeService fixedassetTypeService;

    public ExcelImportResult importDepartments(MultipartFile file) {
        List<DepartmentImportRow> rows = readRows(file, DepartmentImportRow.class);
        ExcelImportResult result = new ExcelImportResult();
        for (int i = 0; i < rows.size(); i++) {
            DepartmentImportRow row = rows.get(i);
            result.increaseTotal();
            if (isBlank(row.getNumber()) || isBlank(row.getName())) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 部门编号或名称为空");
                continue;
            }
            if (departmentService.findByNumber(row.getNumber().trim()) != null) {
                result.increaseSkipped();
                continue;
            }
            Department department = new Department();
            department.setNumber(row.getNumber().trim());
            department.setName(row.getName().trim());
            int code = departmentService.insert(department);
            if (code == 0) {
                result.increaseSuccess();
            } else {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 部门编号重复");
            }
        }
        return result;
    }

    public ExcelImportResult importPositions(MultipartFile file) {
        List<PositionImportRow> rows = readRows(file, PositionImportRow.class);
        ExcelImportResult result = new ExcelImportResult();

        Map<String, Department> departmentByNumber = new HashMap<>();
        for (Department item : departmentService.getAll()) {
            if (!isBlank(item.getNumber())) {
                departmentByNumber.put(item.getNumber().trim(), item);
            }
        }
        Map<String, EmployeeType> typeByNumber = new HashMap<>();
        for (EmployeeType item : employeeTypeService.getAll()) {
            if (!isBlank(item.getNumber())) {
                typeByNumber.put(item.getNumber().trim(), item);
            }
        }

        for (int i = 0; i < rows.size(); i++) {
            PositionImportRow row = rows.get(i);
            result.increaseTotal();
            if (isBlank(row.getNumber()) || isBlank(row.getName()) || isBlank(row.getDepartmentNumber()) || isBlank(row.getEmployeeTypeNumber())) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 岗位必填字段不完整");
                continue;
            }
            if (positionService.findByNumber(row.getNumber().trim()) != null) {
                result.increaseSkipped();
                continue;
            }
            Department department = departmentByNumber.get(row.getDepartmentNumber().trim());
            if (department == null) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 未找到部门编号 " + row.getDepartmentNumber());
                continue;
            }
            EmployeeType employeeType = typeByNumber.get(row.getEmployeeTypeNumber().trim());
            if (employeeType == null) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 未找到岗位类别编号 " + row.getEmployeeTypeNumber());
                continue;
            }

            Position position = new Position();
            position.setNumber(row.getNumber().trim());
            position.setName(row.getName().trim());
            position.setMonthlySalary(row.getMonthlySalary() == null ? 0 : row.getMonthlySalary());
            position.setDepartmentID(department.getId());
            position.setTypeID(employeeType.getId());

            int code = positionService.insert(position);
            if (code == 0) {
                result.increaseSuccess();
            } else {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 岗位编号重复");
            }
        }
        return result;
    }

    public ExcelImportResult importEmployees(MultipartFile file) {
        List<EmployeeImportRow> rows = readRows(file, EmployeeImportRow.class);
        ExcelImportResult result = new ExcelImportResult();

        Map<String, Department> departmentByNumber = new HashMap<>();
        for (Department item : departmentService.getAll()) {
            if (!isBlank(item.getNumber())) {
                departmentByNumber.put(item.getNumber().trim(), item);
            }
        }
        Map<String, Position> positionByNumber = new HashMap<>();
        for (Department department : departmentService.getAll()) {
            List<Position> positionList = positionService.findByDepartmentID(department.getId());
            for (Position item : positionList) {
                if (!isBlank(item.getNumber())) {
                    positionByNumber.put(item.getNumber().trim(), item);
                }
            }
        }

        for (int i = 0; i < rows.size(); i++) {
            EmployeeImportRow row = rows.get(i);
            result.increaseTotal();
            if (isBlank(row.getNumber()) || isBlank(row.getName()) || isBlank(row.getPositionNumber())) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 员工工号/姓名/岗位编号不能为空");
                continue;
            }
            if (employeeService.findByNumber(row.getNumber().trim()) != null) {
                result.increaseSkipped();
                continue;
            }

            Position position = positionByNumber.get(row.getPositionNumber().trim());
            if (position == null) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 未找到岗位编号 " + row.getPositionNumber());
                continue;
            }
            Department department = null;
            if (!isBlank(row.getDepartmentNumber())) {
                department = departmentByNumber.get(row.getDepartmentNumber().trim());
                if (department == null) {
                    result.increaseSkipped();
                    result.addError("第 " + (i + 2) + " 行: 未找到部门编号 " + row.getDepartmentNumber());
                    continue;
                }
            }

            Employee employee = new Employee();
            employee.setNumber(row.getNumber().trim());
            employee.setName(row.getName().trim());
            employee.setSex(trimToNull(row.getSex()));
            employee.setPhone(trimToNull(row.getPhone()));
            employee.setAddress(trimToNull(row.getAddress()));
            employee.setBirthday(trimToNull(row.getBirthday()));
            employee.setEntryDate(trimToNull(row.getEntryDate()));
            employee.setIdNumber(trimToNull(row.getIdNumber()));
            employee.setEducation(trimToNull(row.getEducation()));
            employee.setMarriage(trimToNull(row.getMarriage()));
            employee.setPosition(position.getId());
            employee.setDepartmentID(department == null ? position.getDepartmentID() : department.getId());

            int code = employeeService.insert(employee);
            if (code == 0) {
                result.increaseSuccess();
            } else {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 员工工号重复");
            }
        }
        return result;
    }

    public ExcelImportResult importCustomers(MultipartFile file, String currentUserNumber) {
        List<CustomerImportRow> rows = readRows(file, CustomerImportRow.class);
        ExcelImportResult result = new ExcelImportResult();
        for (int i = 0; i < rows.size(); i++) {
            CustomerImportRow row = rows.get(i);
            result.increaseTotal();
            if (isBlank(row.getNumber()) || isBlank(row.getName()) || isBlank(row.getType())) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 客户编号/名称/类型不能为空");
                continue;
            }
            if (customerService.findByNumber(row.getNumber().trim()) != null) {
                result.increaseSkipped();
                continue;
            }

            Customer customer = new Customer();
            customer.setNumber(row.getNumber().trim());
            customer.setName(row.getName().trim());
            customer.setType(row.getType().trim());
            customer.setPhone(trimToNull(row.getPhone()));
            customer.setAddress(trimToNull(row.getAddress()));
            customer.setRemarks(trimToNull(row.getRemarks()));
            customer.setApplyNumber(isBlank(row.getApplyNumber()) ? currentUserNumber : row.getApplyNumber().trim());

            int code = customerService.insert(customer);
            if (code == 0) {
                result.increaseSuccess();
            } else {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 客户编号重复");
            }
        }
        return result;
    }

    public ExcelImportResult importFixedassets(MultipartFile file, String currentUserNumber) {
        List<FixedassetImportRow> rows = readRows(file, FixedassetImportRow.class);
        ExcelImportResult result = new ExcelImportResult();

        Map<String, FixedassetType> fixedassetTypeByNumber = new HashMap<>();
        for (FixedassetType item : fixedassetTypeService.getAll()) {
            if (!isBlank(item.getNumber())) {
                fixedassetTypeByNumber.put(item.getNumber().trim(), item);
            }
        }

        for (int i = 0; i < rows.size(); i++) {
            FixedassetImportRow row = rows.get(i);
            result.increaseTotal();
            if (isBlank(row.getNumber()) || isBlank(row.getName()) || isBlank(row.getTypeNumber()) || row.getPrice() == null) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 固定资产必填字段不完整");
                continue;
            }
            if (fixedassetService.findByNumber(row.getNumber().trim()) != null) {
                result.increaseSkipped();
                continue;
            }

            FixedassetType fixedassetType = fixedassetTypeByNumber.get(row.getTypeNumber().trim());
            if (fixedassetType == null) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 未找到资产类别编号 " + row.getTypeNumber());
                continue;
            }

            String ownerNumber = isBlank(row.getEmployeeNumber()) ? currentUserNumber : row.getEmployeeNumber().trim();
            Employee owner = employeeService.findByNumber(ownerNumber);
            if (owner == null) {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 未找到资产归属人工号 " + ownerNumber);
                continue;
            }

            Fixedassets fixedassets = new Fixedassets();
            fixedassets.setNumber(row.getNumber().trim());
            fixedassets.setName(row.getName().trim());
            fixedassets.setTypeID(fixedassetType.getId());
            fixedassets.setPrice(row.getPrice());
            fixedassets.setEmployeeNumber(ownerNumber);
            fixedassets.setTaskTypeID("2");
            fixedassets.setStatus("1");
            fixedassets.setApplyTime(new Date());

            int code = fixedassetService.adminImportInsert(fixedassets);
            if (code == 0) {
                result.increaseSuccess();
            } else {
                result.increaseSkipped();
                result.addError("第 " + (i + 2) + " 行: 固定资产编号重复");
            }
        }
        return result;
    }

    private <T> List<T> readRows(MultipartFile file, Class<T> rowClass) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请先上传 Excel 文件");
        }
        try (InputStream inputStream = file.getInputStream()) {
            return EasyExcel.read(inputStream)
                    .head(rowClass)
                    .sheet(0)
                    .doReadSync();
        } catch (IOException e) {
            throw new IllegalArgumentException("读取 Excel 失败: " + e.getMessage(), e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}

