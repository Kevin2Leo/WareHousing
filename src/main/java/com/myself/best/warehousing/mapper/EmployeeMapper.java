package com.myself.best.warehousing.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myself.best.warehousing.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
