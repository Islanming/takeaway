package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.SetmealDto;
import com.entity.Category;
import com.entity.Setmeal;
import com.entity.SetmealDish;
import com.service.CategoryService;
import com.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealDishController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐及对应套餐-菜品关系
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> insertWithSetmealDish(@RequestBody SetmealDto setmealDto){
        System.out.println(setmealDto.toString());
        setmealService.insertWithSetmealDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<SetmealDto> records = setmealPage.getRecords().stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Category category = categoryService.getById(setmealDto.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(records);
        return R.success(setmealDtoPage);

    }

    /**
     * 通过id查询套餐信息及对应套餐-菜品关系
     * 修改套餐信息时的数据回显需要调用
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> selectById(@PathVariable Long id){
        return R.success(setmealService.selectById(id));
    }


    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.deleteWithDish(ids);
        return R.success("删除套餐成功");
    }

}
