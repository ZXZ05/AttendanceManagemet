package com.zpark.sb.service;

import com.zpark.sb.dao.CheckRepairDao;
import com.zpark.sb.dao.FixedassetsDao;
import com.zpark.sb.dao.LeaveDao;
import com.zpark.sb.dao.TaskDao;
import com.zpark.sb.entity.Apply;
import com.zpark.sb.entity.Check;
import com.zpark.sb.entity.CheckRepair;
import com.zpark.sb.entity.FixedassetType;
import com.zpark.sb.entity.Fixedassets;
import com.zpark.sb.entity.Leave;
import com.zpark.sb.entity.Task;
import com.zpark.sb.entity.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    public static final String TASK_TYPE_LEAVE = "1";
    public static final String TASK_TYPE_FIXEDASSET_BUY = "2";
    public static final String TASK_TYPE_FIXEDASSET_SCRAP = "3";

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private LeaveDao leaveDao;
    @Autowired
    private FixedassetsDao fixedassetsDao;
    @Autowired
    private CheckRepairDao checkRepairDao;
    @Autowired
    private CheckService checkService;
    @Autowired
    private LeaveTypeService leaveTypeService;
    @Autowired
    private FixedassetTypeService fixedassetTypeService;
    @Autowired
    private NotificationService notificationService;

    public int deleteById(String id) {
        return taskDao.deleteById(id);
    }

    public int insert(Task task) {
        taskDao.insert(task);
        notificationService.createNotice(
                task.getReceiveNumber(),
                "待审批提醒",
                "您有新的待审批任务: " + (task.getName() == null ? "-" : task.getName()),
                "PENDING_APPROVAL",
                "TASK",
                task.getId()
        );
        return 0;
    }

    public Task selectById(String id) {
        return taskDao.selectById(id);
    }

    public int update(Task task) {
        return taskDao.update(task);
    }

    public List<Task> getAllTodo(String receiveNumber) {
        return taskDao.getAllTodo(receiveNumber);
    }

    public List<Task> getAllHistoric(String receiveNumber) {
        return taskDao.getAllHistoric(receiveNumber);
    }

    public Apply findByApplyID(Task task) {
        task.setType(resolveTypeName(task.getTypeID()));
        return taskDao.findByApplyID(task);
    }

    public List<Task> getAll(String receiveNumber) {
        List<Task> taskList = taskDao.getAll(receiveNumber);
        for (Task item : taskList) {
            if ("0".equals(item.getStatus())) {
                item.setStatusName("待审批");
            } else if ("1".equals(item.getStatus())) {
                item.setStatusName("已通过");
            } else if ("2".equals(item.getStatus())) {
                item.setStatusName("已驳回");
            } else if ("3".equals(item.getStatus())) {
                item.setStatusName("已撤销");
            }
        }
        return taskList;
    }

    public int approval(Task task) throws ParseException {
        if (task == null || task.getTypeID() == null || task.getAdvice() == null) {
            return 1;
        }

        String typeID = task.getTypeID();
        String typeName = resolveTypeName(typeID);
        task.setType(typeName);
        Apply apply = taskDao.findByApplyID(task);
        if (apply == null) {
            return 1;
        }

        if ("yes".equals(task.getAdvice())) {
            task.setStatus("1");
            taskDao.update(task);

            if (TASK_TYPE_LEAVE.equals(typeID)) {
                Leave leave = leaveDao.selectById(apply.getId());
                if (leave == null) {
                    return 1;
                }
                leave.setStatus("1");
                leaveDao.update(leave);

                List<Check> checkList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date1 = sdf.format(leave.getBeginTime());
                String date2 = sdf.format(leave.getEndTime());
                Date beginDate = sdf.parse(date1);
                Date endDate = sdf.parse(date2);
                List<String> dateList = new ArrayList<>();
                dateList.add(date1);
                Calendar calBegin = Calendar.getInstance();
                calBegin.setTime(beginDate);
                while (endDate.after(calBegin.getTime())) {
                    calBegin.add(Calendar.DAY_OF_MONTH, 1);
                    dateList.add(sdf.format(calBegin.getTime()));
                }
                for (String item : dateList) {
                    Check check = new Check();
                    check.setDate(item);
                    checkList.add(check);
                }
                String remarks = leaveTypeService.selectById(leave.getTypeID()).getName();
                for (Check item : checkList) {
                    item.setId(UUID.randomUUID().toString());
                    item.setEmployeeID(leave.getApplyNumber());
                    item.setRemarks(remarks);
                    checkService.insert(item);
                }
            } else if (TASK_TYPE_FIXEDASSET_BUY.equals(typeID) || TASK_TYPE_FIXEDASSET_SCRAP.equals(typeID)) {
                Fixedassets fixedassets = fixedassetsDao.selectById(apply.getId());
                if (fixedassets == null) {
                    return 1;
                }
                fixedassets.setStatus("1");
                fixedassetsDao.update(fixedassets);
                FixedassetType fixedassetType = fixedassetTypeService.selectById(fixedassets.getTypeID());
                if (fixedassetType != null) {
                    int quantity = fixedassetType.getQuantity() == null ? 0 : fixedassetType.getQuantity();
                    fixedassetType.setQuantity(quantity + 1);
                    fixedassetTypeService.update(fixedassetType);
                }
            } else if (CheckRepairService.TASK_TYPE_ID.equals(typeID)) {
                CheckRepair checkRepair = checkRepairDao.selectById(apply.getId());
                if (checkRepair == null) {
                    return 1;
                }
                checkRepair.setStatus("1");
                checkRepairDao.update(checkRepair);
                checkService.repairAttendance(checkRepair);
            }
            notifyApprovalResult(apply, typeID, true);
            return 0;
        }

        if ("no".equals(task.getAdvice())) {
            task.setStatus("2");
            taskDao.update(task);
            if (TASK_TYPE_LEAVE.equals(typeID)) {
                Leave leave = leaveDao.selectById(apply.getId());
                if (leave != null) {
                    leave.setStatus("2");
                    leaveDao.update(leave);
                }
            } else if (TASK_TYPE_FIXEDASSET_BUY.equals(typeID) || TASK_TYPE_FIXEDASSET_SCRAP.equals(typeID)) {
                Fixedassets fixedassets = fixedassetsDao.selectById(apply.getId());
                if (fixedassets != null) {
                    fixedassets.setStatus("2");
                    fixedassetsDao.update(fixedassets);
                }
            } else if (CheckRepairService.TASK_TYPE_ID.equals(typeID)) {
                CheckRepair checkRepair = checkRepairDao.selectById(apply.getId());
                if (checkRepair != null) {
                    checkRepair.setStatus("2");
                    checkRepairDao.update(checkRepair);
                }
            }
            notifyApprovalResult(apply, typeID, false);
            return 0;
        }

        return 1;
    }

    private String resolveTypeName(String typeID) {
        TaskType taskType = taskTypeService.selectById(typeID);
        if (taskType != null) {
            return taskType.getName();
        }
        if (CheckRepairService.TASK_TYPE_ID.equals(typeID)) {
            return CheckRepairService.TASK_TYPE_NAME;
        }
        return "";
    }

    private void notifyApprovalResult(Apply apply, String typeID, boolean approved) {
        if (apply == null || apply.getApplyNumber() == null) {
            return;
        }
        String title = "审批结果提醒";
        String resultText = approved ? "已通过" : "未通过";
        String content;
        if (TASK_TYPE_LEAVE.equals(typeID)) {
            content = "您的请假申请" + resultText + "，请及时查看。";
        } else if (TASK_TYPE_FIXEDASSET_BUY.equals(typeID) || TASK_TYPE_FIXEDASSET_SCRAP.equals(typeID)) {
            content = "您的固定资产申请" + resultText + "，请及时查看。";
        } else if (CheckRepairService.TASK_TYPE_ID.equals(typeID)) {
            content = "您的补卡申请" + resultText + "，请及时查看。";
        } else {
            content = "您的审批申请" + resultText + "。";
        }
        notificationService.createNotice(
                apply.getApplyNumber(),
                title,
                content,
                "APPROVAL_RESULT",
                "TASK",
                apply.getId()
        );
    }
}
