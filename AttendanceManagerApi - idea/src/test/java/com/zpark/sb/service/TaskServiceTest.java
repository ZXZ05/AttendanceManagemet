package com.zpark.sb.service;

import com.zpark.sb.dao.FixedassetsDao;
import com.zpark.sb.dao.LeaveDao;
import com.zpark.sb.dao.TaskDao;
import com.zpark.sb.entity.Apply;
import com.zpark.sb.entity.FixedassetType;
import com.zpark.sb.entity.Fixedassets;
import com.zpark.sb.entity.Task;
import com.zpark.sb.entity.TaskType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskDao taskDao;
    @Mock
    private TaskTypeService taskTypeService;
    @Mock
    private LeaveDao leaveDao;
    @Mock
    private FixedassetsDao fixedassetsDao;
    @Mock
    private CheckService checkService;
    @Mock
    private LeaveTypeService leaveTypeService;
    @Mock
    private FixedassetTypeService fixedassetTypeService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void approvalShouldUpdateFixedassetTypeQuantityWhenApproved() throws Exception {
        Task task = new Task();
        task.setTypeID("T1");
        task.setAdvice("yes");

        TaskType taskType = new TaskType();
        taskType.setName("固定资产购置申请");
        when(taskTypeService.selectById("T1")).thenReturn(taskType);

        Apply apply = new Apply();
        apply.setId("A1");
        when(taskDao.findByApplyID(task)).thenReturn(apply);

        Fixedassets fixedassets = new Fixedassets();
        fixedassets.setTypeID("FT1");
        when(fixedassetsDao.selectById("A1")).thenReturn(fixedassets);

        FixedassetType fixedassetType = new FixedassetType();
        fixedassetType.setQuantity(2);
        when(fixedassetTypeService.selectById("FT1")).thenReturn(fixedassetType);

        int result = taskService.approval(task);

        assertEquals(0, result);
        verify(fixedassetsDao).update(any(Fixedassets.class));
        ArgumentCaptor<FixedassetType> captor = ArgumentCaptor.forClass(FixedassetType.class);
        verify(fixedassetTypeService).update(captor.capture());
        assertEquals(3, captor.getValue().getQuantity());
    }
}

