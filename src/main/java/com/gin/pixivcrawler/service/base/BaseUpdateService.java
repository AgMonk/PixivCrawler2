package com.gin.pixivcrawler.service.base;

/**
 * @author bx002
 * @date 2021/2/2 13:59
 */
public interface BaseUpdateService<T> {
    /**
     * 更新一个实体
     * @param entity     实体
     * @return boolean
     * @author bx002
     * @date 2021/2/2 14:08
     */
    boolean updateOne(T entity);
}
