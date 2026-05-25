package com.zpark.sb.dao;

import com.zpark.sb.entity.LoginRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginRecordDao {
    int insert(LoginRecord record);

    List<LoginRecord> listByEmployeeNumber(@Param("employeeNumber") String employeeNumber, @Param("limitSize") Integer limitSize);
}

