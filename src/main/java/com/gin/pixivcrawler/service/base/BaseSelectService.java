package com.gin.pixivcrawler.service.base;

import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivErrorException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author bx002
 * @date 2021/2/2 13:58
 */
public interface BaseSelectService<T> {
    /**
     * 查询一个实体
     *
     * @param id id
     * @return T 实体
     * @author bx002
     * @date 2021/2/2 13:54
     */
    T findOne(Serializable id) throws PixivErrorException;

    /**
     * 查询多个实体
     *
     * @param idCollection id集合
     * @return java.util.List<T>
     * @author bx002
     * @date 2021/2/2 13:54
     */
    List<T> findList(Collection<Serializable> idCollection);
}
