package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CustomException;
import com.dto.SetmealDto;
import com.entity.Setmeal;
import com.entity.SetmealDish;
import com.mapper.SetmealMapper;
import com.service.SetmealDishService;
import com.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 */
@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐信息及对应的套餐-菜品关系
     *
     * @param setmealDto
     */
    @Override

    public void insertWithSetmealDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> list = setmealDto.getSetmealDishes().stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(list);
    }

    /**
     * 通过id查询套餐信息及对应套餐-菜品关系
     *
     * @param id
     */
    @Override
    public SetmealDto selectById(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 通过删除套餐信息及对应套餐-菜品关系
     *
     * @param ids
     */
    @Override
    public void deleteWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        long count = this.count(setmealLambdaQueryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        this.removeBatchByIds(ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper);
    }


}
