package com.zpark.sb.dao;

import com.zpark.sb.entity.Fixedassets;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FixedassetsDao {
    int deleteById(String id);

    int insert(Fixedassets record);

    Fixedassets selectById(String id);

    int update(Fixedassets record);

    List<Fixedassets> getAll();

    Fixedassets findByNumber(@Param("number")String number);

    List<Fixedassets> findByEmployeeNumber(@Param("number")String number);

    List<Fixedassets> getRoomList();
}