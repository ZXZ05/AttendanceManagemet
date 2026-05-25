package com.zpark.sb.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsDao {
    List<Map<String, Object>> getDepartmentDistribution();

    List<Map<String, Object>> getAttendanceTrend(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);

    List<Map<String, Object>> getLateEarlyTrend(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);

    List<Map<String, Object>> getLeaveTypeRatio(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);

    List<Map<String, Object>> getAssetAmountTrend(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);

    List<Map<String, Object>> getSalaryAmountTrend(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);
}

