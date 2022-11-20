package com.myself.best.warehousing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myself.best.warehousing.common.R;
import com.myself.best.warehousing.dto.DishDto;
import com.myself.best.warehousing.entity.Category;
import com.myself.best.warehousing.entity.Dish;
import com.myself.best.warehousing.entity.DishFlavor;
import com.myself.best.warehousing.service.CategoryService;
import com.myself.best.warehousing.service.DishFlavorService;
import com.myself.best.warehousing.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        wrapper.orderByAsc(Dish::getSort);//排序

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
}
