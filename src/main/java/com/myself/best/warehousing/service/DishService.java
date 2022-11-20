package com.myself.best.warehousing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myself.best.warehousing.dto.DishDto;
import com.myself.best.warehousing.entity.Dish;


public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表: dish、dish_flavor
    void saveDishWithFlavors(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavors(Long id);

    //更新菜品，同时更新菜品对应的口味数据
    void updateWithFlavors(DishDto dishDto);
}
