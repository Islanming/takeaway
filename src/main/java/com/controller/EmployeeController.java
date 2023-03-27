package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.entity.Employee;
import com.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author Lenovo
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request 登录成功后会将员工id传到session，这时可以用request获取到session里面的员工id
     * @param employee 账号和密码封装成Employee类传回后端
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //查出用户名和密码都正确的项
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername()).eq(Employee::getPassword,password);
        Employee employeeServiceOne = employeeService.getOne(queryWrapper);

        //若查询结果为空则说明用户不存在 或 用户名和密码不匹配
        if(employeeServiceOne == null){
            return R.error("用户名或密码不正确，请重新输入");
        }

        //检查账号状态
        if(employeeServiceOne.getStatus()==0){
            return R.error("该账号已被禁用");
        }

        //登录成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee",employeeServiceOne.getId());
        return R.success(employeeServiceOne);
    }

    /**
     * 员工退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前员工的登录id
        request.getSession().removeAttribute("employee");
        return R.success("退出登录成功");
    }

    /**
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> insert(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息：{}",employee.toString());
        //设置初始密码123456，并加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //获取当前用户的ID
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        boolean save = employeeService.save(employee);
        if(save){
            return R.success("操作成功！");
        }
        return R.error("操作失败！");
    }


    /**
     * 分页查询，查询
     * @param page 第几页
     * @param pageSize 一页多少条信息
     * @param name 查询名字
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        //添加查询条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 更新员工信息
     * 更改员工账号状态时用到
     * 也是编辑员工信息操作的第二步
     *
     * @param request
     * @param employee 其中的id在前端的js对long型数据进行处理时会丢失精度，与数据库中的id会不一样，会使操作失败
     *                 故需要在响应时将long统一转换为字符串，使用消息转换器实现
     *                 （1）提供对象转换器JacksonObjectMapper，基于jackson将Java对象转为json
     *                 （2）在SpringMvcConfig配置类中拓展SpringMVC的消息转换器，提供上面的对象转换器
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
        long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);
        boolean update = employeeService.updateById(employee);
        if(update){
            return R.success("操作成功");
        }
        return R.error("操作失败");
    }

    /**
     * 根据id查询员工信息
     * 编辑员工信息操作中的第一步
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> selectById(@PathVariable Long id){
        log.info("根据id查询员工信息......");
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }



}
