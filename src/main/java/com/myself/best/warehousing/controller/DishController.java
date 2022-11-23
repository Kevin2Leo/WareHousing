package com.myself.best.warehousing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myself.best.warehousing.common.R;
import com.myself.best.warehousing.dto.DishDto;
import com.myself.best.warehousing.entity.Category;
import com.myself.best.warehousing.entity.Dish;
import com.myself.best.warehousing.entity.DishFlavor;
import com.myself.best.warehousing.entity.Setmeal;
import com.myself.best.warehousing.service.CategoryService;
import com.myself.best.warehousing.service.DishFlavorService;
import com.myself.best.warehousing.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        dishService.saveDishWithFlavors(dishDto);
        return R.success("菜品新增成功");
    }

    /**
     * 分页查询菜品
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {

        Page<DishDto> pageDtoInfo = new Page<>(page, pageSize);
        Page<Dish> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        wrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);//排序

        dishService.page(pageInfo, wrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records");

        List<Dish> dishList = pageInfo.getRecords();

        List<DishDto> dtoList = dishList.stream().map((dish) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());

        pageDtoInfo.setRecords(dtoList);

        return R.success(pageDtoInfo);
    }

    /**
     * 根据id查询菜品详情信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> dish(@PathVariable("id") Long id) {

        DishDto dishDto = dishService.getByIdWithFlavors(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavors(dishDto);

        return R.success("菜品修改成功");
    }

    /**
     * 根据条件查询菜品信息
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        //构造查询条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .like(dish.getName() != null, Dish::getName, dish.getName())
                .eq(dish.getStatus() != null, Dish::getStatus, dish.getStatus())//status==1 起售
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(wrapper);
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish dish1 : dishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);//拷贝一份

            Long dishId = dish1.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavors);

            dishDtoList.add(dishDto);
        }

        return R.success(dishDtoList);
    }

    /**
     * 启售、停售、批量启售、批量停售 菜品
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {

        //update dish set status = 0 where id in (1,2,3);
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus, status)
                     .in(Dish::getId, ids);

        dishService.update(updateWrapper);
        return R.success("修改菜品售卖状态成功");
    }



}
