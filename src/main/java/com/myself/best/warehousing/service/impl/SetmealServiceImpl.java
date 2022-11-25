package com.myself.best.warehousing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myself.best.warehousing.common.CustomException;
import com.myself.best.warehousing.dto.SetmealDto;
import com.myself.best.warehousing.entity.Category;
import com.myself.best.warehousing.entity.Dish;
import com.myself.best.warehousing.entity.Setmeal;
import com.myself.best.warehousing.entity.SetmealDish;
import com.myself.best.warehousing.mapper.SetmealMapper;
import com.myself.best.warehousing.service.CategoryService;
import com.myself.best.warehousing.service.SetmealDishService;
import com.myself.best.warehousing.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐，同时插入套餐对应的菜品数据，需要操作两张表: setmeal、setmeal_dish
     * @param setmealDto
     */
    @Override
    @Transactional //事务控制
    public void saveSetmealWithDish(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        //保存套餐的基本信息到套餐表 setmeal
        this.save(setmeal);

        //获取setmeal_id
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeSetmealWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1, 2, 3) and status = 1
        //查询套餐状态，确定是否可用删除(是否是'启售'状态，如果是 就无法删除)
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId, ids)
                .eq(Setmeal::getStatus, 1);//status==1 '启售'状态
        int count = this.count(wrapper);
        //如果不能删除，抛出一个业务异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐setmeal表中的数据
        this.removeByIds(ids);
        //再删除关系表中setmeal_dish的数据
        //delete from setmeal_dish where setmeal_id in (1, 2, 3)
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);
    }

    /**
     * 根据id查询套餐详情信息
     *
     * @param id
     */
    @Override
    public SetmealDto getByIdWithDishs(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);//拷贝数据

        Category category = categoryService.getById(setmeal.getCategoryId());
        setmealDto.setCategoryName(category.getName());

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishs = setmealDishService.list(wrapper);

        setmealDto.setSetmealDishes(setmealDishs);

        return setmealDto;
    }

    /**
     * 更新套餐表setmeal，同时更新菜品对应的setmeal_dish表
     *
     * @param setmealDto
     */
    @Override
    @Transactional //更新多张表，添加事务控制
    public void updateWithSetmealDish(SetmealDto setmealDto) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);//拷贝数据
        this.updateById(setmeal);

        //对于setmeal_dish表，可以先清除 后添加
        Long setmealId = setmeal.getId();
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(wrapper);//先删除

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDishes);//再重新新增

    }


}
