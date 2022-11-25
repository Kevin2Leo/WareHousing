package com.myself.best.warehousing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myself.best.warehousing.common.R;
import com.myself.best.warehousing.dto.DishDto;
import com.myself.best.warehousing.dto.SetmealDto;
import com.myself.best.warehousing.entity.Category;
import com.myself.best.warehousing.entity.Dish;
import com.myself.best.warehousing.entity.Setmeal;
import com.myself.best.warehousing.entity.SetmealDish;
import com.myself.best.warehousing.service.CategoryService;
import com.myself.best.warehousing.service.SetmealDishService;
import com.myself.best.warehousing.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 修改套餐信息
     * @param setmealDto
     */
    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true) //清理所有名叫setmealCache的缓存
    public R<String> update(@RequestBody SetmealDto setmealDto) {

        setmealService.updateWithSetmealDish(setmealDto);
        return R.success("菜品修改成功");
    }

    /**
     * 根据id查询套餐详情信息
     * @param id
     */
    @GetMapping("/{id}")
    public R<SetmealDto> dish(@PathVariable("id") Long id) {

        SetmealDto setmealDto = setmealService.getByIdWithDishs(id);
        return R.success(setmealDto);
    }

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
        wrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);//添加排序条件
        //进行分页查询
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

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true) //清理所有名叫setmealCache的缓存
    public R<String> add(@RequestBody SetmealDto setmealDto) {

        setmealService.saveSetmealWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 删除、批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true) //清理所有名叫setmealCache的缓存
    public R<String> delete(@RequestParam List<Long> ids) {

        setmealService.removeSetmealWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 启售、停售、批量启售、批量停售 套餐
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {

        log.info("status:{}, ids:{}", status, ids);

        //update setmeal set status = 0 where id in (1,2,3);
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Setmeal::getStatus, status)
                     .in(Setmeal::getId, ids);

        setmealService.update(updateWrapper);
        return R.success("修改售卖状态成功");
    }

    /**
     * 根据条件查询套餐信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<SetmealDto>> list(Setmeal setmeal) {

        //构造查询条件
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(Setmeal::getStatus, setmeal.getStatus())//status==1 起售
                .orderByDesc(Setmeal::getUpdateTime);//排序

        List<Setmeal> setmealList = setmealService.list(wrapper);

        //利用Stream流的形式，将集合转化
        List<SetmealDto> setmealDtoList = setmealList.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);//对象拷贝
            //获取category_id
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());//添加 categoryName

            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, item.getId());
            List<SetmealDish> setmealDishs = setmealDishService.list(queryWrapper);

            setmealDto.setSetmealDishes(setmealDishs);
            return setmealDto;
        }).collect(Collectors.toList());

        return R.success(setmealDtoList);
    }
}
