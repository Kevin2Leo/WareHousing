package com.myself.best.warehousing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myself.best.warehousing.common.R;
import com.myself.best.warehousing.entity.Employee;
import com.myself.best.warehousing.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

//        1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);
//        3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("该emp = {Employee@8273} \"Employee(id=1, username=admin, name=管理员, password=e10adc3949ba59abbe56e057f20f883e, phone=13812312312, sex=1, idNumber=110101199001010047, status=1, createTime=2021-05-06T17:20:07, updateTime=2022-05-24T18:16:54, createUser=1, updateUser=1)\"用户不存在！");
        }
//        4、密码比对，如果不一致则返回登录失败结果
        if (!password.equals(emp.getPassword())) {
            return R.error("密码错误！");
        }
//        5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() != 1) {
            return R.error("该账户已禁用！");
        }

//        6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

}
