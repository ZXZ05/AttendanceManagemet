package com.zpark.sb.service;

import com.zpark.sb.dao.CheckDao;
import com.zpark.sb.entity.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CheckService {

    private static final Logger log = LoggerFactory.getLogger(CheckService.class);
    private static final String NORMAL = "正常";
    private static final String LATE = "迟到";
    private static final String LEAVE_EARLY = "早退";

    @Autowired
    private CheckDao checkDao;

    @Value("${attendance.on-time}")
    private String onTime;

    @Value("${attendance.off-time}")
    private String offTime;

    public int deleteById(String id) {
        return checkDao.deleteById(id);
    }

    public int insert(Check check) {
        return checkDao.insert(check);
    }

    public int checkOn(Check check) throws ParseException {
        if (check == null || ObjectUtils.isEmpty(check.getEmployeeID()) || check.getCheckOnTime() == null) {
            return 0;
        }
        check.setId(UUID.randomUUID().toString());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        check.setDate(dateFormatter.format(check.getCheckOnTime()));
        Date checkInTime = timeFormatter.parse(timeFormatter.format(check.getCheckOnTime()));

        Date standardTime;
        try {
            standardTime = timeFormatter.parse(onTime + ":00");
        } catch (ParseException e) {
            log.warn("Invalid attendance.on-time config: {}, fallback to 08:30", onTime);
            standardTime = timeFormatter.parse("08:30:00");
        }
        check.setCheckOnStatus(checkInTime.before(standardTime) ? NORMAL : LATE);
        checkDao.insert(check);
        return 0;
    }

    public int checkOff(Check check) throws ParseException {
        if (check == null || ObjectUtils.isEmpty(check.getEmployeeID()) || check.getCheckOffTime() == null) {
            return 0;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        String date = dateFormatter.format(check.getCheckOffTime());

        Check query = new Check();
        query.setEmployeeID(check.getEmployeeID());
        query.setDate(date);
        Check record = findByNumberAndDate(query);
        if (record == null) {
            record = new Check();
            record.setId(UUID.randomUUID().toString());
            record.setEmployeeID(check.getEmployeeID());
            record.setDate(date);
        }

        Date checkOffTime = timeFormatter.parse(timeFormatter.format(check.getCheckOffTime()));
        Date standardTime;
        try {
            standardTime = timeFormatter.parse(offTime + ":00");
        } catch (ParseException e) {
            log.warn("Invalid attendance.off-time config: {}, fallback to 17:30", offTime);
            standardTime = timeFormatter.parse("17:30:00");
        }

        record.setCheckOffStatus(checkOffTime.after(standardTime) ? NORMAL : LEAVE_EARLY);
        record.setCheckOffTime(check.getCheckOffTime());
        if (findByNumberAndDate(query) == null) {
            checkDao.insert(record);
        } else {
            checkDao.update(record);
        }
        return 0;
    }

    public int getCheckOn(Check check) {
        if (check == null || check.getCheckOnTime() == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        check.setDate(sdf.format(check.getCheckOnTime()));
        Check check1 = findByNumberAndDate(check);
        if (check1 != null) {
            if (check1.getCheckOnTime() != null) {
                return 1;
            } else if (check1.getRemarks() != null) {
                return 2;
            }
        }
        return 0;
    }

    public int getCheckOff(Check check) {
        if (check == null || check.getCheckOffTime() == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        check.setDate(sdf.format(check.getCheckOffTime()));
        Check check1 = findByNumberAndDate(check);
        if (check1 != null && check1.getCheckOffTime() != null) {
            return 1;
        }
        return 0;
    }

    public Check selectById(String id) {
        return checkDao.selectById(id);
    }

    public int update(Check check) {
        return checkDao.update(check);
    }

    public List<Check> findByNumber(String number) {
        return checkDao.findByNumber(number);
    }

    public List<Check> findByMonth(String month) {
        return checkDao.findByMonth(month);
    }

    public List<Check> findByNumberAndMonth(Check check) {
        return checkDao.findByNumberAndMonth(check);
    }

    public Check findByNumberAndDate(Check check) {
        return checkDao.findByNumberAndDate(check);
    }

    public int getWorkDay(String date) {
        String y = date.substring(0, 4);
        String m = date.substring(5, 7);
        int year = Integer.parseInt(y);
        int month = Integer.parseInt(m);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);

        int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int start = 1;
        int count = 0;
        while (start <= max) {
            c.set(Calendar.DAY_OF_MONTH, start);
            if (isWorkDay(c)) {
                count++;
            }
            start++;
        }
        return count;
    }

    public boolean isWorkDay(Calendar c) {
        int week = c.get(Calendar.DAY_OF_WEEK);
        return week != Calendar.SUNDAY && week != Calendar.SATURDAY;
    }

    public List<Check> getCheckDay(Check check) {
        check.setDate(check.getDate().substring(0, 7));
        return checkDao.getCheckDay(check);
    }

    public List<Check> getLateDay(Check check) {
        check.setDate(check.getDate().substring(0, 7));
        return checkDao.getLateDay(check);
    }

    public List<Check> getLeaveEarlyDay(Check check) {
        check.setDate(check.getDate().substring(0, 7));
        return checkDao.getLeaveEarlyDay(check);
    }

    public int getCheckDayNumber(Check check) {
        return getCheckDay(check).size();
    }

    public int getLateDayNumber(Check check) {
        return getLateDay(check).size();
    }

    public int getLeaveEarlyDayNumber(Check check) {
        return getLeaveEarlyDay(check).size();
    }

    public List<Check> getLeaveDay(Check check) {
        return checkDao.getLeaveDay(check);
    }

    public int getLeaveDayNumber(Check check, String leaveType) {
        int days = 0;
        List<Check> checkList = getLeaveDay(check);
        for (Check item : checkList) {
            if (item.getRemarks() != null && leaveType.equals(item.getRemarks())) {
                days = item.getDays();
            }
        }
        return days;
    }

    public int getAllLeaveDay(Check check) {
        int days = 0;
        List<Check> checkList = getLeaveDay(check);
        for (Check item : checkList) {
            if (item.getRemarks() != null) {
                days += item.getDays();
            }
        }
        return days;
    }
}
