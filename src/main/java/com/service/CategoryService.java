package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.Category;

/**
 * 分类
 * @author Lenovo
 */
public interface CategoryService extends IService<Category> {
    /**
     * 判断是否可以删除+逻辑删除
     * @param id
     */
    public void remove(Long id);

}
