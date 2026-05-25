package com.zpark.sb.service;

import com.zpark.sb.dao.SalaryDao;
import com.zpark.sb.entity.Check;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.Position;
import com.zpark.sb.entity.Salary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SalaryService {

    @Autowired
    private SalaryDao salaryDao;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private NotificationService notificationService;

    public int deleteById(String id) {
        return salaryDao.deleteById(id);
    }

    public int insert(Salary salary) {
        return salaryDao.insert(salary);
    }

    public Salary selectById(String id) {
        return salaryDao.selectById(id);
    }

    public int update(Salary record) {
        return salaryDao.update(record);
    }

    public int payOff(Check check) {
        if (check == null || check.getEmployeeID() == null || check.getMonth() == null || check.getMonth().length() < 7) {
            return 2;
        }
        if (findByNumberAndMonth(check) != null) {
            return 1;
        }

        Employee employee = employeeService.findByNumber(check.getEmployeeID());
        if (employee == null || employee.getPosition() == null) {
            return 2;
        }
        Position position = positionService.selectById(employee.getPosition());
        if (position == null || position.getMonthlySalary() == null) {
            return 2;
        }

        int workDays = check.getWorkDays() == null ? 0 : check.getWorkDays();
        int checkDays = check.getCheckDays() == null ? 0 : check.getCheckDays();
        int lateTimes = check.getLateDays() == null ? 0 : check.getLateDays();
        int leaveEarlyTimes = check.getLeaveEarlyDays() == null ? 0 : check.getLeaveEarlyDays();
        if (workDays <= 0) {
            return 2;
        }

        Salary salary = new Salary();
        salary.setId(UUID.randomUUID().toString());
        salary.setMonthlySalary(position.getMonthlySalary());
        salary.setWorkDays(workDays);
        salary.setCheckDays(checkDays);
        salary.setLeaveDays(check.getLeaveDays() == null ? 0 : check.getLeaveDays());
        salary.setLateTimes(lateTimes);
        salary.setLeaveEarlyTimes(leaveEarlyTimes);

        double value = position.getMonthlySalary() * (checkDays / (double) workDays) - lateTimes * 50D - leaveEarlyTimes * 50D;
        salary.setSalary(value);
        salary.setEmployeeID(check.getEmployeeID());
        salary.setMonth(check.getMonth().substring(0, 7));
        insert(salary);
        notificationService.createNotice(
                check.getEmployeeID(),
                "工资条发放提醒",
                salary.getMonth() + " 工资条已生成，请及时查看。",
                "SALARY_ISSUED",
                "SALARY",
                salary.getId()
        );
        return 0;
    }

    public Salary findByNumberAndMonth(Check check) {
        if (check == null || check.getMonth() == null || check.getMonth().length() < 7) {
            return null;
        }
        check.setMonth(check.getMonth().substring(0, 7));
        return salaryDao.findByNumberAndMonth(check);
    }
}
