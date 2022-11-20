package com.myself.best.warehousing.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myself.best.warehousing.entity.Category;

import java.io.Serializable;


public interface CategoryService extends IService<Category> {

    /**
     * 根据删除分类，删除之前需要进行判断。判断分类下有无关联菜品
     * @param id
     */
    void remove(Long id);
}
