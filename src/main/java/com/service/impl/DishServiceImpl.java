package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.DishDto;
import com.entity.Dish;
import com.entity.DishFlavor;
import com.mapper.DishMapper;
import com.service.DishFlavorService;
import com.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.datatransfer.DataFlavor;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 */
@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Override
    public void insertWithFlavor(DishDto dishDto) {
        //新增菜品信息
        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 查询菜品信息及其对应口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto selectByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 更新菜品信息及其对应口味信息
     *
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        Long dishId = dishDto.getId();

        //先清除口味表中的对应信息，因为修改后口味信息的数目可能会发生改变
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);


        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //原本对应的口味信息删除了，直接添加即可
        dishFlavorService.saveBatch(flavors);
    }
}
