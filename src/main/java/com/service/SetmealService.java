package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.SetmealDto;
import com.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐信息及对应的套餐-菜品关系
     * @param setmealDto
     */
    public void insertWithSetmealDish(SetmealDto setmealDto);

    /**
     * 通过id查询套餐信息及对应套餐-菜品关系
     * @param id
     */
    public SetmealDto selectById(Long id);


    /**
     * 通过删除套餐信息及对应套餐-菜品关系
     * @param ids
     */
    public void deleteWithDish(List<Long> ids);

}
