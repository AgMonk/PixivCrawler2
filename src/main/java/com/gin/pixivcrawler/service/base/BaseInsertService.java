package com.gin.pixivcrawler.service.base;

import java.util.Collection;

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
    /**
     * 保存多个实体
     * @param entities 多个实体
     * @return boolean
     * @author bx002
     * @date 2021/2/2 14:52
     */
    boolean saveList(Collection<T> entities);
}
