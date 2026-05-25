package com.zpark.sb.service;

import com.zpark.sb.dao.MeetingDao;
import com.zpark.sb.entity.Employee;
import com.zpark.sb.entity.Meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MeetingService {

    @Autowired
    private MeetingDao meetingDao;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private NotificationService notificationService;

    public int deleteById(String id) {
        return meetingDao.deleteById(id);
    }

    public int insert(Meeting meeting) {
        meeting.setId(UUID.randomUUID().toString());
        int result = meetingDao.insert(meeting);
        if (result > 0 && "1".equals(meeting.getType())) {
            List<String> receivers = new ArrayList<>();
            for (Employee employee : employeeService.getAll()) {
                if (employee.getNumber() != null) {
                    receivers.add(employee.getNumber());
                }
            }
            String beginTime = meeting.getBeginTime() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd HH:mm").format(meeting.getBeginTime());
            notificationService.createBatchNotice(
                    receivers,
                    "会议开始前提醒",
                    "会议《" + (meeting.getTitle() == null ? "-" : meeting.getTitle()) + "》将于 " + beginTime + " 开始。",
                    "MEETING_REMINDER",
                    "MEETING",
                    meeting.getId()
            );
        }
        return result;
    }

    public Meeting selectById(String id) {
        return meetingDao.selectById(id);
    }

    public int update(Meeting meeting) {
        return meetingDao.update(meeting);
    }

    public List<Meeting> getAll() {
        return meetingDao.getAll();
    }

    public List<Meeting> getAllMeeting() {
        return meetingDao.getAllMeeting();
    }

    public List<Meeting> getAllNotice() {
        return meetingDao.getAllNotice();
    }

    public List<Meeting> getNotice() {
        List<Meeting> meetingList =  meetingDao.getNotice();
        for(Meeting item : meetingList){
            if(item.getPublishTime() != null){
                SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(item.getPublishTime());
                item.setDate(date);
            }
        }
        return meetingList;
    }
}
