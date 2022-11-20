package com.myself.best.warehousing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myself.best.warehousing.common.CustomException;
import com.myself.best.warehousing.entity.Category;
import com.myself.best.warehousing.entity.Dish;
import com.myself.best.warehousing.entity.Setmeal;
import com.myself.best.warehousing.mapper.CategoryMapper;
import com.myself.best.warehousing.mapper.DishMapper;
import com.myself.best.warehousing.mapper.SetmealMapper;
import com.myself.best.warehousing.service.CategoryService;
import com.myself.best.warehousing.service.DishService;
import com.myself.best.warehousing.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据删除分类，删除之前需要进行判断。判断分类下有无关联菜品
     * @param id
     * @return
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(wrapper1);
        if (count1 > 0) {
            //已经关联菜品，需要抛出一个业务异常
            throw new CustomException("当前分类已经关联菜品, 无法删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务导常
        LambdaQueryWrapper<Setmeal> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(wrapper2);
        if (count2 > 0) {
            //已经关联套餐，需要抛出一个业务异常
            throw new CustomException("当前分类已经关联套餐, 无法删除");
        }

        //正常删除分类
        this.removeById(id);
    }
}
