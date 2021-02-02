package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivTagDao;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author bx002
 * @date 2021/2/2 14:35
 */
@Service
@Slf4j
public class PixivTagServiceImpl extends ServiceImpl<PixivTagDao,PixivTag> implements PixivTagService {
    @Override
    public boolean saveList(Collection<PixivTag> entities) {
        return saveBatch(entities);
    }

    @Override
    public boolean saveOne(PixivTag entity) {
        return save(entity);
    }

    @Override
    public PixivTag findOne(Serializable id) {
        return getById(id);
    }

    @Override
    public List<PixivTag> findList(Collection<Serializable> idCollection) {
        return listByIds(idCollection);
    }

    @Override
    public boolean updateOne(PixivTag entity) {
        return updateById(entity);
    }
}
