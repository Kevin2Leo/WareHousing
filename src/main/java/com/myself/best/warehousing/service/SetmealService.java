package com.myself.best.warehousing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myself.best.warehousing.dto.SetmealDto;
import com.myself.best.warehousing.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时插入套餐对应的菜品数据，需要操作两张表: setmeal、setmeal_dish
     * @param setmealDto
     */
    void saveSetmealWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    void removeSetmealWithDish(List<Long> ids);

    /**
     * 根据id查询套餐详情信息
     * @param id
     */
    SetmealDto getByIdWithDishs(Long id);

    /**
     * 更新套餐表setmeal，同时更新菜品对应的setmeal_dish表
     * @param setmealDto
     */
    void updateWithSetmealDish(SetmealDto setmealDto);

}
