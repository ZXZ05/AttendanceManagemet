package com.zpark.sb.service;

import com.zpark.sb.dao.EmployeeDao;
import com.zpark.sb.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private EmployeeTypeService employeeTypeService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public int deleteById(String id) {
        Employee employee = employeeDao.selectById(id);
        if (employee == null) {
            return 404;
        }
        employeeDao.deleteById(id);
        syncDepartment(employee);
        return 200;
    }

    private void syncDepartment(Employee employee) {
        if (employee == null) {
            return;
        }
        if (employee.getDepartmentID() != null) {
            List<Employee> employees = employeeDao.selectByDepartmentId(employee.getDepartmentID());
            Department department = departmentService.selectById(employee.getDepartmentID());
            if (department != null) {
                department.setQuantity(employees.size());
                departmentService.update(department);
            }
        }
        Position position = null;
        if (employee.getPosition() != null) {
            List<Employee> employees = employeeDao.selectByPositionId(employee.getPosition());
            position = positionService.selectById(employee.getPosition());
            if (position != null) {
                position.setQuantity(employees.size());
                positionService.update(position);
            }
        }
        if (position != null && position.getTypeID() != null) {
            List<Employee> employees = employeeDao.selectByEmployeeType(position.getTypeID());
            EmployeeType employeeType = employeeTypeService.selectById(position.getTypeID());
            if (employeeType != null) {
                employeeType.setQuantity(employees.size());
                employeeTypeService.update(employeeType);
            }
        }
    }

    public int insert(Employee employee) {
        Employee employee1 = findByNumber(employee.getNumber());
        if (employee1 != null) {
            return 1;
        } else {

            Position position = positionService.selectById(employee.getPosition());
            if(position != null) {
                employee.setEmployeeType(position.getTypeID());
            }
            employee.setId(UUID.randomUUID().toString());
            employee.setPassword(encodePassword("123456"));
            employee.setWorkStatus("0");
            employeeDao.insert(employee);
            syncDepartment(employee);
            return 0;
        }
    }

    public int register(Employee employee) {
        Employee existed = findByNumber(employee.getNumber());
        if (existed != null) {
            return 1;
        }

        Employee registerEmployee = new Employee();
        registerEmployee.setId(UUID.randomUUID().toString());
        registerEmployee.setNumber(employee.getNumber());
        registerEmployee.setName(
                employee.getName() == null || employee.getName().trim().isEmpty()
                        ? employee.getNumber()
                        : employee.getName()
        );
        registerEmployee.setPhone(employee.getPhone());
        registerEmployee.setPassword(encodePassword(employee.getPassword()));
        registerEmployee.setWorkStatus("0");
        // default normal employee type
        registerEmployee.setEmployeeType("4");
        registerEmployee.setSex(
                employee.getSex() == null || employee.getSex().trim().isEmpty()
                        ? "未知"
                        : employee.getSex()
        );
        registerEmployee.setAddress(employee.getAddress());
        registerEmployee.setBirthday(employee.getBirthday());
        employeeDao.insert(registerEmployee);
        syncDepartment(registerEmployee);
        return 0;
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean verifyPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        if (isBcryptPassword(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }

    public void upgradePasswordIfNeeded(Employee employee, String rawPassword) {
        if (employee == null || rawPassword == null) {
            return;
        }
        if (!isBcryptPassword(employee.getPassword())) {
            employee.setPassword(encodePassword(rawPassword));
            employeeDao.update(employee);
        }
    }

    private boolean isBcryptPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }

    public Employee selectById(String id) {
        return employeeDao.selectById(id);
    }

    public int update(Employee employee) {
        Position position = positionService.selectById(employee.getPosition());
        if(position != null) {
            employee.setEmployeeType(position.getTypeID());
        }
        employeeDao.update(employee);
        syncDepartment(employee);
        return 1;
    }

    public List<Employee> getAll() {
        return employeeDao.getAll();
    }


    public Employee findByNumber(String number) {
        return employeeDao.findByNumber(number);
    }

    public List<Employee> findByNameAndDepartment(Employee employee) {
        return employeeDao.findByNameAndDepartment(employee);
    }

    public Employee getMinister(Employee employee) {
        return employeeDao.getMinister(employee);
    }

    public Employee getBoss(Employee employee) {
        return employeeDao.getBoss(employee);
    }

    public Employee getLeader(Employee employee) {
        if (employee == null || employee.getType() == null) {
            return null;
        }
        if (employee.getType().equals("1")) {
            Employee employee1 = getMinister(employee);
            if (employee1 != null) {
                return employee1;
            } else {
                Employee employee2 = getBoss(employee);
                return employee2;
            }
        } else if (employee.getType().equals("2")) {
            Employee employee2 = getBoss(employee);
            return employee2;
        } else {
            return employee;
        }
    }

    public List<Chart> getEducation() {
        return employeeDao.getEducation();
    }

    public List<Employee> findByPositionID(String positionID) {
        return employeeDao.findByPositionID(positionID);
    }

    public List<Chart> getAge() {
        return employeeDao.getAge();
    }

    public List<Chart> getNew() {
        return employeeDao.getNew();
    }

}
