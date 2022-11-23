package com.myself.best.warehousing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myself.best.warehousing.entity.OrderDetail;
import com.myself.best.warehousing.mapper.OrderDetailMapper;
import com.myself.best.warehousing.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
