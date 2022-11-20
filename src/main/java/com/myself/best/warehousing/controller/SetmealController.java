package com.myself.best.warehousing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myself.best.warehousing.common.R;
import com.myself.best.warehousing.dto.DishDto;
import com.myself.best.warehousing.dto.SetmealDto;
import com.myself.best.warehousing.entity.Category;
import com.myself.best.warehousing.entity.Dish;
import com.myself.best.warehousing.entity.Setmeal;
import com.myself.best.warehousing.service.CategoryService;
import com.myself.best.warehousing.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询套餐
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {

        Page<SetmealDto> pageDtoInfo = new Page<>(page, pageSize);
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        wrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);

        setmealService.page(pageInfo, wrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records");

        List<Setmeal> setmealList = pageInfo.getRecords();

        List<SetmealDto> dtoList = setmealList.stream().map((setmeal) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());

        pageDtoInfo.setRecords(dtoList);

        return R.success(pageDtoInfo);
    }

}
