package com.gin.pixivcrawler.service.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 基础业务
 *
 * @author bx002
 * @date 2021/2/2 13:52
 */
public interface BaseService<T> extends BaseSelectService<T>,BaseInsertService<T>,BaseUpdateService<T> {

}
