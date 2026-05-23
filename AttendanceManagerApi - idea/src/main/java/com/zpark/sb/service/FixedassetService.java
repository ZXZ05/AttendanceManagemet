package com.zpark.sb.service;

import com.zpark.sb.dao.FixedassetsDao;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.Fixedassets;
import com.zpark.sb.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FixedassetService {

    @Autowired
    private FixedassetsDao fixedassetsDao;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private EmployeeService employeeService;

    public int deleteById(String id) {
        return fixedassetsDao.deleteById(id);
    }

    public int insert(Fixedassets fixedassets) {
        Fixedassets existed = findByNumber(fixedassets.getNumber());
        if (existed != null) {
            return 1;
        }

        String applyId = UUID.randomUUID().toString();
        fixedassets.setId(applyId);
        Employee applicant = employeeService.findByNumber(fixedassets.getEmployeeNumber());
        if (applicant == null) {
            return 2;
        }

        if ("3".equals(applicant.getType())) {
            fixedassets.setStatus("1");
            fixedassets.setApprovalID(applicant.getId());
            fixedassetsDao.insert(fixedassets);
            return 0;
        }

        String taskId = UUID.randomUUID().toString();
        fixedassets.setApprovalID(taskId);
        fixedassets.setStatus("0");
        fixedassetsDao.insert(fixedassets);

        Task task = new Task();
        task.setId(taskId);
        task.setApplyID(applyId);
        task.setApplyTime(fixedassets.getApplyTime());
        task.setTypeID(fixedassets.getTaskTypeID());
        task.setApplyNumber(fixedassets.getEmployeeNumber());
        String typeName = taskTypeService.selectById(fixedassets.getTaskTypeID()).getName();
        task.setName(fixedassets.getEmployeeName() + "的" + typeName);
        task.setStatus("0");
        Employee leader = employeeService.getLeader(applicant);
        if (leader == null) {
            return 2;
        }
        task.setReceiveNumber(leader.getNumber());
        taskService.insert(task);
        return 0;
    }

    public Fixedassets selectById(String id) {
        return fixedassetsDao.selectById(id);
    }

    public int update(Fixedassets fixedassets) {
        return fixedassetsDao.update(fixedassets);
    }

    public List<Fixedassets> getAll() {
        return fixedassetsDao.getAll();
    }

    public Fixedassets findByNumber(String number) {
        return fixedassetsDao.findByNumber(number);
    }

    public List<Fixedassets> findByEmployeeNumber(String number) {
        List<Fixedassets> fixedassetsList = fixedassetsDao.findByEmployeeNumber(number);
        for (Fixedassets item : fixedassetsList) {
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
        return fixedassetsList;
    }

    public List<Fixedassets> getRoomList() {
        return fixedassetsDao.getRoomList();
    }
}
