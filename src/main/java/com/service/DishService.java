package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.DishDto;
import com.entity.Dish;

/**
 * @author Lenovo
 */
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    public void insertWithFlavor(DishDto dishDto);

    /**
     * 查询菜品信息及其对应口味信息
     * @param id
     * @return
     */
    public DishDto selectByIdWithFlavor(Long id);

    /**
     * 更新菜品信息及其对应口味信息
     * @param dishDto
     */
    public void updateWithFlavor(DishDto dishDto);
}
