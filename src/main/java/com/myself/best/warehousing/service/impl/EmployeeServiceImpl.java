package com.myself.best.warehousing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myself.best.warehousing.entity.Employee;
import com.myself.best.warehousing.mapper.EmployeeMapper;
import com.myself.best.warehousing.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
                                    implements EmployeeService {

}
