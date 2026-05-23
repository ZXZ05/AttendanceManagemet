package com.zpark.sb.service;

import com.zpark.sb.dao.FixedassetsDao;
import com.zpark.sb.dao.LeaveDao;
import com.zpark.sb.dao.TaskDao;
import com.zpark.sb.entity.Apply;
import com.zpark.sb.entity.Check;
import com.zpark.sb.entity.FixedassetType;
import com.zpark.sb.entity.Fixedassets;
import com.zpark.sb.entity.Leave;
import com.zpark.sb.entity.Task;
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

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private LeaveDao leaveDao;
    @Autowired
    private FixedassetsDao fixedassetsDao;
    @Autowired
    private CheckService checkService;
    @Autowired
    private LeaveTypeService leaveTypeService;
    @Autowired
    private FixedassetTypeService fixedassetTypeService;

    public int deleteById(String id) {
        return taskDao.deleteById(id);
    }

    public int insert(Task task) {
        taskDao.insert(task);
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
        task.setType(taskTypeService.selectById(task.getTypeID()).getName());
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
            }
        }
        return taskList;
    }

    public int approval(Task task) throws ParseException {
        if (task == null || task.getTypeID() == null || task.getAdvice() == null) {
            return 1;
        }

        String type = taskTypeService.selectById(task.getTypeID()).getName();
        task.setType(type);
        Apply apply = taskDao.findByApplyID(task);
        if (apply == null) {
            return 1;
        }

        if ("yes".equals(task.getAdvice())) {
            task.setStatus("1");
            taskDao.update(task);

            if ("请假申请".equals(type)) {
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
            } else if ("固定资产购置申请".equals(type) || "固定资产报废申请".equals(type)) {
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
            }
            return 0;
        }

        if ("no".equals(task.getAdvice())) {
            task.setStatus("2");
            taskDao.update(task);
            if ("请假申请".equals(type)) {
                Leave leave = leaveDao.selectById(apply.getId());
                if (leave != null) {
                    leave.setStatus("2");
                    leaveDao.update(leave);
                }
            } else if ("固定资产购置申请".equals(type) || "固定资产报废申请".equals(type)) {
                Fixedassets fixedassets = fixedassetsDao.selectById(apply.getId());
                if (fixedassets != null) {
                    fixedassets.setStatus("2");
                    fixedassetsDao.update(fixedassets);
                }
            }
            return 0;
        }

        return 1;
    }
}
