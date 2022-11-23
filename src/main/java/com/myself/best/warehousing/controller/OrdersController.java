package com.myself.best.warehousing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myself.best.warehousing.common.BaseContext;
import com.myself.best.warehousing.common.R;
import com.myself.best.warehousing.dto.OrdersDto;
import com.myself.best.warehousing.entity.OrderDetail;
import com.myself.best.warehousing.entity.Orders;
import com.myself.best.warehousing.service.OrderDetailService;
import com.myself.best.warehousing.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders 订单数据
     * @return 字符串
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 订单详情
     *
     * @param page 页码
     * @param pageSize 每页显示的条数
     * @return Page数据
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(Integer page, Integer pageSize) {
        //获得当前用户的id
        Long userId = BaseContext.getCurrentId();

        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> pageDtoInfo = new Page<>(page, pageSize);

        //获得当前用户的订单数据
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        ordersService.page(pageInfo, wrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records");

        List<Orders> ordersList = pageInfo.getRecords();

        List<OrdersDto> ordersDtoList = new ArrayList<>();

        for (Orders orders : ordersList) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);//拷贝一份

            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, orders.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
            ordersDto.setOrderDetails(orderDetails);
            ordersDtoList.add(ordersDto);
        }
        pageDtoInfo.setRecords(ordersDtoList);
        return R.success(pageDtoInfo);
    }

    /**
     * 后台管理页面展示
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        //1.构造分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        //添加一个number条件(订单号) 可能为空
        wrapper.like(number != null, Orders::getNumber, number);

        //添加时间条件
        wrapper.between(beginTime != null || endTime != null, Orders::getOrderTime, beginTime, endTime);
        //3.执行查询
        ordersService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> order(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("派送成功");
    }
}
