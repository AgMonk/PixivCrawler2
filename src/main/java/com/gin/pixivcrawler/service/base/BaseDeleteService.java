package com.gin.pixivcrawler.service.base;

import java.io.Serializable;

/**
 * @author bx002
 * @date 2021/2/2 13:58
 */
public interface BaseDeleteService<T> {
    /**
     * 按id删除
     *
     * @param id id
     * @return boolean
     * @author bx002
     * @date 2021/2/5 11:36
     */
    boolean deleteById(Serializable id);
}
