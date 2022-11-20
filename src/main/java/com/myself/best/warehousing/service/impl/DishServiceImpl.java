package com.myself.best.warehousing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myself.best.warehousing.dto.DishDto;
import com.myself.best.warehousing.entity.Category;
import com.myself.best.warehousing.entity.Dish;
import com.myself.best.warehousing.entity.DishFlavor;
import com.myself.best.warehousing.mapper.DishMapper;
import com.myself.best.warehousing.service.CategoryService;
import com.myself.best.warehousing.service.DishFlavorService;
import com.myself.best.warehousing.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表: dish、dish_flavor
    @Override
    @Transactional //事务控制
    public void saveDishWithFlavors(DishDto dishDto) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        //保存菜品的基本信息到菜品表dish
        this.save(dish);

        //获取dish_id
        Long dishId = dish.getId();
        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : dishFlavors) {
            dishFlavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(dishFlavors);
    }

    //根据id查询菜品信息和对应的口味信息
    @Override
    public DishDto getByIdWithFlavors(Long id) {

        DishDto dishDto = new DishDto();

        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);

        Category category = categoryService.getById(dish.getCategoryId());
        dishDto.setCategoryName(category.getName());

        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);
        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    //更新菜品，同时更新菜品对应的口味数据
    @Override
    @Transactional
    public void updateWithFlavors(DishDto dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        //更新dish表
        this.updateById(dish);

        //对于dish_flavor表，可以先清除 后添加
        Long dishId = dish.getId();
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(wrapper);//先删除

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);//再重新新增
    }
}
