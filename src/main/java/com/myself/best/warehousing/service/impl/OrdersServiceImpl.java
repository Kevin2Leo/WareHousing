package com.myself.best.warehousing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.myself.best.warehousing.common.BaseContext;
import com.myself.best.warehousing.entity.*;
import com.myself.best.warehousing.mapper.OrdersMapper;
import com.myself.best.warehousing.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date Created in 21:17 2022/5/24
 * @Author: Chen_zhuo
 * @Modified By
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Transactional //涉及到多张表 添加事务管理
    @Override
    public void submit(Orders orders) {
        //获得当前用户的id
        Long userId = BaseContext.getCurrentId();
        //获得当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        long orderId = IdWorker.getId();//生成一个订单id
        AtomicInteger amount = new AtomicInteger(0);//原子操作, 保证线程安全
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());//求总金额
            return orderDetail;
        }).collect(Collectors.toList());

        //查询用户数据
        User user = userService.getById(userId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        //向orders表中插入 1 条数据
        orders.setId(orderId);//设置订单id
        orders.setNumber(String.valueOf(orderId));//设置订单号number
        orders.setOrderTime(LocalDateTime.now());//设置订单创建时间
        orders.setCheckoutTime(LocalDateTime.now());//设置订单结账时间
        //订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
        orders.setStatus(2);//设置订单状态
        orders.setUserId(userId);//设置用户id
        orders.setAmount(new BigDecimal(amount.get()));//设置总金额，需要作加法运算
        orders.setUserName(user.getName());//设置用户名
        orders.setConsignee(addressBook.getConsignee());//设置收货人
        orders.setPhone(addressBook.getPhone());//设置收货人电话
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                +(addressBook.getCityName() == null ? "" : addressBook.getCityName())
                +(addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                +(addressBook.getDetail() == null ? "" : addressBook.getDetail()));//设置收货地址

        this.save(orders);
        //向order_detail表中插入一条或多条数据
        orderDetailService.saveBatch(orderDetails);
        //下单完成之后，要清空我们的购物车数据
        shoppingCartService.remove(wrapper);

    }
}
