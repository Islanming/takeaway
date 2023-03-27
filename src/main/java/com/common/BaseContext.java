package com.common;

/**
 * 什么是ThreadLocal?
 *     Threadlocal并 不是一个Thread，而是Thread的局部变量。当使用ThreadLoca l维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
 * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
 *      Threadlocal为每个线程提供单独一份存储空间，具有线程隔离的效果，只有在线程内才能获取到对应的值，线程外则不能访问。
 * Threadlocal常用方法：
 *      public void set(T value） 设置当前线程的线程局部变量的值
 *      public T get()  返回当前线程所对应的线程局部变量的值
 * 我们可以在LoginCheckFilter的doFilter方法中获取当前登录用户id，
 * 并调用ThreadLocal的set方法来设置当前线程的线程局部变量的值（用户id），
 * 然后在My MetaObjectHandler的updateFill方法中调用ThreadLocal的get方法来获得当前线程所对应的线程局部变量的值（用户id）。
 * @author Lenovo
 *
 * 基于封装工具类，用户保存和获取当前登录用户ID
 *
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
