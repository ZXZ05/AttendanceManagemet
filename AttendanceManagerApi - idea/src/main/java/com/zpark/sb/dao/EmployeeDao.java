package com.zpark.sb.dao;

import com.zpark.sb.entity.Chart;
import com.zpark.sb.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeDao {
    int deleteById(String id);

    int insert(Employee record);

    Employee selectById(String id);

    int update(Employee record);

    List<Employee> getAll();

    Employee findByNumber(@Param("number")String number);

    Employee findDetailByNumber(@Param("number")String number);

    List<Employee> findByNameAndDepartment(Employee employee);

    Employee getMinister(Employee employee);

    Employee getBoss(Employee employee);

    List<Chart> getEducation();

    List<Employee> findByPositionID(@Param("positionID")String positionID);

    List<Chart> getAge();

    List<Chart> getNew();

    List<Employee> selectByDepartmentId(@Param("departmentId") String departmentId);

    List<Employee> selectByPositionId(@Param("position") String position);

    List<Employee> selectByEmployeeType(@Param("employeeType") String employeeType);
}
