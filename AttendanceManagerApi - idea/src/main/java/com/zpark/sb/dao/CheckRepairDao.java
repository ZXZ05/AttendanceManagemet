package com.zpark.sb.dao;

import com.zpark.sb.entity.CheckRepair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CheckRepairDao {
    int deleteById(String id);

    int insert(CheckRepair checkRepair);

    CheckRepair selectById(String id);

    int update(CheckRepair checkRepair);

    List<CheckRepair> findByEmployeeNumber(@Param("applyNumber") String applyNumber);

    List<CheckRepair> findActiveByEmployeeNumberAndDate(CheckRepair checkRepair);

    List<CheckRepair> findForStatistics(@Param("month") String month);
}
