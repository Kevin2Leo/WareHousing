package com.myself.best.warehousing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.myself.best.warehousing.common.BaseContext;
import com.myself.best.warehousing.common.R;
import com.myself.best.warehousing.entity.ShoppingCart;
import com.myself.best.warehousing.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 购物车管理
 * @Date Created in 23:20 2022/11/21
 * @Author: Chen_zhuo
 * @Modified By
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查看当前用户的购物车
     *
     * @return
     */
    @GetMapping("list")
    public R<List<ShoppingCart>> list() {
        //获取当前用户id (user_id)
        Long userId = BaseContext.getCurrentId();

        //根据user_id 查询购物车信息
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
                .orderByAsc(ShoppingCart::getCreateTime);//按创建时间排序
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);
        return R.success(shoppingCartList);
    }

    /**
     * 新增购物车 或者 商品+1
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {

        //设置user_id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);//设置user_id
        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        if (dishId != null && setmealId == null) {//说明新增的是菜品dish
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }

        if (dishId == null && setmealId != null) {//说明新增的是套餐setmeal
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart shoppingCartEntity = shoppingCartService.getOne(queryWrapper);

        if (shoppingCartEntity != null) {
            //如果已经存在，就在原来数量number基础上 +1
            Integer number = shoppingCartEntity.getNumber();
            shoppingCartEntity.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartEntity);
        } else {
            //如果不存在，则添加到购物车，数量number默认就是 1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartEntity = shoppingCart;
        }
        return R.success(shoppingCartEntity);
    }

    /**
     * 购物车 商品-1
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //设置用户userId ,指定当前是哪个用户的购物车
        Long currentId = BaseContext.getCurrentId();

        //查询当前增加的菜品或者套餐 是否在购物车当中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if (dishId != null){//说明减少的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else {//说明减少的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart shoppingCart2 = shoppingCartService.getOne(queryWrapper);
        if (shoppingCart2.getNumber() > 1){//如果数量大于1, 则将总数量-1
            Integer number = shoppingCart2.getNumber();
            shoppingCart2.setNumber(number - 1);
            shoppingCartService.updateById(shoppingCart2);
        }else {//如果数量等于1, 则将此条数据删除

            shoppingCartService.remove(queryWrapper);
            shoppingCart2.setNumber(0);
        }
        return R.success(shoppingCart2);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        //返回响应数据
        return R.success("清空完毕");
    }
}
