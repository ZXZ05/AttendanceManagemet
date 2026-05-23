package com.zpark.sb.service;

import com.zpark.sb.dao.LeaveDao;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.Leave;
import com.zpark.sb.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LeaveService {

    @Autowired
    private LeaveDao leaveDao;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private EmployeeService employeeService;

    public int deleteById(String id) {
        return leaveDao.deleteById(id);
    }

    public int insert(Leave leave) {
        String applyId = UUID.randomUUID().toString();
        leave.setId(applyId);
        String taskId = UUID.randomUUID().toString();
        leave.setApprovalID(taskId);
        leave.setStatus("0");
        leaveDao.insert(leave);

        Task task = new Task();
        task.setId(taskId);
        task.setApplyID(applyId);
        task.setApplyTime(leave.getApplyTime());
        task.setTypeID(leave.getTaskTypeID());
        task.setApplyNumber(leave.getApplyNumber());
        String typeName = taskTypeService.selectById(leave.getTaskTypeID()).getName();
        task.setName(leave.getApplyName() + "的" + typeName);
        task.setStatus("0");

        Employee applicant = employeeService.findByNumber(leave.getApplyNumber());
        if (applicant == null) {
            return 2;
        }
        Employee leader = employeeService.getLeader(applicant);
        if (leader == null) {
            return 2;
        }
        task.setReceiveNumber(leader.getNumber());
        taskService.insert(task);
        return 0;
    }

    public Leave selectById(String id) {
        return leaveDao.selectById(id);
    }

    public int update(Leave leave) {
        return leaveDao.update(leave);
    }

    public List<Leave> findByEmployeeNumber(String number) {
        List<Leave> leaveList = leaveDao.findByEmployeeNumber(number);
        for (Leave item : leaveList) {
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
        return leaveList;
    }
}
