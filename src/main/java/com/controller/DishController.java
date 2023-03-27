package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.DishDto;
import com.entity.Category;
import com.entity.Dish;
import com.entity.DishFlavor;
import com.service.CategoryService;
import com.service.DishFlavorService;
import com.service.DishService;
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
@Slf4j
@RestController
@RequestMapping("/dish")
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
    public R<String> insert(@RequestBody DishDto dishDto){
        dishService.insertWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }



    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);

        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,dishLambdaQueryWrapper);


        Page<DishDto> dtoPage = new Page<>();

        //拷贝pageInfo到dtoPage中去，除了records，里面存放的是查询到的列表信息，因为还差一个菜品分类名的属性
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        //里面存放的是查询到的列表信息
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            //拷贝遍历到的records中的项到dishDto中
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = dishDto.getCategoryId();

            Category category = categoryService.getById(categoryId);

            dishDto.setCategoryName(category.getName());

            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);

    }

    /**
     * 根据id查询菜品信息
     * 用于修改页面的数据回填
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> selectById(@PathVariable Long id){
        return R.success(dishService.selectByIdWithFlavor(id));
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品信息成功");
    }


    @GetMapping("/list")
    public R<List<Dish>> selectByCategoryId(Dish dish){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
        return R.success(dishList);
    }


}
