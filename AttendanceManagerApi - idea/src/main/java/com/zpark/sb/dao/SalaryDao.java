package com.zpark.sb.dao;

import com.zpark.sb.entity.Check;
import com.zpark.sb.entity.Salary;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SalaryDao {
    int deleteById(String id);

    int insert(Salary record);

    Salary selectById(String id);

    int update(Salary record);

    Salary findByNumberAndMonth(Check check);
}