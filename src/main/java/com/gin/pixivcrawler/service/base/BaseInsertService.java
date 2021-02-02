package com.gin.pixivcrawler.service.base;

/**
 * @author bx002
 * @date 2021/2/2 13:58
 */
public interface BaseInsertService<T> {
    /**
     * 保存一个实体
     * @param entity 实体
     * @return boolean
     * @author bx002
     * @date 2021/2/2 14:00
     */
    boolean saveOne(T entity);
}
