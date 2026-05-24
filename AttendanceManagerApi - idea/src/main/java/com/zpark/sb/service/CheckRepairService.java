package com.zpark.sb.service;

import com.zpark.sb.dao.CheckRepairDao;
import com.zpark.sb.entity.CheckRepair;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.Task;
import com.zpark.sb.entity.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CheckRepairService {

    public static final String TASK_TYPE_ID = "4";
    public static final String TASK_TYPE_NAME = "考勤补卡申请";
    public static final int SUCCESS = 0;
    public static final int INVALID_PARAM = 1;
    public static final int USER_OR_LEADER_NOT_FOUND = 2;
    public static final int DUPLICATED_ACTIVE_REPAIR = 3;

    @Autowired
    private CheckRepairDao checkRepairDao;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private EmployeeService employeeService;

    public int deleteById(String id) {
        return checkRepairDao.deleteById(id);
    }

    public int insert(CheckRepair checkRepair) {
        if (isInvalidRepair(checkRepair)) {
            return INVALID_PARAM;
        }

        Employee applicant = employeeService.findByNumber(checkRepair.getApplyNumber());
        if (applicant == null) {
            return USER_OR_LEADER_NOT_FOUND;
        }
        Employee leader = employeeService.getLeader(applicant);
        if (leader == null) {
            return USER_OR_LEADER_NOT_FOUND;
        }
        if (isFutureDate(checkRepair.getRepairDate())) {
            return INVALID_PARAM;
        }
        if (hasActiveOverlap(checkRepair)) {
            return DUPLICATED_ACTIVE_REPAIR;
        }

        String applyId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        checkRepair.setId(applyId);
        checkRepair.setApprovalID(taskId);
        checkRepair.setStatus("0");
        checkRepair.setTaskTypeID(TASK_TYPE_ID);
        if (checkRepair.getApplyTime() == null) {
            checkRepair.setApplyTime(new Date());
        }
        checkRepairDao.insert(checkRepair);

        Task task = new Task();
        task.setId(taskId);
        task.setApplyID(applyId);
        task.setApplyTime(checkRepair.getApplyTime());
        task.setTypeID(TASK_TYPE_ID);
        task.setApplyNumber(checkRepair.getApplyNumber());
        task.setName(checkRepair.getApplyName() + "的" + resolveTaskTypeName());
        task.setStatus("0");
        task.setReceiveNumber(leader.getNumber());
        taskService.insert(task);
        return SUCCESS;
    }

    public int revoke(CheckRepair checkRepair) {
        if (checkRepair == null || ObjectUtils.isEmpty(checkRepair.getId())) {
            return INVALID_PARAM;
        }
        CheckRepair existed = checkRepairDao.selectById(checkRepair.getId());
        if (existed == null || !"0".equals(existed.getStatus())) {
            return INVALID_PARAM;
        }

        existed.setStatus("3");
        checkRepairDao.update(existed);
        Task task = taskService.selectById(existed.getApprovalID());
        if (task != null && "0".equals(task.getStatus())) {
            task.setStatus("3");
            taskService.update(task);
        }
        return SUCCESS;
    }

    public CheckRepair selectById(String id) {
        return checkRepairDao.selectById(id);
    }

    public int update(CheckRepair checkRepair) {
        return checkRepairDao.update(checkRepair);
    }

    public List<CheckRepair> findByEmployeeNumber(String number) {
        List<CheckRepair> repairList = checkRepairDao.findByEmployeeNumber(number);
        for (CheckRepair item : repairList) {
            item.setStatusName(toStatusName(item.getStatus()));
            if ("1".equals(item.getStatus()) || "2".equals(item.getStatus())) {
                Task task = taskService.selectById(item.getApprovalID());
                if (task == null) {
                    continue;
                }
                Employee employee = employeeService.findByNumber(task.getApprovalNumber());
                if (employee != null) {
                    item.setApprovalName(employee.getName());
                }
                item.setApprovalTime(task.getApprovalTime());
            }
        }
        return repairList;
    }

    public Map<String, Object> getStatistics(String month) {
        List<CheckRepair> repairList = checkRepairDao.findForStatistics(month);
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("total", repairList.size());
        statistics.put("pending", countByStatus(repairList, "0"));
        statistics.put("approved", countByStatus(repairList, "1"));
        statistics.put("rejected", countByStatus(repairList, "2"));
        statistics.put("revoked", countByStatus(repairList, "3"));
        statistics.put("topEmployees", buildTopEmployees(repairList));
        statistics.put("typeItems", buildTypeItems(repairList));
        return statistics;
    }

    private String resolveTaskTypeName() {
        TaskType taskType = taskTypeService.selectById(TASK_TYPE_ID);
        return taskType == null ? TASK_TYPE_NAME : taskType.getName();
    }

    private String toStatusName(String status) {
        if ("1".equals(status)) {
            return "已通过";
        }
        if ("2".equals(status)) {
            return "已驳回";
        }
        if ("3".equals(status)) {
            return "已撤销";
        }
        return "待审批";
    }

    private long countByStatus(List<CheckRepair> repairList, String status) {
        return repairList.stream().filter(item -> status.equals(item.getStatus())).count();
    }

    private List<Map<String, Object>> buildTopEmployees(List<CheckRepair> repairList) {
        Map<String, Map<String, Object>> employeeMap = new LinkedHashMap<>();
        for (CheckRepair item : repairList) {
            String employeeNumber = item.getApplyNumber();
            Map<String, Object> row = employeeMap.computeIfAbsent(employeeNumber, key -> {
                Map<String, Object> value = new HashMap<>();
                value.put("employeeNumber", employeeNumber);
                value.put("employeeName", item.getApplyName() == null ? employeeNumber : item.getApplyName());
                value.put("count", 0);
                value.put("pending", 0);
                return value;
            });
            row.put("count", ((Integer) row.get("count")) + 1);
            if ("0".equals(item.getStatus())) {
                row.put("pending", ((Integer) row.get("pending")) + 1);
            }
        }
        return employeeMap.values().stream()
                .sorted((left, right) -> ((Integer) right.get("count")).compareTo((Integer) left.get("count")))
                .limit(5)
                .toList();
    }

    private List<Map<String, Object>> buildTypeItems(List<CheckRepair> repairList) {
        Map<String, Integer> typeMap = new LinkedHashMap<>();
        for (CheckRepair item : repairList) {
            String repairType = item.getRepairType() == null ? "未知" : item.getRepairType();
            typeMap.put(repairType, typeMap.getOrDefault(repairType, 0) + 1);
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : typeMap.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("type", entry.getKey());
            row.put("value", entry.getValue());
            items.add(row);
        }
        return items;
    }

    private boolean isInvalidRepair(CheckRepair checkRepair) {
        if (checkRepair == null
                || ObjectUtils.isEmpty(checkRepair.getApplyNumber())
                || ObjectUtils.isEmpty(checkRepair.getRepairDate())
                || ObjectUtils.isEmpty(checkRepair.getRepairType())
                || ObjectUtils.isEmpty(checkRepair.getReason())) {
            return true;
        }
        if ("上班补卡".equals(checkRepair.getRepairType())) {
            return checkRepair.getCheckOnTime() == null;
        }
        if ("下班补卡".equals(checkRepair.getRepairType())) {
            return checkRepair.getCheckOffTime() == null;
        }
        if ("上下班补卡".equals(checkRepair.getRepairType())) {
            return checkRepair.getCheckOnTime() == null || checkRepair.getCheckOffTime() == null;
        }
        return true;
    }

    private boolean hasActiveOverlap(CheckRepair checkRepair) {
        List<CheckRepair> activeList = checkRepairDao.findActiveByEmployeeNumberAndDate(checkRepair);
        for (CheckRepair active : activeList) {
            if (isRepairTypeOverlap(checkRepair.getRepairType(), active.getRepairType())) {
                return true;
            }
        }
        return false;
    }

    private boolean isFutureDate(String dateText) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setLenient(false);
            Date repairDate = formatter.parse(dateText);
            Date today = formatter.parse(formatter.format(new Date()));
            return repairDate.after(today);
        } catch (ParseException e) {
            return true;
        }
    }

    private boolean isRepairTypeOverlap(String nextType, String existedType) {
        return repairCoversCheckOn(nextType) && repairCoversCheckOn(existedType)
                || repairCoversCheckOff(nextType) && repairCoversCheckOff(existedType);
    }

    private boolean repairCoversCheckOn(String repairType) {
        return "上班补卡".equals(repairType) || "上下班补卡".equals(repairType);
    }

    private boolean repairCoversCheckOff(String repairType) {
        return "下班补卡".equals(repairType) || "上下班补卡".equals(repairType);
    }
}
