package com.myself.best.warehousing.dto;


import com.myself.best.warehousing.entity.OrderDetail;
import com.myself.best.warehousing.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;//收货人

    private List<OrderDetail> orderDetails;
	
}
