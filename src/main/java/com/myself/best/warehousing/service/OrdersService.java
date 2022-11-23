package com.myself.best.warehousing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myself.best.warehousing.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     *
     * @param orders
     */
    void submit(Orders orders);
}
