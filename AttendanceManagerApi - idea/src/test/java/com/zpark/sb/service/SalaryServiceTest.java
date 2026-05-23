package com.zpark.sb.service;

import com.zpark.sb.dao.SalaryDao;
import com.zpark.sb.entity.Check;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.Position;
import com.zpark.sb.entity.Salary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

    @Mock
    private SalaryDao salaryDao;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private PositionService positionService;

    @InjectMocks
    private SalaryService salaryService;

    @Test
    void payOffShouldCalculateWithDoublePrecision() {
        Check check = new Check();
        check.setEmployeeID("E100");
        check.setMonth("2026-05-23");
        check.setWorkDays(22);
        check.setCheckDays(11);
        check.setLateDays(1);
        check.setLeaveEarlyDays(1);
        check.setLeaveDays(0);
        when(salaryDao.findByNumberAndMonth(any(Check.class))).thenReturn(null);

        Employee employee = new Employee();
        employee.setPosition("P1");
        when(employeeService.findByNumber("E100")).thenReturn(employee);

        Position position = new Position();
        position.setMonthlySalary(22000);
        when(positionService.selectById("P1")).thenReturn(position);

        int result = salaryService.payOff(check);

        assertEquals(0, result);
        ArgumentCaptor<Salary> captor = ArgumentCaptor.forClass(Salary.class);
        verify(salaryDao).insert(captor.capture());
        assertEquals(10900D, captor.getValue().getSalary());
    }

    @Test
    void payOffShouldReturnDuplicateWhenSalaryExists() {
        Check check = new Check();
        check.setEmployeeID("E101");
        check.setMonth("2026-05");
        when(salaryDao.findByNumberAndMonth(any(Check.class))).thenReturn(new Salary());

        int result = salaryService.payOff(check);

        assertEquals(1, result);
        verify(salaryDao, never()).insert(any(Salary.class));
    }
}

