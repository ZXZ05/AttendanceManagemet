package com.zpark.sb.service;

import com.zpark.sb.dao.DepartmentDao;
import com.zpark.sb.dao.EmployeeDao;
import com.zpark.sb.dao.PositionDao;
import com.zpark.sb.entity.Department;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private PositionDao positionDao;

    public int deleteById(String id) {
        Employee employee = new Employee();
        employee.setDepartmentID(id);
        List<Employee> employeeList = employeeDao.findByNameAndDepartment(employee);
        if (!employeeList.isEmpty()) {
            return 1;
        }
        departmentDao.deleteById(id);
        List<Position> positionList = positionDao.findByDepartmentID(id);
        for (Position item : positionList) {
            positionDao.deleteById(item.getId());
        }
        return 0;
    }

    public int insert(Department department) {
        Department department1 = findByNumber(department.getNumber());
        if (department1 != null) {
            return 1;
        }
        department.setId(UUID.randomUUID().toString());
        department.setQuantity(0);
        department.setPosNum(0);
        departmentDao.insert(department);
        return 0;
    }

    public Department selectById(String id) {
        return departmentDao.selectById(id);
    }

    public int update(Department department) {
        return departmentDao.update(department);
    }

    public List<Department> getAll() {
        return departmentDao.getAll();
    }

    public List<Department> findByName(String name) {
        return departmentDao.findByName(name);
    }

    public Department findByNumber(String number) {
        return departmentDao.findByNumber(number);
    }
}
