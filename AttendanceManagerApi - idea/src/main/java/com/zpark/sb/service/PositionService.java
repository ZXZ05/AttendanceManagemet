package com.zpark.sb.service;

import com.zpark.sb.dao.DepartmentDao;
import com.zpark.sb.dao.EmployeeDao;
import com.zpark.sb.dao.PositionDao;
import com.zpark.sb.entity.Department;
import com.zpark.sb.entity.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PositionService {

    @Autowired
    private PositionDao positionDao;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private EmployeeDao employeeDao;

    public int deleteById(String id) {
        if(employeeDao.findByPositionID(id).size() != 0){
            return 1;
        }else {
            Position position = selectById(id);
            if(position != null && position.getDepartmentID() != null){
                Department department = departmentDao.selectById(position.getDepartmentID());
                if (department != null && department.getPosNum() != null && department.getPosNum() > 0) {
                    department.setPosNum(department.getPosNum()-1);
                    departmentDao.update(department);
                }
            }
            positionDao.deleteById(id);
            return 0;
        }
    }

    public int insert(Position position) {
        Position position1 = findByNumber(position.getNumber());
        if(position1 != null) {
            return 1;
        }else {
            position.setId(UUID.randomUUID().toString());
            position.setQuantity(0);
            positionDao.insert(position);
            if(position.getDepartmentID() != null){
                Department department = departmentDao.selectById(position.getDepartmentID());
                if(department != null) {

                    department.setPosNum(department.getPosNum()+1);
                    departmentDao.update(department);
                }
            }
            return 0;
        }
    }

    public Position selectById(String id) {
        return positionDao.selectById(id);
    }

    public int update(Position position) {
        return positionDao.update(position);
    }

    public Position findByNumber(String number){
        return positionDao.findByNumber(number);
    }

    public List<Position> findByDepartmentID(String departmentID){
        return positionDao.findByDepartmentID(departmentID);
    }
}
