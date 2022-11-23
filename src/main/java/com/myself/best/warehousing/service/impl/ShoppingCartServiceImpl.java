package com.myself.best.warehousing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myself.best.warehousing.entity.ShoppingCart;
import com.myself.best.warehousing.mapper.ShoppingCartMapper;
import com.myself.best.warehousing.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
