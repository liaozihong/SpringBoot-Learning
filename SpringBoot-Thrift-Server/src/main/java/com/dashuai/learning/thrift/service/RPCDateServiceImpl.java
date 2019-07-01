package com.dashuai.learning.thrift.service;

import org.apache.thrift.TException;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Rpc date service
 * <p/>
 * Created in 2019.06.28
 * <p/>
 *
 * @author Liaozihong
 */
@Controller
public class RPCDateServiceImpl implements RPCDateService.Iface {
    /**
     * 模拟存储
     */
    private Map<Integer, Student> studentMap = new HashMap<>();

    @Override
    public String getDate(String userName) throws TException {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("今天是yyyy年MM月dd日 E kk点mm分");
        String nowTime = simpleDateFormat.format(date);
        return "Hello " + userName + "\n" + nowTime;
    }


    @Override
    public boolean postStudent(Student student) throws TException {
        try {
            studentMap.put(student.getUserId(), student);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Student getStudent(int userId) throws TException {
        return studentMap.get(userId);
    }
}
