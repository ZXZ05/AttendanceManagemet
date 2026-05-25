package com.zpark.sb.service;

import com.zpark.sb.dao.StatisticsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private StatisticsDao statisticsDao;

    public Map<String, Object> getOverview(String month, Integer months) {
        int monthSpan = normalizeMonthSpan(months);
        YearMonth endMonth = parseMonthOrNow(month);
        YearMonth startMonth = endMonth.minusMonths(monthSpan - 1L);

        String startMonthText = startMonth.format(MONTH_FORMATTER);
        String endMonthText = endMonth.format(MONTH_FORMATTER);

        List<String> monthAxis = buildMonthAxis(startMonth, endMonth);
        List<Map<String, Object>> departmentDistribution = normalizeDepartment(statisticsDao.getDepartmentDistribution());
        List<Map<String, Object>> attendanceTrend = normalizeSingleTrend(statisticsDao.getAttendanceTrend(startMonthText, endMonthText), monthAxis);
        List<Map<String, Object>> lateEarlyTrend = normalizeLateEarlyTrend(statisticsDao.getLateEarlyTrend(startMonthText, endMonthText), monthAxis);
        List<Map<String, Object>> leaveTypeRatio = normalizeLeaveRatio(statisticsDao.getLeaveTypeRatio(startMonthText, endMonthText));
        List<Map<String, Object>> assetAmountTrend = normalizeSingleTrend(statisticsDao.getAssetAmountTrend(startMonthText, endMonthText), monthAxis);
        List<Map<String, Object>> salaryAmountTrend = normalizeSingleTrend(statisticsDao.getSalaryAmountTrend(startMonthText, endMonthText), monthAxis);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("range", buildRangeInfo(startMonthText, endMonthText, monthSpan));
        result.put("departmentDistribution", departmentDistribution);
        result.put("attendanceTrend", attendanceTrend);
        result.put("lateEarlyTrend", lateEarlyTrend);
        result.put("leaveTypeRatio", leaveTypeRatio);
        result.put("assetAmountTrend", assetAmountTrend);
        result.put("salaryAmountTrend", salaryAmountTrend);
        return result;
    }

    private int normalizeMonthSpan(Integer months) {
        if (months == null || months <= 0) {
            return 6;
        }
        return Math.min(months, 24);
    }

    private YearMonth parseMonthOrNow(String month) {
        if (month == null || month.trim().isEmpty()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month.trim(), MONTH_FORMATTER);
        } catch (Exception ignore) {
            return YearMonth.now();
        }
    }

    private List<String> buildMonthAxis(YearMonth startMonth, YearMonth endMonth) {
        List<String> axis = new ArrayList<>();
        YearMonth current = startMonth;
        while (!current.isAfter(endMonth)) {
            axis.add(current.format(MONTH_FORMATTER));
            current = current.plusMonths(1);
        }
        return axis;
    }

    private Map<String, Object> buildRangeInfo(String startMonth, String endMonth, int months) {
        Map<String, Object> range = new HashMap<>();
        range.put("startMonth", startMonth);
        range.put("endMonth", endMonth);
        range.put("months", months);
        return range;
    }

    private List<Map<String, Object>> normalizeDepartment(List<Map<String, Object>> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (rows == null) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", toText(row.get("type"), "未分组"));
            item.put("value", toInt(row.get("value")));
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> normalizeSingleTrend(List<Map<String, Object>> rows, List<String> monthAxis) {
        Map<String, Integer> monthValue = new HashMap<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String month = toText(row.get("month"), "");
                if (!month.isEmpty()) {
                    monthValue.put(month, toInt(row.get("value")));
                }
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (String month : monthAxis) {
            Map<String, Object> item = new HashMap<>();
            item.put("time", month);
            item.put("value", monthValue.getOrDefault(month, 0));
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> normalizeLateEarlyTrend(List<Map<String, Object>> rows, List<String> monthAxis) {
        Map<String, Integer> lateMap = new HashMap<>();
        Map<String, Integer> earlyMap = new HashMap<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String month = toText(row.get("month"), "");
                if (month.isEmpty()) {
                    continue;
                }
                lateMap.put(month, toInt(row.get("lateValue")));
                earlyMap.put(month, toInt(row.get("earlyValue")));
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (String month : monthAxis) {
            Map<String, Object> item = new HashMap<>();
            item.put("time", month);
            item.put("lateValue", lateMap.getOrDefault(month, 0));
            item.put("earlyValue", earlyMap.getOrDefault(month, 0));
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> normalizeLeaveRatio(List<Map<String, Object>> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                total = total.add(toDecimal(row.get("value")));
            }
            for (Map<String, Object> row : rows) {
                BigDecimal value = toDecimal(row.get("value"));
                BigDecimal percent = BigDecimal.ZERO;
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    percent = value.divide(total, 4, RoundingMode.HALF_UP);
                }
                Map<String, Object> item = new HashMap<>();
                item.put("type", toText(row.get("type"), "未分类"));
                item.put("value", value.doubleValue());
                item.put("percent", percent.doubleValue());
                result.add(item);
            }
        }
        return result;
    }

    private String toText(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? defaultValue : text;
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (Exception ignore) {
            return 0;
        }
    }

    private BigDecimal toDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(value).trim());
        } catch (Exception ignore) {
            return BigDecimal.ZERO;
        }
    }
}
