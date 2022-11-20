package com.myself.best.warehousing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myself.best.warehousing.entity.Setmeal;
import com.myself.best.warehousing.mapper.SetmealMapper;
import com.myself.best.warehousing.service.SetmealService;
import org.springframework.stereotype.Service;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
                                implements SetmealService {
}
